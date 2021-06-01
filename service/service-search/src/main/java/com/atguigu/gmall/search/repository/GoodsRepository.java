package com.atguigu.gmall.search.repository;

import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Date 2021/5/26 20:37
 * @Author JINdc
 **/

public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {

}
