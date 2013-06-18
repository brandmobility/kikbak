package com.referredlabs.kikbak;

import java.util.Arrays;

import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.referredlabs.kikbak.data.User;
import com.referredlabs.kikbak.http.Http;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends Activity implements StatusCallback, UserInfoChangedCallback {

  private UiLifecycleHelper mUiHelper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mUiHelper = new UiLifecycleHelper(this, this);
    mUiHelper.onCreate(savedInstanceState);

    setContentView(R.layout.activity_login);

    LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
    loginButton.setUserInfoChangedCallback(this);
    loginButton.setReadPermissions(Arrays.asList("email"));
  }

  @Override
  protected void onResume() {
    super.onResume();
    mUiHelper.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
    mUiHelper.onPause();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mUiHelper.onDestroy();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mUiHelper.onSaveInstanceState(outState);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    mUiHelper.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void call(Session session, SessionState state, Exception exception) {
    android.util.Log.d("MMM", "Session state:" + state);
    android.util.Log.d("MMM", "exception:" + exception);
    if (session.isOpened()) {
      findViewById(R.id.login_button).setVisibility(View.INVISIBLE);
    }

  }

  @Override
  public void onUserInfoFetched(GraphUser user) {
    if (user != null) {
      registerUser(user);
    }
  }

  private void registerUser(GraphUser facebookUser) {
    User user = User.createFromFacebook(facebookUser);
    RegisterTask task = new RegisterTask(user);
    task.execute();
  }

  class RegisterTask extends AsyncTask<Void, Void, Void> {
    User mUser;

    RegisterTask(User user) {
      mUser = user;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
      long id = Http.sendRegisterUserReqest(mUser);
      if (id > 0) {
        SharedPreferences pref = getSharedPreferences("login", 0);
        Editor editor = pref.edit();
        editor.putLong("user_id", id);
        editor.commit();
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      // TODO Auto-generated method stub
      startActivity(new Intent(LoginActivity.this, OffersActivity.class));
      finish();
    }

  };

}
