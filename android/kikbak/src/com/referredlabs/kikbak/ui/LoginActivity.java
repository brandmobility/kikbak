
package com.referredlabs.kikbak.ui;

import java.util.Arrays;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.RegisterUserRequest;
import com.referredlabs.kikbak.data.RegisterUserResponse;
import com.referredlabs.kikbak.data.UserType;
import com.referredlabs.kikbak.fb.Fb;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.tasks.UpdateFriends;
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

    // if (session.isOpened()) {
    // // findViewById(R.id.login_button).setVisibility(View.INVISIBLE);
    // List<String> permissions = session.getPermissions();
    // if (!permissions.containsAll(Arrays.asList(Fb.PUBLISH_PERMISSIONS))) {
    // Session.NewPermissionsRequest newPermissionsRequest = new Session
    // .NewPermissionsRequest(this, Arrays.asList(Fb.PUBLISH_PERMISSIONS));
    // session.requestNewPublishPermissions(newPermissionsRequest);
    // }
    // }
  }

  @Override
  public void onUserInfoFetched(GraphUser user) {
    if (user != null && !Register.getInstance().isRegistered()) {
      mLoginButton.setEnabled(false);
      registerUser(user);
    }
  }

  private void registerUser(GraphUser facebookUser) {
    RegisterTask task = new RegisterTask(facebookUser);
    task.execute();
  }

  class RegisterTask extends AsyncTask<Void, Void, Void> {
    private GraphUser mFacebookUser;
    private long mUserId;
    private boolean mSuccess = false;

    RegisterTask(GraphUser user) {
      mFacebookUser = user;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
      try {
        UserType user = UserType.createFromFacebook(mFacebookUser);
        RegisterUserRequest req = new RegisterUserRequest();
        req.user = user;
        String uri = Http.getUri("/user/register/fb/");
        RegisterUserResponse resp = Http.execute(uri, req, RegisterUserResponse.class);
        mUserId = resp.userId.userId;
        mSuccess = true;
      } catch (Exception e) {
        android.util.Log.d("MMM", "Registration failed.");
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      if (mSuccess) {
        String userName = mFacebookUser.getUsername();
        if (TextUtils.isEmpty(userName)) {
          userName = mFacebookUser.getFirstName() + " " + mFacebookUser.getLastName();
        }
        Register.getInstance().registerUser(mUserId, userName);
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
        UpdateFriends task = new UpdateFriends();
        task.execute();
      } else {
        Session session = Session.getActiveSession();
        session.closeAndClearTokenInformation();
        mLoginButton.setEnabled(true);

        Toast.makeText(LoginActivity.this, R.string.registration_failed, Toast.LENGTH_LONG).show();
      }
    }
  };

}
