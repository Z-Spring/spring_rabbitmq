package com.murphy.service;

import com.murphy.entity.Book;
import com.murphy.entity.Cart;
import com.murphy.mapper.BookMapper;
import com.murphy.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.elasticsearch.common.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

/**
 * @author Murphy
 */
@Service
@Slf4j
public class BookService {
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    RestHighLevelClient restHighLevelClient;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    RedisTemplate redisTemplate;

    public List<Book> getBook() {
        List<Book> list = bookMapper.display();
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
        List<Book> list = bookMapper.getCart(uid);
        return list;
    }

    public List<Integer> getId() {
        if (bookMapper.getId().isEmpty()) {
            // ???????????????
            return Collections.emptyList();
        }
        return bookMapper.getId();
    }

    //    ??????uid??????pid
    public List<Integer> getId2(int uid) {
        if (bookMapper.getId2(uid).isEmpty()) {
            // ???????????????
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

    public int getCountByName(String name, int uid) {
        if (bookMapper.getId2(uid).contains(name)) {
            return bookMapper.getCountByName(name, uid);
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

    public List<Integer> getPidFromBookInfo() {
        return bookMapper.getPidFromBookInfo();
    }

    /**
     * ???????????????????????????
     *
     * @param keyword ???????????????
     * @return ??????????????????     ???????????????????????????????5.5
     * @throws IOException
     */
    public List searchBook(String keyword) throws IOException {
        List<Object> list = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest("bookstore");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // ????????????
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", keyword);
        // bool??????
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().should(matchQueryBuilder);
        // ???????????? ????????????name????????????
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder
                .field("name")
                .field("tags")
                .preTags("<span class='test' style='color:red'>")
                .postTags("</span>");

        searchSourceBuilder
                .query(matchQueryBuilder)
                .query(boolQueryBuilder)
                .highlighter(highlightBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse =
                restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        if (searchResponse.getHits().getTotalHits().value == 0) {
            log.info("can not find this product????");
            List error = List.of("searchError");
            return error;
        } else {
            for (SearchHit documentFields : searchResponse.getHits().getHits()) {
                Map<String, HighlightField> highlightFields = documentFields.getHighlightFields();
                HighlightField name = highlightFields.get("name");
                Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
                int pid = parseInt(documentFields.getId());
                sourceAsMap.put("pid", pid);
                // ?????????????????????????????????????????????????????????????????????
                if (name != null) {
                    Text[] texts = name.fragments();
                    String new_name = "";
                    for (Text text : texts) {
                        new_name += text;
                    }
                    sourceAsMap.put("name", new_name);
                }
                list.add(documentFields.getSourceAsMap());
            }
        }
        return list;
    }

    /**
     * ??????????????????????????????
     */
    public String getName(int pid) {
        return bookMapper.getName(pid);
    }

    /**
     * ??????????????????????????? ????????????id????????????????????????????????????????????????
     *
     * <p>????????????????????? ??????Redis???zset
     *
     * <p>key-rankinglist value-productName&score
     */
    @Scheduled(initialDelay = 60_000, fixedRate = 60_000)
    public void getCountByPid() {
        int score = 0;
        for (int pid : bookMapper.getPidFromBookInfo()) {
            for (int uid : bookMapper.getUid()) {
                score += getCount(pid, uid);
            }
            log.info("{} : {}", pid, score);
            String productName = getName(pid);
            redisTemplate.opsForZSet().add("rankingList", productName, score);
            score = 0;
        }
    }
}
