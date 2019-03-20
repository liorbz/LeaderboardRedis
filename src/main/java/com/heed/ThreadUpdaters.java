package com.heed;

import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUpdaters {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        String key = "mostUsedLanguages";

        List<String> generatedUsers = new ArrayList<>();

        for (int j = 0; j < 100000; j++) {
            generatedUsers.add(UUID.randomUUID().toString());
        }

        for (int i=0; i<100; i++) {
            executorService.execute(() -> {
                Jedis jedis = new Jedis("127.0.0.1", 6379);

                while (true) {
                    System.out.println("User " + Thread.currentThread().getName() + " collecting");
                    try {
                        Thread.sleep((int) (Math.random() * 10000));
                    } catch (InterruptedException e) {
                    }

                    int currentUserIndex = (int) (Math.random() * 100000);
                    String currentUser = generatedUsers.get(currentUserIndex);
                    jedis.zadd(key, Math.random() * 10, currentUser);
                }
            });
        }
    }
}
