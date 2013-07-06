
package com.referredlabs.kikbak.ui;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.GetUserOffersRequest;
import com.referredlabs.kikbak.data.GetUserOffersResponse;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.ui.OfferListFragment.OnOfferClickedListener;
import com.referredlabs.kikbak.ui.RedeemChooserDialog.OnRedeemOptionSelectedListener;
import com.referredlabs.kikbak.ui.RewardListFragment.OnRewardClickedListener;
import com.referredlabs.kikbak.utils.Register;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener,
    OnOfferClickedListener, OnRewardClickedListener, OnRedeemOptionSelectedListener {

  SectionsPagerAdapter mSectionsPagerAdapter;
  ViewPager mViewPager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (!Register.getInstance().isRegistered()) {
      startActivity(new Intent(this, LoginActivity.class));
      finish();
      return;
    }

    setContentView(R.layout.activity_main);

    final ActionBar actionBar = getActionBar();
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

    mViewPager = (ViewPager) findViewById(R.id.pager);
    mViewPager.setAdapter(mSectionsPagerAdapter);
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
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_settings) {
      new Thread() {
        @Override
        public void run() {
          try {
            String uri = Http.getUri("/user/offer/1");
            GetUserOffersRequest req = GetUserOffersRequest.create(37.44, -122.17);
            GetUserOffersResponse resp = Http.execute(uri, req, GetUserOffersResponse.class);
            android.util.Log.d("MMM", "number of offers:" + resp.offers.length);
          } catch (Exception e) {
            // TODO: handle exception
          }
        }
      }.start();
    }
    if (item.getItemId() == R.id.action_login) {
      startActivity(new Intent(this, LoginActivity.class));
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

  public class SectionsPagerAdapter extends FragmentPagerAdapter {

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
  public void onRewardClicked(Reward offer) {
    if (offer.gift != null && offer.kikbak != null) {
      String gift = offer.getGiftValueString();
      String credit = offer.getCreditValueString();
      RedeemChooserDialog dialog = RedeemChooserDialog.newInstance(gift, credit);
      dialog.show(getSupportFragmentManager(), "");
    } else if (offer.gift != null) {
      onRedeemGiftSelected();
    } else if (offer.kikbak != null) {
      onRedeemCreditSelected();
    }
  }

  @Override
  public void onRedeemGiftSelected() {
    Intent intent = new Intent(this, RedeemGiftActivity.class);
    startActivity(intent);
  }

  @Override
  public void onRedeemCreditSelected() {
    Intent intent = new Intent(this, RedeemCreditActivity.class);
    startActivity(intent);
  }
}
