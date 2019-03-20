package com.heed.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LeaderboardThreadsMongoSync {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        String key = "mostUsedLanguages";
        final int NUM_OF_THREADS = 10;
        final int NUM_OF_ITERATIONS = 10;
        CountDownLatch countDownLatch = new CountDownLatch(NUM_OF_THREADS*NUM_OF_ITERATIONS);

        for (int i=0; i<NUM_OF_THREADS; i++)
            executorService.execute(() -> {
                long st = System.currentTimeMillis();
                MongoClient mongoClient = MongoClients.create("mongodb://localhost");
                MongoDatabase testDb = mongoClient.getDatabase("test");
                //System.out.println("GetDBName:"+testDb.getName());
                MongoCollection<Document> usersCollection = testDb.getCollection("users");

                for (int j = 0; j < NUM_OF_ITERATIONS; j++) {
                    System.out.println("User " + Thread.currentThread().getName() + " get leaderboard");
                    //try {
                    //Thread.sleep((int) (Math.random() * 10000));
                    //} catch (InterruptedException e) {
                    //}

                    Bson orderBy = Sorts.orderBy(new BasicDBObject("amount", 1));

                    FindIterable<Document> documents = usersCollection.find().sort(orderBy).limit(50);
                    for (Document document : documents) {
                        //System.out.println(document);
                    }

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
}
