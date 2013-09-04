
package com.referredlabs.kikbak.tasks;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import android.os.Handler;
import android.os.Message;

public abstract class TaskEx<V> extends Task<V> {

  private Handler mHandler = new TaskHandler();

  @Override
  protected void done() {
    Message msg = mHandler.obtainMessage(0, this);
    mHandler.dispatchMessage(msg);
  }

  protected void onSuccess(V result) {
  }

  protected void onFailed(Exception exception) {
  }

  protected void onCancell() {
  }

  private static class TaskHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      @SuppressWarnings("unchecked")
      TaskEx<Object> task = (TaskEx<Object>) msg.obj;
      try {
        task.onSuccess(task.get());
      } catch (CancellationException e) {
        task.onCancell();
      } catch (InterruptedException e) {
        task.onCancell();
      } catch (ExecutionException e) {
        task.onFailed((Exception) e.getCause());
      }
    }
  }

}
