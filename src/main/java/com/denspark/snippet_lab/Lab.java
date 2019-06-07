package com.denspark.snippet_lab;

import java.util.concurrent.*;
import java.util.function.Supplier;

public class Lab {

    public static void main(String[] args) {

//        runAsyncTest();
//        supplyAsyncTest();
//        executorAsync();
        thenApplyTest();


    }

    public static void runAsyncTest() {
        CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                // Имитация длительной работы
                try {
                    TimeUnit.SECONDS.sleep(4);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
                System.out.println("Я буду работать в отдельном потоке, а не в главном.");
            }
        });

// Блокировка и ожидание завершения Future
        try {
            future.get();
            System.out.println("Get() завершился");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void supplyAsyncTest() {
        // Запуск асинхронной задачи, заданной объектом Supplier
        CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    System.out.println("Future is running....");
                    TimeUnit.SECONDS.sleep(4);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
                return "Результат асинхронной задачи";
            }
        });

// Блокировка и получение результата Future
        try {
            String result = future.get();
            System.out.println(result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void executorAsync() {
        Executor executor = Executors.newFixedThreadPool(10);
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Результат асинхронной задачи";
        }, executor);

        try {
            String result = future.get();
            System.out.println(result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    public static void thenApplyTest() {
        // Создаём CompletableFuture
        CompletableFuture<String> whatsYourNameFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Rajeev";
        });

// Добавляем колбэк к Future, используя thenApply()
        CompletableFuture<String> greetingFuture = whatsYourNameFuture.thenApply(name -> {
            return "Привет," + name;
        });

// Блокировка и получение результата Future
        try {
            System.out.println(greetingFuture.get()); // Привет, Rajeev
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
