package com.heed.asynchttpclient;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.asynchttpclient.*;
import org.asynchttpclient.util.HttpConstants;
import org.springframework.web.context.request.async.DeferredResult;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.ea.async.Async.await;

public class AsyncTest {
    public static void main(String[] args) throws IOException, InterruptedException {

        //AsyncHttpClient asyncHttpClient = Dsl.asyncHttpClient();
        AsyncHttpClient asyncHttpClient = Dsl.asyncHttpClient(Dsl.config()
                .setMaxConnections(50)
                .setMaxConnectionsPerHost(50)
                .setPooledConnectionIdleTimeout(100)
                .setConnectionTtl(500));

        HttpClient httpClient = HttpClientBuilder.create().setMaxConnTotal(50).setMaxConnPerRoute(50).build();
        ResponseHandler<String> handler = new BasicResponseHandler();

        AsyncTest asyncTest = new AsyncTest();

        asyncTest.runAll(asyncHttpClient, httpClient, handler);

//        long st=System.currentTimeMillis();
//        asyncTest.asyncHttpCall(client);
//        long et=System.currentTimeMillis();
//        long tt=et-st;
//        System.out.println("Total time: "+tt);

        //System.out.println("Before sleep");
        //TimeUnit.SECONDS.sleep(50);
        //System.out.println("After sleep");

        asyncHttpClient.close();
    }

    private void runAll(AsyncHttpClient asyncHttpClient, HttpClient httpClient,
                        ResponseHandler<String> handler) throws InterruptedException {
        long st = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        final int NUM_OF_CALLS=2000;

        // 100 calls - 68 TPS sync, 53 TPS async
        // 500 calls - 181 TPS sync, 257 TPS async
        // 1000 calls - 185 sync, 423 TPS async
        // 2000 calls - Failed sync, 750 TPS async

        for (int i=0; i<NUM_OF_CALLS; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    asyncHttpCall(asyncHttpClient);
//                    try {
//                        syncHttpCall(httpClient, handler);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
            });
        }

        System.out.println("Waiting");
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.MINUTES);
        long et = System.currentTimeMillis();
        long tt = et-st;
        System.out.println("Total time: "+tt);
        System.out.println("TPS: "+(NUM_OF_CALLS*1000/tt));
        System.out.println("Done");
    }

    private CompletableFuture<Void> asyncHttpCall(AsyncHttpClient client) {
        String response1 = await(async(client,"async1"));
        //async1(client);
        String response2 = await(async(client,"async2"));

        return CompletableFuture.completedFuture(null);
    }

    private void syncHttpCall(HttpClient client, ResponseHandler<String> handler) throws IOException {
        String response1 = sync(client,"async1", handler);
        String response2 = sync(client,"async2", handler);
    }

    private String sync(HttpClient client, String title, ResponseHandler<String> handler) throws IOException {
        HttpGet httpRequest = new HttpGet("http://www.google.com");
        return client.execute(httpRequest, handler);
    }

    private CompletableFuture<String> async(AsyncHttpClient client, String title) {
        //System.out.println("Start async1");
        Request getRequest = new RequestBuilder(HttpConstants.Methods.GET).setUrl("http://www.google.com").build();

        //Request request = Dsl.get("http://www.google.com").build();
        CompletableFuture<String> responseFuture = new CompletableFuture<>();

        client.executeRequest(getRequest, new AsyncCompletionHandler<Object>() {
            @Override
            public Object onCompleted(Response response) throws Exception {
                //System.out.println(title+" onCompleted: "+response.getStatusCode());
                if (response.getStatusCode()!=200) {
                    System.out.println("Failed");
                }
                responseFuture.complete(response.getResponseBody());
                return response;
            }
        });

//        client.executeRequest(postRequest, new AsyncCompletionHandler<Void>() {
//
//            @Override
//            public Void onCompleted(Response response) throws Exception {
//                System.out.println("Async1 onCompleted");
//                responseFuture.complete(response.getResponseBody());
//                return null;
//            }
//        });

        return responseFuture;
    }

//    private CompletableFuture<String> async2(AsyncHttpClient client) {
//
//    }
}
