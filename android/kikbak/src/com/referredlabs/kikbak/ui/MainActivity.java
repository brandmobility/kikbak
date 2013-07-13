
package com.referredlabs.kikbak.ui;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.gson.Gson;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.AvailableCreditType;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.GiftType;
import com.referredlabs.kikbak.gcm.GcmHelper;
import com.referredlabs.kikbak.service.LocationFinder;
import com.referredlabs.kikbak.service.LocationFinder.LocationFinderListener;
import com.referredlabs.kikbak.ui.OfferListFragment.OnOfferClickedListener;
import com.referredlabs.kikbak.ui.RedeemChooserDialog.OnRedeemOptionSelectedListener;
import com.referredlabs.kikbak.ui.RewardListFragment.OnRewardClickedListener;
import com.referredlabs.kikbak.utils.Register;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener,
    OnOfferClickedListener, OnRewardClickedListener, OnRedeemOptionSelectedListener,
    LocationFinderListener {

  SectionsPagerAdapter mSectionsPagerAdapter;
  ViewFlipper mViewFlipper;
  ViewPager mViewPager;
  LocationFinder mLocationFinder;
  Reward mSelectedReward;

  BroadcastReceiver mNetworkStateReceiver;
  boolean mIsConnected;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (!Register.getInstance().isRegistered()) {
      startActivity(new Intent(this, LoginActivity.class));
      finish();
      return;
    }
    setContentView(R.layout.activity_main);
    GcmHelper.getInstance().registerIfNeeded();
    mLocationFinder = new LocationFinder(this);
    setupViews();
  }

  void setupViews() {
    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

    mViewPager = (ViewPager) findViewById(R.id.pager);
    mViewPager.setAdapter(mSectionsPagerAdapter);

    mViewFlipper = (ViewFlipper) findViewById(R.id.flipper);
    if (!isConnected()) {
      mViewFlipper.setDisplayedChild(1);
    }
    setupActionBar();
  }

  void setupActionBar() {
    final ActionBar actionBar = getActionBar();
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
      @Override
      public void onPageSelected(int position) {
        actionBar.setSelectedNavigationItem(position);
      }
    });

    for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
      Tab tab = actionBar.newTab();
      tab.setText(mSectionsPagerAdapter.getPageTitle(i));
      tab.setTabListener(this);
      actionBar.addTab(tab);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    registerNetworkStateReceiver();
    mLocationFinder.startLocating();
  }

  @Override
  protected void onPause() {
    super.onPause();
    unregisterNetworkStateReceiver();
    mLocationFinder.stopLocating();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_settings:
        break;

      case R.id.action_login:
        startActivity(new Intent(this, LoginActivity.class));
        return true;

      case R.id.action_clear_registration:
        Register.getInstance().clear();
        finish();
        return true;

      case R.id.action_gcm:
        actionGcm();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    mViewPager.setCurrentItem(tab.getPosition());
  }

  @Override
  public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
  }

  @Override
  public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
  }

  @Override
  public void onOfferClicked(ClientOfferType offer) {
    Intent intent = new Intent(this, GiveActivity.class);
    Gson gson = new Gson();
    String data = gson.toJson(offer);
    intent.putExtra("data", data);
    startActivity(intent);
  }

  @Override
  public void onRewardClicked(Reward reward) {
    if (reward.gift != null && reward.credit != null) {
      mSelectedReward = reward;
      String gift = reward.getGiftValueString();
      String credit = reward.getCreditValueString();
      RedeemChooserDialog dialog = RedeemChooserDialog.newInstance(gift, credit);
      dialog.show(getSupportFragmentManager(), "");
    } else if (reward.gift != null) {
      redeemGift(reward.gift);
    } else if (reward.credit != null) {
      redeemCredit(reward.credit);
    }
  }

  @Override
  public void onRedeemGiftSelected() {
    if (mSelectedReward != null) {
      redeemGift(mSelectedReward.gift);
      mSelectedReward = null;
    }
  }

  @Override
  public void onRedeemCreditSelected() {
    if (mSelectedReward != null) {
      redeemCredit(mSelectedReward.credit);
      mSelectedReward = null;
    }
  }

  private void redeemGift(GiftType gift) {
    Intent intent = new Intent(this, RedeemGiftActivity.class);
    String data = new Gson().toJson(gift);
    intent.putExtra(RedeemGiftActivity.EXTRA_GIFT, data);
    startActivity(intent);
  }

  private void redeemCredit(AvailableCreditType credit) {
    Intent intent = new Intent(this, RedeemCreditActivity.class);
    String data = new Gson().toJson(credit);
    intent.putExtra(RedeemCreditActivity.EXTRA_KIKBAK, data);
    startActivity(intent);
  }

  boolean isConnected() {
    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    return isConnected;
  }

  void unregisterNetworkStateReceiver() {
    if (mNetworkStateReceiver != null) {
      unregisterReceiver(mNetworkStateReceiver);
      mNetworkStateReceiver = null;
    }
  }

  void registerNetworkStateReceiver() {
    mNetworkStateReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        boolean isConnected = isConnected();
        // if (isConnected != mIsConnected) {
        // mIsConnected = isConnected;
        // mViewFlipper.setDisplayedChild(isConnected ? 0 : 1);
        // }
        Log.w("MMM", "connected:" + isConnected);
      }
    };

    IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    registerReceiver(mNetworkStateReceiver, filter);
    mIsConnected = isConnected();
    mViewFlipper.setDisplayedChild(mIsConnected ? 0 : 1);
  }

  @Override
  public void onLocationUpdated(Location location) {
    OfferListFragment frag = mSectionsPagerAdapter.getOfferFragment();
    if (frag != null)
      frag.setUserLocation(location);
  }

  private void actionGcm() {
    GcmHelper helper = GcmHelper.getInstance();
    String regId = helper.getRegistrationId();
    if (regId != null) {
      Toast.makeText(this, regId, Toast.LENGTH_LONG).show();
    } else {
      Toast.makeText(this, "Not yet registered!", Toast.LENGTH_SHORT).show();
    }
  }

  // ------------------------------------------------------

  private class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public int getCount() {
      return 2;
    }

    @Override
    public Fragment getItem(int position) {
      if (position == 0)
        return new OfferListFragment();
      if (position == 1)
        return new RewardListFragment();

      throw new IllegalArgumentException();
    }

    @Override
    public CharSequence getPageTitle(int position) {
      if (position == 0)
        return getString(R.string.title_offers);
      if (position == 1)
        return getString(R.string.title_redeem);
      throw new IllegalArgumentException();
    }

    public Fragment getFragmentByPosition(int position) {
      return getSupportFragmentManager().findFragmentByTag(
          "android:switcher:" + mViewPager.getId() + ":" + getItemId(position));
    }

    public OfferListFragment getOfferFragment() {
      return (OfferListFragment) getFragmentByPosition(0);
    }

    public RewardListFragment getRewardFragment() {
      return (RewardListFragment) getFragmentByPosition(1);
    }

  }

}
