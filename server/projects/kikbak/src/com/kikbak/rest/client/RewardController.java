package com.kikbak.rest.client;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.UPCAWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.kikbak.client.service.RateLimitException;
import com.kikbak.client.service.RedemptionException;
import com.kikbak.client.service.RewardService;
import com.kikbak.jaxb.claim.ClaimCreditRequest;
import com.kikbak.jaxb.claim.ClaimCreditResponse;
import com.kikbak.jaxb.redeemcredit.RedeemCreditRequest;
import com.kikbak.jaxb.redeemcredit.RedeemCreditResponse;
import com.kikbak.jaxb.redeemgift.RedeemGiftRequest;
import com.kikbak.jaxb.redeemgift.RedeemGiftResponse;
import com.kikbak.jaxb.redeemgift.RedeemGiftStatus;
import com.kikbak.jaxb.rewards.ClaimGiftResponse;
import com.kikbak.jaxb.rewards.ClaimStatusType;
import com.kikbak.jaxb.rewards.GiftType;
import com.kikbak.jaxb.rewards.RewardsRequest;
import com.kikbak.jaxb.rewards.RewardsResponse;
import com.kikbak.jaxb.statustype.SuccessStatus;

@Controller
@RequestMapping("/rewards")
public class RewardController extends AbstractController {

    private static final Logger logger = Logger.getLogger(RewardController.class);

    @Autowired
    RewardService service;

    @RequestMapping(value = "/claim/{userId}/{referralCode}", method = RequestMethod.GET)
    public ClaimGiftResponse claimGift(@PathVariable("userId") Long userId,
            @PathVariable("referralCode") String referralCode, final HttpServletResponse httpResponse) {
        try {
            ensureCorrectUser(userId);

            ClaimGiftResponse response = new ClaimGiftResponse();
            List<GiftType> gifts = new LinkedList<GiftType>();
            List<Long> agIds = new LinkedList<Long>();
            ClaimStatusType status = service.claimGift(userId, referralCode, gifts, agIds);
            response.setStatus(status);
            response.getGifts().addAll(gifts);
            if (!agIds.isEmpty()) {
                response.setAgId(agIds.get(0));
            }
            return response;
        } catch (WrongUserException e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        } catch (Exception e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    @RequestMapping(value = "/claim/{userId}", method = RequestMethod.GET)
    public ClaimGiftResponse claimGiftViaCookie(@PathVariable("userId") Long userId, final HttpServletRequest request,
            final HttpServletResponse httpResponse) {

        Cookie[] cookies = request.getCookies();
        String referralCode = null;
        for (Cookie cookie : cookies) {
            if (StringUtils.equals(UserController.REFERRAL_CODE_KEY, cookie.getName())) {
                referralCode = cookie.getValue();
                break;
            }
        }

        return claimGift(userId, referralCode, httpResponse);
    }

    @RequestMapping(value = "/request/{userId}", method = RequestMethod.POST)
    public RewardsResponse rewards(@PathVariable("userId") Long userId, @RequestBody RewardsRequest request,
            final HttpServletResponse httpResponse) {
        try {
            ensureCorrectUser(userId);

            RewardsResponse response = new RewardsResponse();
            response.getGifts().addAll(service.getGifts(userId));
            response.getCredits().addAll(service.getCredits(userId));
            return response;
        } catch (WrongUserException e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        } catch (Exception e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    @RequestMapping(value = "/redeem/gift/{userId}", method = RequestMethod.POST)
    public RedeemGiftResponse redeemGift(@PathVariable("userId") Long userId, @RequestBody RedeemGiftRequest request,
            final HttpServletResponse httpResponse) {

        RedeemGiftResponse response = new RedeemGiftResponse();
        try {
            ensureCorrectUser(userId);

            response.setStatus(RedeemGiftStatus.OK);
            response.setAuthorizationCode(service.registerGiftRedemption(userId, request.getGift()));
            return response;
        } catch (RedemptionException e) {
            logger.error(e, e);
            response.setStatus(RedeemGiftStatus.INVALID_CODE);
            return response;
        } catch (RateLimitException e) {
            logger.error(e, e);
            response.setStatus(RedeemGiftStatus.LIMIT_REACH);
            return response;
        } catch (WrongUserException e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        } catch (Exception e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    @RequestMapping(value = "/redeem/credit/{userId}", method = RequestMethod.POST)
    public RedeemCreditResponse redeemKikbak(@PathVariable("userId") Long userId,
            @RequestBody RedeemCreditRequest request, final HttpServletResponse httpResponse) {
        try {
            ensureCorrectUser(userId);

            RedeemCreditResponse response = new RedeemCreditResponse();
            response.setResponse(service.redeemCredit(userId, request.getCredit()));
            return response;
        } catch (WrongUserException e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        } catch (Exception e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    @RequestMapping(value = "/generateBarcode/{userId}/{allocatedGiftId}/{height}/{width}/", method = RequestMethod.GET)
    public void generateBarcode(@PathVariable("userId") Long userId,
            @PathVariable("allocatedGiftId") Long allocatedGiftId, @PathVariable("height") Integer height,
            @PathVariable("width") Integer width, final HttpServletResponse httpResponse) {

        try {
            ensureCorrectUser(userId);

            String barcode = service.getBarcode(userId, allocatedGiftId);
            BitMatrix result = new UPCAWriter().encode(barcode, BarcodeFormat.UPC_A, width, height);
            httpResponse.setContentType("image/png");
            httpResponse.addHeader("barcode", barcode);
            MatrixToImageWriter.writeToStream(result, "png", httpResponse.getOutputStream());
        } catch (WrongUserException e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        } catch (Exception e) {
            logger.error("cannot generate barcode", e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
    }

    @RequestMapping(value = "/claim/{userId}/", method = RequestMethod.POST)
    public ClaimCreditResponse claimCredit(@PathVariable("userId") Long userId,
            @RequestBody ClaimCreditRequest request, final HttpServletResponse httpResponse) {
        try {
            ensureCorrectUser(userId);

            ClaimCreditResponse response = new ClaimCreditResponse();
            response.setStatus(SuccessStatus.OK);
            service.claimCredit(userId, request.getClaim());
            return response;
        } catch (WrongUserException e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        } catch (Exception e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    @RequestMapping(value = "/generateQrcode/{code}/{size}", method = RequestMethod.GET)
    public void generateQrcode(@PathVariable("code") String code, @PathVariable("size") Integer size,
            final HttpServletResponse response) {
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.MARGIN, 0);
            BitMatrix result = new QRCodeWriter().encode(code, BarcodeFormat.QR_CODE, size, size, hints);
            response.setContentType("image/png");
            response.addHeader("qrcode", code);
            MatrixToImageWriter.writeToStream(result, "png", response.getOutputStream());
        } catch (Exception e) {
            logger.error("cannot generate barcode", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
