
package com.referredlabs.kikbak.ui;

import android.os.AsyncTask;

import com.referredlabs.kikbak.data.AvailableCreditType;
import com.referredlabs.kikbak.data.ClientMerchantType;
import com.referredlabs.kikbak.data.GiftType;
import com.referredlabs.kikbak.data.RewardsRequest;
import com.referredlabs.kikbak.data.RewardsResponse;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.utils.Register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RefreshRewardTask2 extends AsyncTask<Void, Void, Void> {

  public interface RefreshRewardListener {
    void onNewReward(List<Reward> rewards);
  }

  private long mUserId;
  List<Reward2> mRewards;
  RefreshRewardListener mListener;

  RefreshRewardTask2(RefreshRewardListener listener) {
    mListener = listener;
    mUserId = Register.getInstance().getUserId();
  }

  @Override
  protected Void doInBackground(Void... params) {
    try {
      String uri = Http.getUri(RewardsRequest.PATH + mUserId);
      RewardsRequest req = new RewardsRequest();
      RewardsResponse resp = Http.execute(uri, req, RewardsResponse.class);
      mRewards = getRewards(resp.gifts, resp.credits);

    } catch (Exception e) {
      android.util.Log.d("MMM", "Exception while fetching a redeem list:" + e);
    }
    return null;
  }

  private List<Reward2> getRewards(GiftType[] gifts, AvailableCreditType[] credits) {
    HashMap<Long, Reward2> map = new HashMap<Long, Reward2>();

    for (GiftType gift : gifts) {
      long id = gift.offerId;
      Reward2 entry = map.get(id);
      if (entry == null) {
        entry = new Reward2(id, gift.merchant);
        map.put(id, entry);
      }
      entry.addGift(gift);
    }

    for (AvailableCreditType credit : credits) {
      long id = credit.offerId;
      Reward2 entry = map.get(id);
      if (entry == null) {
        entry = new Reward2(id, credit.merchant);
        map.put(id, entry);
      }
      entry.addCredit(credit);
    }

    ArrayList<Reward2> result = new ArrayList<Reward2>(map.values());
    return result;
  }

  @Override
  protected void onPostExecute(Void result) {
    //mListener.onNewReward(mRewards);
  }

}
