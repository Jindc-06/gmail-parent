package com.atguigu.gmall.all.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;
import com.atguigu.gmall.search.SearchFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Date 2021/5/26 11:14
 * @Author JINdc
 **/
@Controller
public class SearchController {

    @Autowired
    private SearchFeignClient searchFeignClient;

    //es,搜索框或商品属性搜索
    @RequestMapping({"list.html","search.html"})
    public String list(Model model, SearchParam searchParam, HttpServletRequest request){
        StringBuffer requestURL = request.getRequestURL();
        SearchResponseVo searchResponseVo = searchFeignClient.list(searchParam);
        List<Goods> goodsList = searchResponseVo.getGoodsList();
        model.addAttribute("goodsList",goodsList);
        model.addAttribute("trademarkList",searchResponseVo.getTrademarkList());
        model.addAttribute("attrsList",searchResponseVo.getAttrsList());
        model.addAttribute("urlParam",requestURL + "?" + getUrlParam(searchParam));//传给前台的url
        //品牌属性值
        if (!StringUtils.isEmpty(searchParam.getTrademark())){
            //品牌   2:华为  tmId:tmName
            model.addAttribute("trademarkParam",searchParam.getTrademark().split(":")[1]);
        }
        //平台属性 SearchAttr
        if (searchParam.getProps()!=null && searchParam.getProps().length>0){
            List<SearchAttr> propsParamList = new ArrayList<>();
            //取出属性集合(props=1:0-499:价格)
            for (String prop : searchParam.getProps()) {
                String attrId = prop.split(":")[0];
                String attrName = prop.split(":")[2];
                String attrValue = prop.split(":")[1];
                SearchAttr searchAttr = new SearchAttr();
                searchAttr.setAttrId(Long.parseLong(attrId));
                searchAttr.setAttrName(attrName);
                searchAttr.setAttrValue(attrValue);
                propsParamList.add(searchAttr);
            }
            model.addAttribute("propsParamList",propsParamList);
        }
        //热度值  order=2:desc=热度值:排列顺序
        if (!StringUtils.isEmpty(searchParam.getOrder())){
            //前端需要返回一个map集合
            Map<String,Object> orderMap = new HashMap<>();
            String[] split = searchParam.getOrder().split(":");
            String orderNum = split[0];//热度值
            String orderSort = split[1];//排列顺序
            orderMap.put("type",orderNum);
            orderMap.put("sort",orderSort);
            model.addAttribute("orderMap",orderMap);
        }
        return "list/index";
    }
    //前台的数据的的http访问Url(localhost:8300/list.html/+urlParam)
    public Object getUrlParam(SearchParam searchParam){
        String urlParam = "";

         // urlParam--> ?(三级分类id或者关键字)&props(平台属性数组)

        String keyword = searchParam.getKeyword();
        Long category3Id = searchParam.getCategory3Id();
        String[] props = searchParam.getProps();
        //String trademark = searchParam.getTrademark();

        //url参数拼接 ,关键字(keyword)和三级分类ID(category3Id)只能一个
        if (!StringUtils.isEmpty(keyword)){
            urlParam = urlParam+"keyword="+keyword;
        }
        if (!StringUtils.isEmpty(category3Id)){
            urlParam = urlParam+"category3Id="+category3Id;
        }
        if(props!=null && props.length>0){
            for (String prop : props) {
                urlParam = urlParam+"&props="+prop;
            }
        }
        return urlParam;
    }

    //首页三级分类属性列表
    @RequestMapping({"index.html","/"})
    public String index(Model model){
        List<JSONObject> jsonObjects = searchFeignClient.getCategoryToIndex();
        model.addAttribute("list",jsonObjects);
        return "index/index";
    }
}
