
package com.referredlabs.kikbak.ui;

import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.flurry.android.FlurryAgent;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.RegisterUserRequest;
import com.referredlabs.kikbak.data.RegisterUserResponse;
import com.referredlabs.kikbak.data.RegisterUserResponseStatus;
import com.referredlabs.kikbak.data.UserType;
import com.referredlabs.kikbak.fb.Fb;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.log.Log;
import com.referredlabs.kikbak.tasks.TaskEx;
import com.referredlabs.kikbak.utils.Register;
import com.referredlabs.kikbak.utils.StatusException;

public class LoginActivity extends KikbakActivity implements
    UserInfoChangedCallback {

  private UiLifecycleHelper mUiHelper;
  private LoginButton mLoginButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mUiHelper = new UiLifecycleHelper(this, null);
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
  public void onUserInfoFetched(GraphUser user) {
    if (user != null && !Register.getInstance().isRegistered()) {
      mLoginButton.setEnabled(false);
      String accessToken = Session.getActiveSession().getAccessToken();
      registerUser(user, accessToken);
    }
  }

  private void registerUser(GraphUser fbUser, String accessToken) {
    mTask = new RegisterTask(fbUser, accessToken);
    mTask.execute();
  }

  protected void onRegistrationSuccess() {
    startActivity(new Intent(LoginActivity.this, MainActivity.class));
    finish();
  }

  protected void onRegistrationFailed(Exception exception) {
    Session session = Session.getActiveSession();
    session.closeAndClearTokenInformation();

    if (exception instanceof StatusException) {
      StatusException e = (StatusException) exception;
      RegisterUserResponseStatus status = e.getStatus();
      if (status == RegisterUserResponseStatus.TOO_FEW_FRIENDS) {
        showTooFewFriendsDialog();
        return;
      }
    }

    Toast.makeText(LoginActivity.this, R.string.registration_failed, Toast.LENGTH_LONG).show();
    mLoginButton.setEnabled(true);
  }

  private void showTooFewFriendsDialog() {
    new TooFewFriendsDialog().show(getSupportFragmentManager(), null);
  }

  class RegisterTask extends TaskEx {
    GraphUser mFacebookUser;
    private String mAccessToken;

    RegisterTask(GraphUser fbUser, String accessToken) {
      mFacebookUser = fbUser;
      mAccessToken = accessToken;
    }

    @Override
    protected void doInBackground() throws Exception {
      UserType user = new UserType();
      user.access_token = mAccessToken;

      RegisterUserRequest req = new RegisterUserRequest();
      req.user = user;

      String uri = Http.getUri("/user/register/fb/");
      RegisterUserResponse resp = Http.execute(uri, req, RegisterUserResponse.class);

      if (resp.status != RegisterUserResponseStatus.OK) {
        throw new StatusException(resp.status);
      }

      String firstName = mFacebookUser.getFirstName();
      String fullName = mFacebookUser.getName();
      Register.getInstance().registerUser(resp.userId.userId, firstName, fullName);
    }

    @Override
    protected void onSuccess() {
      FlurryAgent.logEvent(Log.EVENT_REG_OK);
      onRegistrationSuccess();
    }

    @Override
    protected void onFailed(Exception exception) {
      FlurryAgent.logEvent(Log.EVENT_REG_FAILED);
      onRegistrationFailed(exception);
    }
  }
}
