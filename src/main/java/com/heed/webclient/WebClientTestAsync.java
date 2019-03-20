package com.heed.webclient;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WebClientTestAsync {
    public static void main(String[] args) throws InterruptedException {

        WebClientTestAsync webClientTest = new WebClientTestAsync();

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        String key = "mostUsedLanguages";
        final int NUM_OF_THREADS = 20;
        final int NUM_OF_ITERATIONS = 20;
        CountDownLatch countDownLatch = new CountDownLatch(NUM_OF_THREADS*NUM_OF_ITERATIONS);

        for (int i=0; i<NUM_OF_THREADS; i++)
            executorService.execute(() -> {
                long st = System.currentTimeMillis();

                for (int j = 0; j < NUM_OF_ITERATIONS; j++) {
                    System.out.println("User " + Thread.currentThread().getName() + " get leaderboard");
                    WebClient webClient = WebClient.create("http://www.google.com");
                    Flux.merge(
                            webClient
                                    .get()
                                    .uri("/")
                                    .retrieve().bodyToMono(String.class)
                    )
                    .parallel()
                    .runOn(Schedulers.single())
                    .subscribe(new Subscriber<String>() {
                        @Override
                        public void onSubscribe(Subscription s) {
                            s.request(1000);
                        }

                        @Override
                        public void onNext(String s) {
                            //System.out.println("onNext");
                            //webClientTest.info(s);
                            countDownLatch.countDown();
                        }

                        @Override
                        public void onError(Throwable t) {
                            //System.out.println("Error");
                            countDownLatch.countDown();
                        }

                        @Override
                        public void onComplete() {
                            //System.out.println("onComplete");
                        }
                    });
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
