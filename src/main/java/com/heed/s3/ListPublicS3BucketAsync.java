package com.heed.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class ListPublicS3BucketAsync {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Starting...");
        //S3AsyncClient s3AsyncClient = S3AsyncClient.builder().build();
        //S3Client s3Client = S3Client.builder().build();
        Bucket b = null;
        //String bucketName="cdn-stg.heed-dev.io";

        int lineNumber=0;
        //int lineToChoose = (int)(Math.random()*10000);
        //String line = "anchorman";
        //String line = "analogue";
        String line;
        ExecutorService threadPool = Executors.newFixedThreadPool(200);
        AtomicInteger queueSize = new AtomicInteger();

        BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/lbzeev/git/LeaderboardRedis/src/main/resources/open_buckets5.txt"));

        try (BufferedReader br = new BufferedReader(new FileReader("/Users/lbzeev/git/LeaderboardRedis/src/main/resources/words.txt"))) {
            while ((line = br.readLine()) != null) {
//                if (lineNumber==lineToChoose) {
//                    break;
//                }
                lineNumber++;
                final String bucketName=line;

                if (lineNumber>15326) {
                    //System.out.println(lineNumber);
                    boolean bucketExist = false;
                    try {

//                        CompletableFuture<ListObjectsResponse> listObjectResponse = s3AsyncClient.listObjects(
//                                ListObjectsRequest.builder().bucket(bucketName).build());

//                        CompletableFuture<GetBucketAclResponse> future = s3AsyncClient.getBucketAcl(
//                                GetBucketAclRequest.builder().bucket(bucketName).build())
//                                .thenApply(s -> {
//                                    System.out.format("Bucket %s exists.\n", bucketName);
//                                    s3AsyncClient.listObjects(
//                                            ListObjectsRequest.builder().bucket(bucketName).build())
//                                            .thenApply(a -> {
//                                                return a;
//                                            });
//                                    return s;
//                                });


//                        s3AsyncClient.getBucketLocation(
//                                GetBucketLocationRequest.builder().bucket(bucketName).build())
//                                .thenAcceptAsync(s -> {
                        //System.out.println("Bucket name: "+bucketName);
                                    //System.out.println("list objects");

//                        ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(bucketName).maxKeys(1).build());
//
//                        System.out.println("listObjectsV2Response:");
//                        for (S3Object s3Object : listObjectsV2Response.contents()) {
//                            System.out.println(s3Object.key());
//                        }
//
//                        ListObjectsV2Response listObjectsV2Response1 = s3AsyncClient.listObjectsV2(
//                                ListObjectsV2Request.builder().bucket(bucketName).maxKeys(1).build()).get();
//
//                        System.out.println("listObjectsV2Response1:");
//                        for (S3Object s3Object : listObjectsV2Response1.contents()) {
//                            System.out.println(s3Object.key());
//                        }

                        queueSize.incrementAndGet();

                        TimeUnit.MILLISECONDS.sleep(100);
                        S3AsyncClient s3AsyncClient = S3AsyncClient.builder().build();
                        s3AsyncClient.listObjectsV2(
                                ListObjectsV2Request.builder().bucket(bucketName).maxKeys(1).build()).whenCompleteAsync(
                                (listObjectsV2Response2, throwable) -> {
                                    //System.out.println("listObjectsV2Response2:");

                                    queueSize.decrementAndGet();
                                    System.out.println("queueSize: "+queueSize);

                                    try {
                                        if (throwable!=null) {
                                            System.out.println(throwable.getMessage());
                                            System.out.println(bucketName+ " No permissions");
                                            bw.write(bucketName+"\n");
                                        } else {
                                            bw.write(bucketName+" can list\n");
                                            System.out.println(bucketName+" can list\n");

    //                                        for (S3Object s3Object : listObjectsV2Response2.contents()) {
    //                                            System.out.println(s3Object.key());
    //                                        }
                                        }
                                        if (System.currentTimeMillis()%20==0) {
                                            bw.flush();
                                        }
                                    } catch (IOException e) {
                                        System.out.println("Failed write to file: "+e.getMessage());
                                    }
                                }
                        , threadPool);



//                                            .thenApply(r -> {
//                                                System.out.printf("success");
//                                                try {
//                                                    bw.write(bucketName+" can list\n");
//                                                    bw.flush();
//                                                } catch (IOException e) {
//                                                    e.printStackTrace();
//                                                }
//                                                return null;
//                                            } )
//                                            .exceptionally(e -> {
//                                                System.out.println("Failed");
//                                                return null;
//                                            });

//                                })
//                                .exceptionally(e -> {
//                                    //e.printStackTrace();
//                                    System.out.println("No permissions");
//                                    try {
//                                        bw.write(bucketName+"\n");
//                                        bw.flush();
//                                    } catch (IOException e1) {
//                                        e1.printStackTrace();
//                                    }
//                                    return null;
//                                });

                    } catch (Exception e) {
                        System.out.println("Failed to check if bucket exists");
                    }
                }
            }
        }

        System.out.println("Before sleep");
        TimeUnit.SECONDS.sleep(10);
        System.out.println("After sleep");
        bw.close();

    }
}
