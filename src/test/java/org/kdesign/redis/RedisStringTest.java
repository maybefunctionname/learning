package org.kdesign.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kdesign.pojo.Product;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @ClassName RedisStringTest
 * @Description TODO
 * @Author {maybe a function name}
 * @Date 2024/3/6 20:39
 **/
public class RedisStringTest {
    private static RedisClient redisClient;
    private static StatefulRedisConnection<String, String> connection;
    private static RedisAsyncCommands<String, String> asyncCommands;
    @Before
    public void before() {
        redisClient = RedisClient.create(RedisURI.builder().withHost("39.98.38.197").withPort(6379).build());
        connection = redisClient.connect();
        asyncCommands = connection.async();
    }
    @After
    public void after() {
        connection.close();
        redisClient.shutdown();
    }
    @Test
    public void testCacheProduct() throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        ObjectMapper objectMapper = new ObjectMapper();
        Product product = new Product();
        product.setName("碗");
        product.setPrice(3_000.00d);
        product.setDesc("这是一个碗！");
        String json = objectMapper.writeValueAsString(product);
        asyncCommands.set("product1", json).get(1, TimeUnit.SECONDS);
    }
    @Test
    public void testGetProduct() throws ExecutionException, InterruptedException, TimeoutException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String productJson = asyncCommands.get("product1").get(1, TimeUnit.SECONDS);
        Product product = objectMapper.readValue(productJson, new TypeReference<Product>() {});
        System.out.println(product);
    }

    @Test
    public void testLock () throws InterruptedException {
        int threadNum = 1;
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        Runnable runnable = () -> {
            try {
                countDownLatch.await();
                while (true){
                    // 获取锁 5s延时不存在key才能创建
                    SetArgs setArgs = SetArgs.Builder.ex(5).nx();
                    String result = asyncCommands.set("update-product", Thread.currentThread().getName(), setArgs).get(1, TimeUnit.SECONDS);
                    if (!"OK".equals(result)) {
                        System.out.println(Thread.currentThread().getName() + "加锁失败，自旋等待锁");
                        Thread.sleep(100);
                    } else {
                        System.out.println(Thread.currentThread().getName() + "加锁成功");
                        break;
                    }
                }
                // 加锁成功，开始执行业务逻辑
                System.out.println(Thread.currentThread().getName() + "开始执行业务逻辑");
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName() + "完成业务逻辑");
                // 释放锁
                asyncCommands.del("update-product").get(1, TimeUnit.SECONDS);
                System.out.println(Thread.currentThread().getName() + "释放锁");
            } catch (Exception e) {
               e.printStackTrace();
            }
        };
        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);
        Thread thread3 = new Thread(runnable);
        thread1.start();
        thread2.start();
        thread3.start();
        // 然后3个线程开始进行抢锁
        countDownLatch.countDown();
        // 阻塞主线程
        Thread.sleep(TimeUnit.DAYS.toMillis(1));
    }

    @Test
    public void testLimit() throws Exception {
        String prefix = "order-service";
        long maxQps = 10;
        // 同一秒发起的交易
        long nowSeconds = System.currentTimeMillis() / 1000;
        for (int i = 0; i < 15; i++) {
            Long result = asyncCommands.incr(prefix + nowSeconds).get(1, TimeUnit.SECONDS);
            if (result > maxQps) {
                System.out.println("请求被限流");
            }else{
                System.out.println("请求正常被处理");
            }
        }
    }

}
