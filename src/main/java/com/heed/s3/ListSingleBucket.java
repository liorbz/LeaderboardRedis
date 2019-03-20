package com.heed.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.*;
import java.util.Iterator;

public class ListSingleBucket {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting...");
        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        String bucketName = "BCR";

        ObjectListing object_listing = s3.listObjects(bucketName);

        for (Iterator<?> iterator =
             object_listing.getObjectSummaries().iterator();
             iterator.hasNext(); ) {
            S3ObjectSummary summary = (S3ObjectSummary) iterator.next();
            //if (summary.getKey().endsWith(".txt")) {
                System.out.println("https://s3.amazonaws.com/" + bucketName + '/' + summary.getKey());
            //}
        }

        // next page
        // String nextMarker = object_listing.getNextMarker();
    }
}
