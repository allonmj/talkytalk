package com.allonapps.talkytalk.data.async.rolled;

import android.util.Pair;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by michael on 10/26/17.
 */

public class AsyncManager {

    private final ExecutorService executorService;
    private final Map<AsyncOpWrapper, Pair<Future<?>, AsyncOp>> operations = new HashMap<>();

    public AsyncManager() {
        this.executorService = createExecutorService();
    }

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;

    private ExecutorService createExecutorService() {
        return new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>()
        );
    }

    public void cancel(Cancelable cancelable) {
        synchronized (operations) {
            if (!operations.containsKey(cancelable.asyncOpWrapper)) {
                return;
            }

            Pair<Future<?>, AsyncOp> pair = operations.get(cancelable.asyncOpWrapper);
            operations.remove(cancelable.asyncOpWrapper);
            if (pair != null) {
                pair.second.cancel();
                pair.first.cancel(true);
            }
        }
    }

    public Cancelable runAsyncOp(AsyncOp asyncOp) throws InterruptedException {
        synchronized (operations) {
            AsyncOpWrapper asyncOpWrapper = new AsyncOpWrapper(asyncOp);
            Future<?> future = executorService.submit(asyncOpWrapper);
            operations.put(asyncOpWrapper, new Pair<Future<?>, AsyncOp>(future, asyncOp));
            return new Cancelable(asyncOpWrapper);
        }
    }

    public static class Cancelable {
        private final AsyncOpWrapper asyncOpWrapper;

        private Cancelable(AsyncOpWrapper asyncOpWrapper) {
            this.asyncOpWrapper = asyncOpWrapper;
        }
    }

    public static abstract class AsyncOp {
        private boolean cancelled;

        private final long timeOutMilliseconds;

        public AsyncOp() {
            this.timeOutMilliseconds = 0;
        }

        public AsyncOp(long timeOutMilliseconds) {
            this.timeOutMilliseconds = timeOutMilliseconds;
        }

        public void cancel() {
            cancelled = true;
        }

        public boolean isCancelled() {
            return cancelled;
        }

        public abstract void doInBackground();
    }

    private class AsyncOpWrapper implements Runnable {
        private final WeakReference<AsyncOp> weakReference;

        AsyncOpWrapper(AsyncOp asyncOp) {
            weakReference = new WeakReference<AsyncOp>(asyncOp);
        }

        private void cleanUp() {
            synchronized (operations) {
                operations.remove(this);
            }
        }

        @Override
        public void run() {
            AsyncOp asyncOp = weakReference.get();
            if (asyncOp == null) {
                return;
            }
            asyncOp.doInBackground();
            cleanUp();
        }
    }
}