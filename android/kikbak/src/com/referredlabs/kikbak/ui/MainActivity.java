
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
import android.widget.Toast;

import com.referredlabs.kikbak.LoginActivity;
import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.ClientOfferType;
import com.referredlabs.kikbak.data.GetUserOffersRequest;
import com.referredlabs.kikbak.data.GetUserOffersResponse;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.ui.OfferListFragment.OnOfferClickedListener;
import com.referredlabs.kikbak.ui.RewardListFragment.OnRewardClickedListener;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener,
    OnOfferClickedListener, OnRewardClickedListener {

  SectionsPagerAdapter mSectionsPagerAdapter;
  ViewPager mViewPager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
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
    startActivity(intent);
  }

  @Override
  public void onRewardClicked(Reward offer) {
    Intent intent = new Intent(this, RedeemActivity.class);
    startActivity(intent);
  }
}
