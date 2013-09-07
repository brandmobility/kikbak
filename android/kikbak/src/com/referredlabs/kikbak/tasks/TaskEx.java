
package com.referredlabs.kikbak.tasks;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import android.os.Handler;
import android.os.Message;

public abstract class TaskEx extends Task {

  private Handler mHandler = new TaskHandler();

  @Override
  protected void done() {
    Message msg = mHandler.obtainMessage(0, this);
    msg.sendToTarget();
  }

  protected void onSuccess() {
  }

  protected void onFailed(Exception exception) {
  }

  protected void onCancelled() {
  }

  private static class TaskHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      TaskEx task = (TaskEx) msg.obj;
      if (task.isCancelled()) {
        task.onCancelled();
      } else {
        try {
          task.get();
          task.onSuccess();
        } catch (CancellationException e) {
          task.onCancelled();
        } catch (InterruptedException e) {
          task.onCancelled();
        } catch (ExecutionException e) {
          task.onFailed((Exception) e.getCause());
        }
      }
    }
  }

}
