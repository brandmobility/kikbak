
package com.referredlabs.kikbak.tasks;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.referredlabs.kikbak.Kikbak;

public abstract class Task implements Future<Void> {

  private final FutureTask<Void> mFuture;
  private volatile boolean mSuccessful = false;
  private volatile Exception mException = null;
  private volatile long mCompletionTimeMillis = 0;
  private final AtomicBoolean mCancelled = new AtomicBoolean();

  public Task() {
    final Callable<Void> call = new Callable<Void>() {
      @Override
      public Void call() throws Exception {
        doInBackground();
        return null;
      }
    };

    mFuture = new FutureTask<Void>(call) {
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

  protected abstract void doInBackground() throws Exception;

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    mCancelled.set(true);
    return mFuture.cancel(mayInterruptIfRunning);
  }

  @Override
  public boolean isCancelled() {
    return mCancelled.get();
  }

  @Override
  public boolean isDone() {
    return mFuture.isDone();
  }

  @Override
  public Void get() throws InterruptedException, ExecutionException {
    return mFuture.get();
  }

  @Override
  public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
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
