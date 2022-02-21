package com.murphy.springboot_rebbitmq;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.murphy.controller.BookController;
import com.murphy.controller.UserController;
import com.murphy.utils.RedisUtil;
import com.murphy.mapper.BookMapper;
import com.murphy.service.BookService;
import com.murphy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.util.*;

//@SpringBootTest
@Slf4j
public class SpringRabbitmqApplicationTests {


    /*    @Test
        void preHandleTest() throws Exception {}{
            long date=System.currentTimeMillis();//获取时间戳
            String uid2="u-"+date;
            System.out.println(uid2);
            log.info("timestamp:{}",uid2);
        }*/
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    UserService userService;
    @Autowired
    BookService bookService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserController userController;
    @Autowired
    BookController bookController;
    @Autowired
    BookMapper bookMapper;
    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Test
    public void searchBook() throws IOException {
        List <Object>list=new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest("test002");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //模糊搜索
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", "java");
        MatchQueryBuilder matchQueryBuilder2 = QueryBuilders.matchQuery("tags", "java");
        //bool查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .should(matchQueryBuilder)
                .should(matchQueryBuilder2);

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
            HighlightField tags = highlightFields.get("tags");
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
            if (tags!=null){
                Text[] texts2 = tags.fragments();
                String new_tags="";
                for (Text text2 : texts2) {
                    new_tags+=text2;
                }
                sourceAsMap.put("tags",new_tags);
            }
            list.add(documentFields.getSourceAsMap());
        }
        System.out.println(list);
    }

    /**
     * 测试JWT
     */
    @Test
    void testJwt(){
//        设置过期时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);
        String token=JWT.create()
                .withClaim("name", "zhangsan")
                .withClaim("age", "18")
                .withExpiresAt(calendar.getTime())
                .sign(Algorithm.HMAC256("secret"));
        System.out.println(token);
    }
    @Test
    void testVerifyJwt(){
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("0-=12hu7^%$0sdaf")).build();
        String token="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NDUxODk1NjEsInVzZXIiOiJ6aGFuZyJ9.LWLnUaob1lQmT1HpNJBhMtUmVi0cMigZYXvlgbJU4tY";
        DecodedJWT verify = jwtVerifier.verify("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NDUxODk1NjEsInVzZXIiOiJ6aGFuZyJ9.LWLnUaob1lQmT1HpNJBhMtUmVi0cMigZYXvlgbJU4tY");
//        System.out.println(verify.getClaim("user").asString());
        DecodedJWT decode = JWT.decode(token);
        System.out.println(decode.getClaim("user").asString());
    }

}
