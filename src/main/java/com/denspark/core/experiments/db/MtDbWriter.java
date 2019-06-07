package com.denspark.core.experiments.db;

import com.denspark.db.filmix_dao.FilmixGenreDao;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

public class MtDbWriter {
    private int THREAD_COUNT;
    private AtomicLong idCounter = new AtomicLong(1);
    private ExecutorService executorService;
    private ApplicationContext context;
    private CallableDbWriter dbWriter;
    private List<Future<CallableDbWriter>> futures = new ArrayList<>();

    public MtDbWriter(int threads, ApplicationContext context) {
        THREAD_COUNT = threads;
        this.context = context;
    }

    public void starExecutor() {
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        startNewTaskNtimes(10);
        executorService.shutdown();

    }

    private void startNewTaskNtimes(int n) {
        dbWriter = new CallableDbWriter(idCounter, context);
        for (int i = 0; i < n; i++) {
            Future<CallableDbWriter> future = executorService.submit(dbWriter);
            futures.add(future);
        }
    }
}
