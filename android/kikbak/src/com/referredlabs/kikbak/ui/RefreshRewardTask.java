
package com.referredlabs.kikbak.ui;

import android.os.AsyncTask;

import com.referredlabs.kikbak.data.ClientMerchantType;
import com.referredlabs.kikbak.data.GiftType;
import com.referredlabs.kikbak.data.KikbakType;
import com.referredlabs.kikbak.data.RewardsRequest;
import com.referredlabs.kikbak.data.RewardsResponse;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.utils.Register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RefreshRewardTask extends AsyncTask<Void, Void, Void> {

  public interface RefreshRewardListener {
    void onNewReward(List<Reward> rewards);
  }

  private long mUserId;
  List<Reward> mRewards;
  RefreshRewardListener mListener;

  RefreshRewardTask(RefreshRewardListener listener) {
    mListener = listener;
    mUserId = Register.getInstance().getUserId();
  }

  @Override
  protected Void doInBackground(Void... params) {
    try {
      String uri = Http.getUri(RewardsRequest.PATH + mUserId);
      RewardsRequest req = new RewardsRequest();
      RewardsResponse resp = Http.execute(uri, req, RewardsResponse.class);
      mRewards = getRewards(resp.gifts, resp.kikbaks);

    } catch (Exception e) {
      android.util.Log.d("MMM", "Exception while fetching a redeem list:" + e);
    }
    return null;
  }

  private List<Reward> getRewards(GiftType[] gifts, KikbakType[] kikbaks) {
    HashMap<Long, Reward> map = new HashMap<Long, Reward>();

    for (GiftType gift : gifts) {
      ClientMerchantType merchant = gift.merchant;
      if (merchant != null) {
        Reward entry = new Reward(merchant);
        entry.gift = gift;
        map.put(merchant.id, entry);
      }
    }

    for (KikbakType kikbak : kikbaks) {
      ClientMerchantType merchant = kikbak.merchant;
      if (merchant != null) {
        Reward entry = map.get(merchant.id);
        if (entry == null) {
          entry = new Reward(merchant);
          map.put(merchant.id, entry);
        }
        entry.kikbak = kikbak;
      }
    }

    ArrayList<Reward> result = new ArrayList<Reward>(map.values());
    return result;
  }

  @Override
  protected void onPostExecute(Void result) {
    mListener.onNewReward(mRewards);
  }

}
