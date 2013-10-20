package com.kikbak.rest.admin;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kikbak.admin.service.AuthenticationException;
import com.kikbak.admin.service.AuthenticationService;
import com.kikbak.admin.service.MerchantService;
import com.kikbak.jaxb.admin.AddLocationRequest;
import com.kikbak.jaxb.admin.AddLocationResponse;
import com.kikbak.jaxb.admin.AddMerchantRequest;
import com.kikbak.jaxb.admin.AddMerchantResponse;
import com.kikbak.jaxb.admin.AddOfferRequest;
import com.kikbak.jaxb.admin.AddOfferResponse;
import com.kikbak.jaxb.admin.GetMerchantsRequest;
import com.kikbak.jaxb.admin.GetMerchantsResponse;
import com.kikbak.jaxb.admin.GetOffersRequest;
import com.kikbak.jaxb.admin.GetOffersResponse;
import com.kikbak.jaxb.admin.TokenType;
import com.kikbak.jaxb.statustype.StatusType;
import com.kikbak.rest.StatusCode;

@Controller
@RequestMapping("/merchant")
public class MerchantController {
	
	private static final Logger logger = Logger.getLogger(MerchantController.class);
	
	
	@Autowired
	MerchantService merchantService;
	
	@Autowired
	AuthenticationService authService;


	@RequestMapping(value = "/merchants", method = RequestMethod.POST)
	public GetMerchantsResponse getMerchants(@CookieValue("AuthToken") String authToken, @RequestBody GetMerchantsRequest request, final HttpServletResponse httpResponse){

		GetMerchantsResponse response = new GetMerchantsResponse();
		StatusType status = new StatusType();
		status.setCode(StatusCode.OK.ordinal());
		response.setStatus(status);
		try {
			//check for authentication
			TokenType token = new TokenType();
			token.setToken(authToken);
			authService.isValidToken(token);
			response.getMerchants().addAll(merchantService.getMerchants());
		} catch (AuthenticationException e) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status.setCode(StatusCode.AUTH_ERROR.ordinal());
			logger.error(e,e);
		}
		catch (Exception e) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status.setCode(StatusCode.ERROR.ordinal());
			logger.error(e,e);
		}
		
		return response;
	}
	
	@RequestMapping(value = "/addmerchant", method = RequestMethod.POST)
	public AddMerchantResponse addMerchant(@CookieValue("AuthToken") String authToken, @RequestBody AddMerchantRequest request, final HttpServletResponse httpResponse){
		
		AddMerchantResponse response = new AddMerchantResponse();
		StatusType status = new StatusType();
		status.setCode(StatusCode.OK.ordinal());
		response.setStatus(status);
		try{
			TokenType token = new TokenType();
			token.setToken(authToken);
			authService.isValidToken(token);
			response.setMerchant(merchantService.addOrUpdateMerchant(request.getMerchant()));
		} catch (AuthenticationException e) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status.setCode(StatusCode.AUTH_ERROR.ordinal());
			logger.error(e,e);
		}
		catch (Exception e) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status.setCode(StatusCode.ERROR.ordinal());
			logger.error(e,e);
		}
		
		return response;
	}
	
	@RequestMapping(value = "/addlocation", method = RequestMethod.POST)
	public AddLocationResponse addLocation(@CookieValue("AuthToken") String authToken, @RequestBody AddLocationRequest request, final HttpServletResponse httpResponse){
		
		AddLocationResponse response = new AddLocationResponse();
		StatusType status = new StatusType();
		status.setCode(StatusCode.OK.ordinal());
		response.setStatus(status);
		try{
			TokenType token = new TokenType();
			token.setToken(authToken);
			authService.isValidToken(token);
			response.setLocation(merchantService.addOrUpdateLocation(request.getLocation()));
		} catch (AuthenticationException e) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status.setCode(StatusCode.AUTH_ERROR.ordinal());
			logger.error(e,e);
		}
		catch (Exception e) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status.setCode(StatusCode.ERROR.ordinal());
			logger.error(e,e);
		}
		
		return response;
	}
	
	@RequestMapping(value = "/addOffer", method = RequestMethod.POST)
	public AddOfferResponse addOffer(@CookieValue("AuthToken") String authToken, @RequestBody AddOfferRequest request, final HttpServletResponse httpResponse){
		AddOfferResponse response = new AddOfferResponse();
		
		StatusType status = new StatusType();
		status.setCode(StatusCode.OK.ordinal());
		response.setStatus(status);
		try{
			TokenType token = new TokenType();
			token.setToken(authToken);
			authService.isValidToken(token);
			response.setOffer(merchantService.addOrUpdateOffer(request.getOffer()));
		} catch (AuthenticationException e) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status.setCode(StatusCode.AUTH_ERROR.ordinal());
			logger.error(e,e);
		}
		catch (Exception e) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status.setCode(StatusCode.ERROR.ordinal());
			logger.error(e,e);
		}
		
		return response;
	}
	
	@RequestMapping(value = "/getOffers", method = RequestMethod.GET)
	public GetOffersResponse getOffers(@CookieValue("AuthToken") String authToken, @RequestBody GetOffersRequest request, final HttpServletResponse httpResponse){
		GetOffersResponse response = new GetOffersResponse();
		StatusType status = new StatusType();
		status.setCode(StatusCode.OK.ordinal());
		response.setStatus(status);
		try{
			TokenType token = new TokenType();
			token.setToken(authToken);
			authService.isValidToken(token);
			response.getOffers().addAll(merchantService.getOffersByMerchant(request.getMerchant()));
		} catch (AuthenticationException e) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status.setCode(StatusCode.AUTH_ERROR.ordinal());
			logger.error(e,e);
		}
		catch (Exception e) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status.setCode(StatusCode.ERROR.ordinal());
			logger.error(e,e);
		}
		return response;
	}
}
