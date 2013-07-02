
package com.referredlabs.kikbak;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.referredlabs.kikbak.data.RegisterUserRequest;
import com.referredlabs.kikbak.data.RegisterUserResponse;
import com.referredlabs.kikbak.data.User;
import com.referredlabs.kikbak.http.Http;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends FragmentActivity implements StatusCallback,
    UserInfoChangedCallback {

  public final static String[] READ_PERMISSIONS = {
      "email"
  };
  public final static String[] PUBLISH_PERMISSIONS = {
      "publish_actions"
  };

  private UiLifecycleHelper mUiHelper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mUiHelper = new UiLifecycleHelper(this, this);
    mUiHelper.onCreate(savedInstanceState);

    setContentView(R.layout.activity_login);

    LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
    loginButton.setUserInfoChangedCallback(this);
    loginButton.setReadPermissions(Arrays.asList(READ_PERMISSIONS));
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
    if (session.isOpened()) {
      // findViewById(R.id.login_button).setVisibility(View.INVISIBLE);
      List<String> permissions = session.getPermissions();
      if (!permissions.containsAll(Arrays.asList(PUBLISH_PERMISSIONS))) {
        Session.NewPermissionsRequest newPermissionsRequest = new Session
            .NewPermissionsRequest(this, Arrays.asList(PUBLISH_PERMISSIONS));
        session.requestNewPublishPermissions(newPermissionsRequest);
      }
    }
  }

  @Override
  public void onUserInfoFetched(GraphUser user) {
    if (user != null) {
      registerUser(user);
    }
  }

  private void registerUser(GraphUser facebookUser) {
    RegisterTask task = new RegisterTask(facebookUser);
    task.execute();
  }

  class RegisterTask extends AsyncTask<Void, Void, Void> {
    GraphUser mFacebookUser;

    RegisterTask(GraphUser user) {
      mFacebookUser = user;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
      try {
        User user = User.createFromFacebook(mFacebookUser);
        RegisterUserRequest req = new RegisterUserRequest();
        req.user = user;
        String uri = Http.getUri("user/register/fb/");
        RegisterUserResponse resp = Http.execute(uri, req, RegisterUserResponse.class);
        long id = resp.userId.userId;
        if (id > 0) {
          SharedPreferences pref = getSharedPreferences("login", 0);
          Editor editor = pref.edit();
          editor.putLong("user_id", id);
          editor.commit();
        }
      } catch (Exception e) {
        android.util.Log.d("MMM", "Registration failed.");
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      // finish();
    }

  };

}
