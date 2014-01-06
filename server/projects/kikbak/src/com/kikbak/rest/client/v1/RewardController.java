package com.kikbak.rest.client.v1;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.kikbak.client.service.v1.OfferExhaustedException;
import com.kikbak.client.service.v1.OfferExpiredException;
import com.kikbak.client.service.v1.RateLimitException;
import com.kikbak.client.service.v1.RedemptionException;
import com.kikbak.client.service.v1.RewardService;
import com.kikbak.jaxb.v1.barcode.BarcodeResponse;
import com.kikbak.jaxb.v1.claim.ClaimCreditRequest;
import com.kikbak.jaxb.v1.claim.ClaimCreditResponse;
import com.kikbak.jaxb.v1.redeemcredit.RedeemCreditRequest;
import com.kikbak.jaxb.v1.redeemcredit.RedeemCreditResponse;
import com.kikbak.jaxb.v1.redeemcredit.RedeemCreditStatus;
import com.kikbak.jaxb.v1.redeemgift.RedeemGiftRequest;
import com.kikbak.jaxb.v1.redeemgift.RedeemGiftResponse;
import com.kikbak.jaxb.v1.redeemgift.RedeemGiftStatus;
import com.kikbak.jaxb.v1.rewards.ClaimGiftResponse;
import com.kikbak.jaxb.v1.rewards.ClaimStatusType;
import com.kikbak.jaxb.v1.rewards.GiftType;
import com.kikbak.jaxb.v1.rewards.RewardsRequest;
import com.kikbak.jaxb.v1.rewards.RewardsResponse;
import com.kikbak.jaxb.v1.statustype.SuccessStatus;

@Controller
@RequestMapping("/rewards")
public class RewardController extends AbstractController {

    private static final Logger logger = Logger.getLogger(RewardController.class);

    @Autowired
    RewardService service;

    @RequestMapping(value = "/claimgift/{userId}/{referralCode}", method = RequestMethod.GET)
    public ClaimGiftResponse claimGift(@PathVariable("userId") Long userId,
            @PathVariable("referralCode") String referralCode,
            final HttpServletRequest request, final HttpServletResponse httpResponse) {
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

    @RequestMapping(value = "/request/{userId}", method = RequestMethod.POST)
    public RewardsResponse rewards(@PathVariable("userId") Long userId, @RequestBody RewardsRequest request,
            final HttpServletResponse httpResponse) {
        try {
            ensureCorrectUser(userId);

            RewardsResponse response = new RewardsResponse();
            response.getGifts().addAll(service.getGifts(userId, request.getUserLocation()));
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
            response.setStatus(RedeemCreditStatus.OK);
            return response;
        } catch (WrongUserException e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        } catch (RedemptionException e) {
            logger.error(e, e);
            RedeemCreditResponse response = new RedeemCreditResponse();
            response.setStatus(RedeemCreditStatus.INVALID_CODE);
            return response;
        }
    }
    
    
    @RequestMapping(value = "/allocateBarcode/{userId}/{allocatedGiftId}/", method = RequestMethod.GET)
    public BarcodeResponse allocateBarcode(@PathVariable("userId") Long userId,
            @PathVariable("allocatedGiftId") Long allocatedGiftId, final HttpServletResponse httpResponse) {
    	try {
            ensureCorrectUser(userId);

            BarcodeResponse code = new BarcodeResponse();
            service.getBarcode(userId, allocatedGiftId, code);
            return code;
        } catch (WrongUserException e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        } catch (Exception e) {
            logger.error("cannot generate barcode", e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    @RequestMapping(value = "/generateBarcode/{userId}/{barcode}/{height}/{width}/", method = RequestMethod.GET)
    public void generateBarcode(@PathVariable("userId") Long userId,
            @PathVariable("barcode") String barcode, @PathVariable("height") Integer height,
            @PathVariable("width") Integer width, final HttpServletResponse httpResponse) {

        try {
            ensureCorrectUser(userId);

            BitMatrix result = new Code128Writer().encode(barcode, BarcodeFormat.CODE_128, width, height);
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
        } catch (IllegalArgumentException e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
    
    @RequestMapping(value = "/allocateAnonymousBarcode/{refferalCode}", method = RequestMethod.GET)
    public BarcodeResponse allocateAnonymousBarcode(@PathVariable("refferalCode") String referralCode,
            final HttpServletResponse httpResponse) {
        try {
            BarcodeResponse response = new BarcodeResponse();
            service.getBarcode(referralCode, response);
            return response;
        } catch (IllegalArgumentException e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;            
        } catch (OfferExpiredException e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_GONE);
            return null;
        } catch (OfferExhaustedException e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return null;
        } catch (Exception e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

}
