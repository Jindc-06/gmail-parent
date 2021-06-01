package com.atguigu.gmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.list.*;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.search.service.SearchService;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Date 2021/5/26 11:40
 * @Author JINdc
 **/
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    ProductFeignClient productFeignClient;
//    @Autowired
//    ElasticsearchRestTemplate restTemplate;
    @Autowired
    GoodsRepository goodsRepository;
    @Autowired
    RestHighLevelClient restHighLevelClient;
    @Autowired
    RedisTemplate redisTemplate;
    @Override
    public List<JSONObject> getCategoryToIndex() {
        List<BaseCategoryView> categoryViews = productFeignClient.getCategoryToIndex();

        //一级分类属性集合
        List<JSONObject> c1JSONObjectList = new ArrayList<>();
        //处理后台查询得到的分类属性集合,通过Category1Id得到c1map元素
        Map<Long, List<BaseCategoryView>> c1Map = categoryViews.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        for (Map.Entry<Long, List<BaseCategoryView>> c1ObjectMap : c1Map.entrySet()) {
            //一级分类的元素(json对象本质是一个map集合)
            JSONObject c1JsonObject = new JSONObject();
            Long c1Id = c1ObjectMap.getKey();
            String category1Name = c1ObjectMap.getValue().get(0).getCategory1Name();
            c1JsonObject.put("categoryId",c1Id);
            c1JsonObject.put("categoryName",category1Name);

            //二级分类集合
            List<JSONObject> c2JSONObjectList = new ArrayList<>();
            //处理一级分类map元素 ,通过Category2Id得到
            Map<Long, List<BaseCategoryView>> c2Map = c1ObjectMap.getValue().stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            for (Map.Entry<Long, List<BaseCategoryView>> c2ObjectMap : c2Map.entrySet()) {
                //二级分类元素
                JSONObject c2JsonObject = new JSONObject();
                Long c2Id = c2ObjectMap.getKey();
                String category2Name = c2ObjectMap.getValue().get(0).getCategory2Name();
                c2JsonObject.put("categoryId",c2Id);
                c2JsonObject.put("categoryName",category2Name);

                //三级分类集合
                List<JSONObject> c3JSONObjectList = new ArrayList<>();
                //处理二级分类map元素 ,通过Category3Id得到
                Map<Long, List<BaseCategoryView>> c3Map = c2ObjectMap.getValue().stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory3Id));
                for (Map.Entry<Long, List<BaseCategoryView>> c3ObjectMap : c3Map.entrySet()) {
                    //三级分类元素
                    JSONObject c3JsonObject = new JSONObject();
                    Long c3Id = c3ObjectMap.getKey();
                    String category3Name = c3ObjectMap.getValue().get(0).getCategory3Name();
                    c3JsonObject.put("categoryId",c3Id);
                    c3JsonObject.put("categoryName",category3Name);
                    c3JSONObjectList.add(c3JsonObject);
                }
                //三级分类集合加入二级分类元素,作为child
                c2JsonObject.put("categoryChild",c3JSONObjectList);
                //将二级分类元素c2JsonObject添加到二级集合
                c2JSONObjectList.add(c2JsonObject);
            }
            //二级分类集合加入一级分类元素,作为child
            c1JsonObject.put("categoryChild",c2JSONObjectList);
            //将一级分类元素c1JsonObject添加到一级集合
            c1JSONObjectList.add(c1JsonObject);
        }
        return c1JSONObjectList;
    }

    @Override
    public void onSale(Long skuId) {

        //从数据库中查询,存入es中
        Goods goods = productFeignClient.getGoodsBySkuId(skuId);
        goods.setCreateTime(new Date());
        goodsRepository.save(goods);
    }

    @Override
    public void cancelSale(Long skuId) {
        //下架删除
        Goods goods = new Goods();
        goods.setId(skuId);
        goodsRepository.delete(goods);
    }

    @Override
    public SearchResponseVo list(SearchParam searchParam) {
        //封装查询语句
        SearchRequest searchRequest = getSearchRequest(searchParam);

        SearchResponse searchResponse =null;
        try {
            //执行检索
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //解析和封装返回结果
        SearchResponseVo searchResponseVo = getSearchResponseVo(searchResponse);
        return searchResponseVo;
    }

    @Override
    public void hotScore(Long skuId) {
        //先将热度值存入缓存
        Long increment = redisTemplate.opsForValue().increment("sku:" + skuId + ":hostScore", 1);
        //热度值到10 一次传递给es
        if (increment%10==0){
            Optional<Goods> optionalGoods = goodsRepository.findById(skuId);
            Goods goods = optionalGoods.get();
            goods.setHotScore(increment);
            goodsRepository.save(goods);
        }
    }

    //es查询语句封装
    SearchRequest getSearchRequest(SearchParam searchParam){
        SearchRequest searchRequest = new SearchRequest();
        //搜索范围
        searchRequest.indices("goods");//搜索索引范围
        searchRequest.types("info");//搜索表
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //es默认搜索10条数据
        searchSourceBuilder.from(0);//从第几页开始
        searchSourceBuilder.size(20);//搜索20条数据

        //检索参数(category3Id,keyword不能同时为null)
        Long category3Id = searchParam.getCategory3Id();
        String keyword = searchParam.getKeyword();
        //属性数组
        String[] props = searchParam.getProps();
        //品牌
        String trademark = searchParam.getTrademark();
        //热度排名
        String order = searchParam.getOrder();

        //复合搜索下的dsl语句封装
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        if(!StringUtils.isEmpty(category3Id)){
            //参数三级分类id
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("category3Id",category3Id);
            boolQueryBuilder.filter(termQueryBuilder);
        }
        if(!StringUtils.isEmpty(keyword)){
            //关键字搜索
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("title",keyword);
            boolQueryBuilder.must(matchQueryBuilder);
        }
        //属性嵌套封装
        if(props!= null && props.length>0){
            for (String prop : props) {
                //23:4G:运行内存  id value name
                String[] split = prop.split(":");
                String attrId = split[0];
                String attrValue = split[1];
                String attrName = split[2];
                BoolQueryBuilder propBoolQueryBuilder = new BoolQueryBuilder();
                propBoolQueryBuilder.filter(new TermQueryBuilder("attrs.attrId",attrId));
                propBoolQueryBuilder.filter(new TermQueryBuilder("attrs.attrValue",attrValue));
                propBoolQueryBuilder.filter(new TermQueryBuilder("attrs.attrName",attrName));
                NestedQueryBuilder nestedQueryBuilder = new NestedQueryBuilder("attrs",propBoolQueryBuilder, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);
            }

        }
        //品牌封装  tmId:tmName
        if(!StringUtils.isEmpty(trademark)){
            String[] split = trademark.split(":");
            String tmId = split[0];
            String tmName =  split[1];
            boolQueryBuilder.filter(new TermQueryBuilder("tmId",tmId));
            boolQueryBuilder.filter(new TermQueryBuilder("tmName",tmName));

        }
        //具体的搜索需要的条件(封装到一个boolQueryBuilder)
        searchSourceBuilder.query(boolQueryBuilder);

        //商品商标聚合查询(通过tmId聚合查询只查询,连接tmName和tmLogoUrl作为子聚合查询)
        searchSourceBuilder.aggregation(AggregationBuilders.terms("tmIdAgg").field("tmId")
                            .subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName"))//terms别名
                            .subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl")));


        //平台属性聚合查询(嵌套查询)
        searchSourceBuilder.aggregation(AggregationBuilders.nested("attrsAgg","attrs")//attrId是attrs的子聚合
                           .subAggregation(AggregationBuilders.terms("attrIdAgg").field("attrs.attrId")//属性值 属性name 是attrId的子聚合
                               .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue"))
                               .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"))));

        //排序规则(默认热度值排序) 后台拼接   -->1:hotScore 2:price
        if(StringUtils.isEmpty(order)){
            //没有选择的时候 热度降序
            searchSourceBuilder.sort("hotScore", SortOrder.DESC);
        }else{
            //页面选择时 两种情况
            String[] split = order.split(":");
            String orderNum = split[0];//1 热度值 2 price
            String orderSort = split[1]; //升序ASC 降序DESC
            if (orderNum.equals("1")){
                searchSourceBuilder.sort("hotScore", orderSort.equals("desc")?SortOrder.DESC:SortOrder.ASC);
            }else if(orderNum.equals("2")){
                searchSourceBuilder.sort("price",  orderSort.equals("desc")?SortOrder.DESC:SortOrder.ASC);
            }
        }

        System.out.println(searchSourceBuilder.toString());
        //搜索语句
        searchRequest.source(searchSourceBuilder);
        return searchRequest;
    }
    //解析和封装返回结果封装方法
    SearchResponseVo getSearchResponseVo(SearchResponse searchResponse){
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        //解析商标属性
        if (searchResponse != null && searchResponse.getHits().totalHits>0){
            SearchHit[] tmHits = searchResponse.getHits().getHits();
            List<Goods> goodsList = new ArrayList<>();
            for (SearchHit tmHit : tmHits) {
                //获得hit中的string数据(包含tm的信息)
                String sourceAsString = tmHit.getSourceAsString();
                //转化成json对象
                Goods goods = JSON.parseObject(sourceAsString,Goods.class);
                goodsList.add(goods);
            }
            searchResponseVo.setGoodsList(goodsList);
        }
        Aggregations aggregations = searchResponse.getAggregations();
//        ParsedLongTerms tmIdAgg = aggregations.get("tmIdAgg");
//        List<? extends Terms.Bucket> tmBuckets = tmIdAgg.getBuckets();
//        if (tmBuckets !=null && tmBuckets.size()>0){
//            List<SearchResponseTmVo> searchResponseTmVoList = new ArrayList<>();
//            for (Terms.Bucket tmBucket : tmBuckets) {
//                SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
//                //商标id
//                long tmId = tmBucket.getKeyAsNumber().longValue();
//                //商标名称
//                ParsedStringTerms tmNameAgg = tmBucket.getAggregations().get("tmNameAgg");
//                String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
//                //商标logo
//                ParsedStringTerms tmLogoUrlAgg = tmBucket.getAggregations().get("tmLogoUrlAgg");
//                String tmLogoUrl = tmLogoUrlAgg.getBuckets().get(0).getKeyAsString();
//                searchResponseTmVo.setTmId(tmId);
//                searchResponseTmVo.setTmName(tmName);
//                searchResponseTmVo.setTmLogoUrl(tmLogoUrl);
//                searchResponseTmVoList.add(searchResponseTmVo);
//            }
//            searchResponseVo.setTrademarkList(searchResponseTmVoList);
//        }
        if(aggregations!=null){
            //商标聚合属性解析
            ParsedLongTerms tmIdAgg = aggregations.get("tmIdAgg");
            List<SearchResponseTmVo> searchResponseTmVoList = tmIdAgg.getBuckets().stream().map(tmBuckets -> {
                SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
                //商标id
                long tmId = tmBuckets.getKeyAsNumber().longValue();
                //商标名称
                ParsedStringTerms tmNameAgg = tmBuckets.getAggregations().get("tmNameAgg");
                String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
                //商标logo
                ParsedStringTerms tmLogoUrlAgg = tmBuckets.getAggregations().get("tmLogoUrlAgg");
                String tmLogoUrl = tmLogoUrlAgg.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmId(tmId);
                searchResponseTmVo.setTmName(tmName);
                searchResponseTmVo.setTmLogoUrl(tmLogoUrl);
                return searchResponseTmVo;
            }).collect(Collectors.toList());
            searchResponseVo.setTrademarkList(searchResponseTmVoList);

        //平台属性聚合解析(属性id对name一对一,对value一对多)

            ParsedNested attrsAgg = aggregations.get("attrsAgg");
            ParsedLongTerms attrIdAgg = attrsAgg.getAggregations().get("attrIdAgg");
            List<SearchResponseAttrVo> searchResponseAttrVoList = attrIdAgg.getBuckets().stream().map(attrIdBucket->{
                SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
                //属性id
                long attrId = attrIdBucket.getKeyAsNumber().longValue();
                //属性name
                ParsedStringTerms attrNameAgg = attrIdBucket.getAggregations().get("attrNameAgg");
                String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
                //为属性id name 赋值
                searchResponseAttrVo.setAttrId(attrId);
                searchResponseAttrVo.setAttrName(attrName);
                //属性value
                ParsedStringTerms attrValueAgg = attrIdBucket.getAggregations().get("attrValueAgg");
                List<String> attrValueList = attrValueAgg.getBuckets().stream().map(attrValueBucket->{
                    String attrValue = attrValueBucket.getKeyAsString();
                    return attrValue;
                }).collect(Collectors.toList());
                //为属性value赋值
                searchResponseAttrVo.setAttrValueList(attrValueList);
                return searchResponseAttrVo;
            }).collect(Collectors.toList());
            searchResponseVo.setAttrsList(searchResponseAttrVoList);
        }

        return searchResponseVo;
    }
}
