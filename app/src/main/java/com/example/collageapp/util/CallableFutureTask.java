package com.example.collageapp.util;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by Yue on 2022/10/24.
 *
 * https://github.com/sangcomz/FishBun/blob/master/FishBun/src/main/java/com/sangcomz/fishbun/util/future/CallableFutureTask.kt
 */
public class CallableFutureTask<T> extends FutureTask<T> {

    private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    private FutureCallback<T> mCallback;

    public CallableFutureTask(Callable<T> callable) {
        super(callable);
    }

    public void execute(FutureCallback<T> callback) {
        mCallback = callback;
        if (!isDone()) {
            mExecutorService.submit(this);
        } else {
            success();
        }
    }

    @Override
    protected void done() {
        super.done();
        success();
    }

    private void success() {
        if (mCallback != null) {
            try {
                mCallback.onSuccess(get());
            } catch (InterruptedException |
                     ExecutionException |
                     CancellationException e) {
                e.printStackTrace();
            }
        }
    }

    public interface FutureCallback<T> {
        void onSuccess(T result);
    }
}
