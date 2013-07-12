package com.kikbak.rest.client;

import java.util.LinkedList;
import java.util.List;

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
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.UPCAWriter;
import com.kikbak.client.service.RateLimitException;
import com.kikbak.client.service.RedemptionException;
import com.kikbak.client.service.RewardService;
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
import com.kikbak.jaxb.statustype.StatusType;
import com.kikbak.rest.StatusCode;

@Controller
@RequestMapping("/rewards")
public class RewardController {

	private static final Logger logger = Logger.getLogger(RewardController.class);
	
	@Autowired
	RewardService service;
    
    @RequestMapping(value = "/claim/{userId}/{referralCode}", method = RequestMethod.GET)
    public ClaimGiftResponse claimGift(@PathVariable("userId") Long userId,
                        @PathVariable("referralCode") String referralCode, final HttpServletResponse httpResponse){
        
        ClaimGiftResponse response = new ClaimGiftResponse();
        StatusType status = new StatusType();
        status.setCode(StatusCode.OK.ordinal());
        response.setStatus(status);
        try {
            List<GiftType> gifts = new LinkedList<GiftType>(); 
            ClaimStatusType claimStatusType = service.claimGift(userId, referralCode, gifts);
            response.getGifts().addAll(gifts);
            response.setClaimStatus(claimStatusType);
            if (response.getGifts().isEmpty()) {
                status.setCode(StatusCode.ERROR.ordinal());
            }
        } catch (Exception e) {
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            status.setCode(StatusCode.ERROR.ordinal());
            logger.error(e,e);
        }
        return response;
    }
    
    @RequestMapping(value = "/claim/{userId}", method = RequestMethod.GET)
    public ClaimGiftResponse claimGiftViaCookie(@PathVariable("userId") Long userId,
            final HttpServletRequest request,  final HttpServletResponse httpResponse){

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
	public RewardsResponse rewards(@PathVariable("userId") Long userId,
						@RequestBody RewardsRequest request, final HttpServletResponse httpResponse){
		
		RewardsResponse response = new RewardsResponse();
		StatusType status = new StatusType();
		status.setCode(StatusCode.OK.ordinal());
		response.setStatus(status);
		try {
			response.getGifts().addAll(service.getGifts(userId));
			response.getCredits().addAll(service.getCredits(userId));
		} catch (Exception e) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status.setCode(StatusCode.ERROR.ordinal());
			logger.error(e,e);
		}
		return response;
	}
	
	@RequestMapping(value="/redeem/gift/{userId}", method = RequestMethod.POST)
	public RedeemGiftResponse redeemGift(@PathVariable("userId") Long userId,
						@RequestBody RedeemGiftRequest request, final HttpServletResponse httpResponse){
		
		RedeemGiftResponse response = new RedeemGiftResponse();
		StatusType status = new StatusType();
		status.setCode(StatusCode.OK.ordinal());
		response.setStatus(status);
		try {
			response.setAuthorizationCode(service.registerGiftRedemption(userId, request.getGift()));
		}
		catch(RedemptionException e){
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setRedeemGiftStatus(RedeemGiftStatus.INVALID_CODE);
			status.setCode(StatusCode.ERROR.ordinal());
			logger.error(e,e);
		}
        catch(RateLimitException e){
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setRedeemGiftStatus(RedeemGiftStatus.LIMIT_REACH);
            status.setCode(StatusCode.ERROR.ordinal());
            logger.error(e,e);
        }
		catch (RuntimeException e) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status.setCode(StatusCode.ERROR.ordinal());
			logger.error(e,e);
		}
		
		return response;
	}
	
	@RequestMapping(value="/redeem/credit/{userId}", method = RequestMethod.POST)
	public RedeemCreditResponse redeemKikbak(@PathVariable("userId") Long userId,
						@RequestBody RedeemCreditRequest request, final HttpServletResponse httpResponse){
		
	    RedeemCreditResponse response = new RedeemCreditResponse();
		StatusType status = new StatusType();
		status.setCode(StatusCode.OK.ordinal());
		response.setStatus(status);
		try {
			response.setResponse(service.redeemCredit(userId, request.getCredit()));
		} catch (Exception e) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status.setCode(StatusCode.ERROR.ordinal());
			logger.error(e,e);
		}
		return response;
	}
    
    @RequestMapping(value="/generateBarcode/{userId}/{code}/{height}/{width}/", method = RequestMethod.GET)
    public void generateBarcode(@PathVariable("userId") Long userId,
            @PathVariable("code") String code, @PathVariable("height") Integer height,
            @PathVariable("width") Integer width, final HttpServletResponse response) {
        try {
            BitMatrix result = new UPCAWriter().encode("485963095124", BarcodeFormat.UPC_A, width, height);
            response.setContentType("image/png");
            MatrixToImageWriter.writeToStream(result, "png", response.getOutputStream());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logger.error("cannot generate barcode", e);
        }
    }
}
