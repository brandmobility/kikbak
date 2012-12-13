package com.kikbak.rest.client;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kikbak.client.service.RewardService;
import com.kikbak.jaxb.RedeemGiftRequest;
import com.kikbak.jaxb.RedeemGiftResponse;
import com.kikbak.jaxb.RedeemKikbakRequest;
import com.kikbak.jaxb.RedeemKikbakResponse;
import com.kikbak.jaxb.RewardsRequest;
import com.kikbak.jaxb.RewardsResponse;
import com.kikbak.jaxb.StatusType;
import com.kikbak.rest.StatusCode;

@Controller
@RequestMapping("/rewards")
public class RewardController {

	private static final Logger logger = Logger.getLogger(RewardController.class);
	
	@Autowired
	RewardService service;
	
	@RequestMapping(value = "/request/{userId}", method = RequestMethod.POST)
	public RewardsResponse rewards(@PathVariable("userId") Long userId,
						@RequestBody RewardsRequest request, final HttpServletResponse httpResponse){
		
		RewardsResponse response = new RewardsResponse();
		StatusType status = new StatusType();
		status.setCode(StatusCode.OK.ordinal());
		response.setStatus(status);
		try {
			response.getGifts().addAll(service.getGifts(userId));
			response.getKikbaks().addAll(service.getKikbaks(userId));
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
		} catch (Exception e) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status.setCode(StatusCode.ERROR.ordinal());
			logger.error(e,e);
		}
		
		return response;
	}
	
	@RequestMapping(value="/redeem/kikbak/{userId}", method = RequestMethod.POST)
	public RedeemKikbakResponse redeemKikbak(@PathVariable("userId") Long userId,
						@RequestBody RedeemKikbakRequest request, final HttpServletResponse httpResponse){
		
		RedeemKikbakResponse response = new RedeemKikbakResponse();
		StatusType status = new StatusType();
		status.setCode(StatusCode.OK.ordinal());
		response.setStatus(status);
		try {
			response.setAuthorizationCode(service.registerKikbakRedemption(userId, request.getKikbak()));
		} catch (Exception e) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status.setCode(StatusCode.ERROR.ordinal());
			logger.error(e,e);
		}
		return response;
	}
}
