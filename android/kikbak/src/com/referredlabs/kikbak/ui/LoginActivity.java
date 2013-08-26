
package com.referredlabs.kikbak.ui;

import java.util.Arrays;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.flurry.android.FlurryAgent;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.RegisterUserRequest;
import com.referredlabs.kikbak.data.RegisterUserResponse;
import com.referredlabs.kikbak.data.UserType;
import com.referredlabs.kikbak.fb.Fb;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.log.Log;
import com.referredlabs.kikbak.utils.Register;

public class LoginActivity extends KikbakActivity implements StatusCallback,
    UserInfoChangedCallback {

  private UiLifecycleHelper mUiHelper;
  LoginButton mLoginButton;
  boolean mFriendsUpdated;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mUiHelper = new UiLifecycleHelper(this, this);
    mUiHelper.onCreate(savedInstanceState);

    setContentView(R.layout.activity_login);

    mLoginButton = (LoginButton) findViewById(R.id.login_button);
    mLoginButton.setUserInfoChangedCallback(this);
    mLoginButton.setReadPermissions(Arrays.asList(Fb.READ_PERMISSIONS));
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
    if (session.isOpened() && !mFriendsUpdated && Register.getInstance().isRegistered()) {
      mFriendsUpdated = true;
    }
  }

  @Override
  public void onUserInfoFetched(GraphUser user) {
    if (user != null && !Register.getInstance().isRegistered()) {
      mLoginButton.setEnabled(false);
      String accessToken = Session.getActiveSession().getAccessToken();
      registerUser(user, accessToken);
    }
  }

  private void registerUser(GraphUser fbUser, String accessToken) {
    RegisterTask task = new RegisterTask(fbUser, accessToken);
    task.execute();
  }

  class RegisterTask extends AsyncTask<Void, Void, Void> {
    GraphUser mFacebookUser;
    private String mAccessToken;
    private long mUserId;
    private boolean mSuccess = false;

    RegisterTask(GraphUser fbUser, String accessToken) {
      mFacebookUser = fbUser;
      mAccessToken = accessToken;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
      try {
        UserType user = new UserType();
        user.access_token = mAccessToken;
        RegisterUserRequest req = new RegisterUserRequest();
        req.user = user;
        String uri = Http.getUri("/user/register/fb/");
        RegisterUserResponse resp = Http.execute(uri, req, RegisterUserResponse.class);
        mUserId = resp.userId.userId;

        String userName = mFacebookUser.getName();
        Register.getInstance().registerUser(mUserId, userName);

        mSuccess = true;
      } catch (Exception e) {
        FlurryAgent.onError(Log.E_REGISTRATION, e.getMessage(), Log.CLASS_NETWORK);
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      if (mSuccess) {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
      } else {
        Session session = Session.getActiveSession();
        session.closeAndClearTokenInformation();
        mLoginButton.setEnabled(true);
        Toast.makeText(LoginActivity.this, R.string.registration_failed, Toast.LENGTH_LONG).show();
      }
    }
  };

}
