
package com.referredlabs.kikbak.test;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.referredlabs.kikbak.data.AvailableCreditType;
import com.referredlabs.kikbak.data.ClientMerchantType;
import com.referredlabs.kikbak.data.MerchantLocationType;
import com.referredlabs.kikbak.data.RewardType;
import com.referredlabs.kikbak.data.ValidationType;
import com.referredlabs.kikbak.ui.RedeemCreditActivity;
import com.referredlabs.kikbak.ui.SuccessActivity;

public class Launcher {

  private static AvailableCreditType getSampleCredit() {
    AvailableCreditType credit = new AvailableCreditType();
    credit.desc = "Ala ma kota";
    credit.detailedDesc = "a kot ma ale";
    credit.id = 3;
    credit.imageUrl = "http://192.168.0.101:8080/images/att_list.png";
    credit.merchant = new ClientMerchantType();
    credit.merchant.id = 0;
    credit.merchant.locations = new MerchantLocationType[1];
    credit.merchant.locations[0] = new MerchantLocationType();
    credit.merchant.name = "Biedronka";
    credit.merchant.url = "http://www.biendronka.pl";
    credit.offerId = 1;
    credit.redeemedGiftsCount = 5;
    credit.rewardType = RewardType.gift_card;
    credit.validationType = ValidationType.qrcode;
    credit.value = 72.23;
    return credit;
  }

  public static void startRedeemCreditActivity(Context ctx) {
    AvailableCreditType credit = getSampleCredit();
    startRedeemCreditActivity(ctx, credit);
  }

  public static void startRedeemCreditActivity(Context ctx, AvailableCreditType credit) {
    Intent intent = new Intent(ctx, RedeemCreditActivity.class);
    String data = new Gson().toJson(credit);
    intent.putExtra(RedeemCreditActivity.EXTRA_CREDIT, data);
    ctx.startActivity(intent);
  }

  public static void startSuccessActivity(Context ctx, String code) {
    AvailableCreditType credit = getSampleCredit();
    startSuccessActivity(ctx, code, credit);
  }

  public static void startSuccessActivity(Context ctx, String code, AvailableCreditType credit) {
    String data = new Gson().toJson(credit);
    Intent intent = new Intent(ctx, SuccessActivity.class);
    // intent.putExtra(SuccessActivity.ARG_BARCODE_BITMAP, barcodeBitmap);
    intent.putExtra(SuccessActivity.ARG_BARCODE, code);
    intent.putExtra(SuccessActivity.ARG_CREDIT, data);
    ctx.startActivity(intent);
  }

}
