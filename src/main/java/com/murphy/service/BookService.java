package com.murphy.service;

import com.murphy.entity.Book;
import com.murphy.entity.Cart;
import com.murphy.mapper.BookMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.elasticsearch.common.text.Text;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * @author Murphy
 */
@Service
public class BookService {
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    RestHighLevelClient restHighLevelClient;


    public List<Book> getBook() {
        List list = bookMapper.display();
        return list;
    }

    public Book getBookById(int pid) {
        return bookMapper.display2(pid);
    }

    public boolean addCart(int pid, int count, double summprice, int uid) {
        if (bookMapper.count(pid, count, summprice, uid) > 0) {
            return true;
        }
        return false;
    }

    public List<Book> getCart(int uid) {
        List list = bookMapper.getCart(uid);
        return list;
    }

    public List<Integer> getId() {
        if (bookMapper.getId().isEmpty()) {
            //设置空集合
            return Collections.emptyList();
        }
        return bookMapper.getId();
    }

    //    根据uid寻找pid
    public List<Integer> getId2(int uid) {
        if (bookMapper.getId2(uid).isEmpty()) {
            //设置空集合
            return Collections.emptyList();
        }
        return bookMapper.getId2(uid);
    }

    public int getCount(int pid, int uid) {
        if (bookMapper.getId2(uid).contains(pid)) {
            return bookMapper.getCount(pid, uid);
        }
        return 0;
    }

    public boolean updateCart(Cart cart) {
        if (bookMapper.updateCart(cart)) {
            return true;
        }
        return false;
    }

    public Double getPrice(int uid) {
        if (bookMapper.getId2(uid).isEmpty()) {
            return 0.00;
        }
        return bookMapper.getPrice(uid);
    }

    public List getPidFromBookInfo(){
        return bookMapper.getPidFromBookInfo();
    }

    /**
     * 顶部搜索框请求数据
     * @param keyword  搜索关键字
     * @return 返回相关数据
     * @throws IOException
     */
    public List searchBook(String keyword) throws IOException {
        List <Object>list=new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest("bookstore");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //模糊搜索
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", keyword);
        //bool查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .should(matchQueryBuilder);
        //高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name")
                .field("tags")
                .preTags("<span class='test' style='color:red'>")
                .postTags("</span>");

        searchSourceBuilder
                .query(matchQueryBuilder)
                .query(boolQueryBuilder)
                .highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit documentFields:searchResponse.getHits().getHits()){
            Map<String, HighlightField> highlightFields = documentFields.getHighlightFields();
            HighlightField name = highlightFields.get("name");
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
            //解析高亮的字段，将原来的字段替换成新的高亮字段
            if (name!=null){
                Text[] texts = name.fragments();
                String new_name="";
                for (Text text : texts) {
                    new_name+=text;
                }
                sourceAsMap.put("name",new_name);
            }
            list.add(documentFields.getSourceAsMap());
        }
        return list;
    }

}
