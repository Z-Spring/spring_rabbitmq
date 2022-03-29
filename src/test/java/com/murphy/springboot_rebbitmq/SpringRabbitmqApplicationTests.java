package com.murphy.springboot_rebbitmq;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.murphy.controller.BookController;
import com.murphy.controller.UserController;
import com.murphy.utils.RedisUtil;
import com.murphy.mapper.BookMapper;
import com.murphy.service.BookService;
import com.murphy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.util.NamedThreadFactory;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.SpringVersion;
import org.springframework.data.redis.core.RedisTemplate;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@SpringBootTest
@Slf4j
@Aspect
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
    public static final int CORE_POOL_SIZE = 5;
    public static final int MAX_POOL_SIZE=10;
    public static final int QUEUE_CAPACITY=100;
    public static final Long KEEP_ALIVE_TIME=1L;
    public final static Lock lock= new ReentrantLock();

    @Test
    public void   test2() throws InterruptedException {
//        给线程命名
        String threadNamePrefix="test-";
        if (lock.tryLock(1,TimeUnit.SECONDS)){
            try {

            }
            finally {
                lock.unlock();
            }
        }
        ThreadFactory threadFactoryBuilder = new ThreadFactoryBuilder()
                .setNameFormat(threadNamePrefix+"my-pool-%d")
                .setDaemon(true).build();
//        创建线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                new NamedThreadFactory("murphy"));
        for (int i = 0; i < 10; i++) {
            Runnable runnable = new MyRunnable(""+i);
            executor.execute(runnable);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        System.out.println("Finished all threads");

    }
    @Before("execution(* com.murphy.springboot_rebbitmq.SpringRabbitmqApplicationTests.testJwt(..))")
    @Test
    void getVersion(){

        System.out.println(SpringVersion.getVersion());

    }

    @Test
    void getCountByPid(){
        int score=0;
        for (int pid: bookMapper.getPidFromBookInfo()){
            for (int uid: bookMapper.getUid()){
                score+= bookService.getCount(pid,uid);
            }
            log.info("{} : {}",pid,score);
            String productName= bookService.getName(pid);
            redisTemplate.opsForZSet().add("rankingList",productName,score);
            List<String> list=new ArrayList<>();

            score=0;

        }
    }
    @Test
    void getMembers(){
        System.out.println(redisTemplate.opsForZSet().reverseRange("rankingList", 0, 2));
    }
    @Test
    @Before("execution(public *com.murphy.controller.BookController.*(..))")
    void testMap(){
        log.info("test AOP");
    }

}
class MyRunnable implements Runnable{
    String command;
    public MyRunnable(String s){
        this.command=s;
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+"开始执行"+command);
        processCommand();
        System.out.println(Thread.currentThread().getName()+"执行完毕了"+command);
    }
    private void processCommand() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return this.command;
    }



}
