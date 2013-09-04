
package com.referredlabs.kikbak.tasks;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.referredlabs.kikbak.Kikbak;

public abstract class Task<V> implements Future<V> {

  private final FutureTask<V> mFuture;
  private volatile boolean mSuccessful = false;
  private volatile Exception mException = null;
  private volatile long mCompletionTimeMillis = 0;

  public Task() {
    final Callable<V> call = new Callable<V>() {
      @Override
      public V call() throws Exception {
        return doInBackground();
      }
    };

    mFuture = new FutureTask<V>(call) {
      protected void done() {
        processDone();
      };
    };
  }

  private void processDone() {
    try {
      get();
      mSuccessful = true;
    } catch (Exception e) {
      // ignore
    }
    mCompletionTimeMillis = System.currentTimeMillis();
    done();
  }

  protected void done() {
  }

  public void execute() {
    Kikbak.PARALLEL_EXECUTOR.execute(mFuture);
  }

  protected abstract V doInBackground() throws Exception;

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    return mFuture.cancel(mayInterruptIfRunning);
  }

  @Override
  public boolean isCancelled() {
    return mFuture.isCancelled();
  }

  @Override
  public boolean isDone() {
    return mFuture.isDone();
  }

  @Override
  public V get() throws InterruptedException, ExecutionException {
    return mFuture.get();
  }

  @Override
  public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
      TimeoutException {
    return mFuture.get(timeout, unit);
  }

  public boolean isSuccessful() {
    return mSuccessful;
  }

  public Exception getException() {
    return mException;
  }

  public long getCompletionTimeMillis() {
    return mCompletionTimeMillis;
  }

}
