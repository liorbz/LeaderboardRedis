package com.heed;

import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LeaderboardThreads {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        String key = "mostUsedLanguages";

        for (int i=0; i<10; i++) {
            executorService.execute(() -> {
                Jedis jedis = new Jedis("127.0.0.1", 6379);

                while (true) {
                    System.out.println("User " + Thread.currentThread().getName() + " get leaderboard");
                    try {
                        Thread.sleep((int) (Math.random() * 10000));
                    } catch (InterruptedException e) {
                    }

                    long st = System.currentTimeMillis();
                    Set<String> zrange = jedis.zrevrange(key, 0, 50);
                    long et = System.currentTimeMillis();
                    long tt = et - st;
                    System.out.println("Total time: "+tt);
                }
            });
        }
    }
}
