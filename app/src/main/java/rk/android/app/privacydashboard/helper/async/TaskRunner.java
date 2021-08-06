package rk.android.app.privacydashboard.helper.async;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskRunner {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Executor executor = Executors.newCachedThreadPool();

    public <R> void executeAsync(CustomCallable<R> callable) {
        try {
            callable.setUiForLoading();
            executor.execute(new RunnableTask<>(handler, callable));
        } catch (Exception ignored) {

        }
    }

    public static class RunnableTask<R> implements Runnable{
        private final Handler handler;
        private final CustomCallable<R> callable;

        public RunnableTask(Handler handler, CustomCallable<R> callable) {
            this.handler = handler;
            this.callable = callable;
        }

        @Override
        public void run() {
            try {
                final R result = callable.call();
                handler.post(new RunnableTaskForHandler<>(callable, result));
            } catch (Exception ignored) {
            }
        }
    }

    public static class RunnableTaskForHandler<R> implements Runnable{

        private final CustomCallable<R> callable;
        private final R result;

        public RunnableTaskForHandler(CustomCallable<R> callable, R result) {
            this.callable = callable;
            this.result = result;
        }

        @Override
        public void run() {
            callable.setDataAfterLoading(result);
        }
    }
}