package com.heed.s3;

import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.*;
import java.util.Iterator;
import java.util.List;

public class ListPublicS3Bucket {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting...");
        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        Bucket b = null;
        //String bucketName="cdn-stg.heed-dev.io";
        String bucketName="stream-adapter-prod";

        int lineNumber=0;
        //int lineToChoose = (int)(Math.random()*10000);
        String line;

        BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/lbzeev/git/LeaderboardRedis/src/main/resources/open_buckets.txt"));

        try (BufferedReader br = new BufferedReader(new FileReader("/Users/lbzeev/git/LeaderboardRedis/src/main/resources/words.txt"))) {
            while ((line = br.readLine()) != null) {
//                if (lineNumber==lineToChoose) {
//                    break;
//                }
                lineNumber++;
                bucketName=line;

                if (lineNumber>15326) {
                    System.out.println(lineNumber);
                    boolean bucketExist =false;
                    try {
                        bucketExist = s3.doesBucketExistV2(bucketName);
                    } catch (Exception e) {
                        System.out.println("Failed to check if bucket exists");
                    }
                    if (bucketExist) {
                        System.out.format("Bucket %s exists.\n",bucketName);
                        bw.write(bucketName);

                        try {
                            ObjectListing object_listing = s3.listObjects(bucketName);
                            bw.write(" can list");
                        } catch (Exception AmazonS3Exception) {
                            System.out.println("No permissions");
                        }

                        bw.write("\n");
                        bw.flush();

                    } else {
                        //System.out.format("Bucket %s doesn't exists \n", bucketName);
                    }
                }
            }
        }

        bw.close();


    }
}
