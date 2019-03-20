package com.heed.webclient;

import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WebClientTestSync {
    public static void main(String[] args) throws InterruptedException {

        WebClientTestSync webClientTest = new WebClientTestSync();

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        String key = "mostUsedLanguages";
        final int NUM_OF_THREADS = 20;
        final int NUM_OF_ITERATIONS = 20;
        CountDownLatch countDownLatch = new CountDownLatch(NUM_OF_THREADS*NUM_OF_ITERATIONS);
        RestTemplate restTemplate = new RestTemplate();

        for (int i=0; i<NUM_OF_THREADS; i++)
            executorService.execute(() -> {
                long st = System.currentTimeMillis();

                for (int j = 0; j < NUM_OF_ITERATIONS; j++) {
                    System.out.println("User " + Thread.currentThread().getName() + " get leaderboard");
                    restTemplate.getForEntity("http://www.google.com", String.class);
                    countDownLatch.countDown();
                }

                long et = System.currentTimeMillis();
                long tt = et - st;
                System.out.println("Total time: " + tt);
            });

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.MINUTES);

        countDownLatch.await();
    }

    public void info(String s) {
        System.out.println(s);
    }
}
