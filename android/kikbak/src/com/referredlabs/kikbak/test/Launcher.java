
package com.referredlabs.kikbak.test;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.referredlabs.kikbak.data.AvailableCreditType;
import com.referredlabs.kikbak.data.ClientMerchantType;
import com.referredlabs.kikbak.data.DiscountType;
import com.referredlabs.kikbak.data.GiftType;
import com.referredlabs.kikbak.data.MerchantLocationType;
import com.referredlabs.kikbak.data.RedemptionLocationType;
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
    credit.rewardType = RewardType.purchase;
    credit.validationType = ValidationType.qrcode;
    credit.value = 72.23;
    return credit;
  }

  private static GiftType getSampleGift() {
    GiftType gift = new GiftType();
    gift.defaultGiveImageUrl = "http://192.168.0.101:8080/images/att_list.png";
    gift.desc = "Ala ma kota";
    gift.detailedDesc = "a kot ma ale";
    gift.discountType = DiscountType.amount;
    gift.merchant = new ClientMerchantType();
    gift.merchant.id = 0;
    gift.merchant.locations = new MerchantLocationType[1];
    gift.merchant.locations[0] = new MerchantLocationType();
    gift.merchant.name = "Biedronka";
    gift.merchant.url = "http://www.biendronka.pl";
    gift.offerId = 1;
    gift.redemptionLocationType = RedemptionLocationType.store;
    gift.shareInfo = null;
    gift.tosUrl = null;
    gift.validationType = ValidationType.qrcode;
    gift.value = 72.23;
    return gift;
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

  public static void startSuccessActivityCredit(Context ctx, String code) {
    AvailableCreditType credit = getSampleCredit();
    startSuccessActivity(ctx, code, credit);
  }

  public static void startSuccessActivityGift(Context ctx, String code) {
    GiftType gift = getSampleGift();
    startSuccessActivity(ctx, code, gift);
  }

  public static void startSuccessActivity(Context ctx, String code, AvailableCreditType credit) {
    String data = new Gson().toJson(credit);
    Intent intent = new Intent(ctx, SuccessActivity.class);
    intent.putExtra(SuccessActivity.ARG_BARCODE, code);
    intent.putExtra(SuccessActivity.ARG_CREDIT, data);
    ctx.startActivity(intent);
  }

  public static void startSuccessActivity(Context ctx, String code, GiftType gift) {
    String data = new Gson().toJson(gift);
    Intent intent = new Intent(ctx, SuccessActivity.class);
    intent.putExtra(SuccessActivity.ARG_BARCODE, code);
    intent.putExtra(SuccessActivity.ARG_GIFT, data);
    ctx.startActivity(intent);
  }

}
