package com.kikbak.rest.client;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kikbak.client.service.UserService;
import com.kikbak.jaxb.devicetoken.DeviceTokenUpdateRequest;
import com.kikbak.jaxb.devicetoken.DeviceTokenUpdateResponse;
import com.kikbak.jaxb.friends.UpdateFriendResponse;
import com.kikbak.jaxb.friends.UpdateFriendsRequest;
import com.kikbak.jaxb.offer.GetUserOffersRequest;
import com.kikbak.jaxb.offer.GetUserOffersResponse;
import com.kikbak.jaxb.register.RegisterUserRequest;
import com.kikbak.jaxb.register.RegisterUserResponse;
import com.kikbak.jaxb.statustype.StatusType;
import com.kikbak.rest.StatusCode;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService service;

	@Autowired
	private PropertiesConfiguration config;
    
    static final String REFERRAL_CODE_KEY = "rc";
	
	private static final String WEB_CLIENT_LOGIN_URL = "web.client.login.page";
	
	private static final int COOKIE_EXPIRE_TIME = 10 * 365 * 24 * 60 * 60;

    private static final Logger logger = Logger.getLogger(UserController.class);
	
	@RequestMapping(value = "/claim/{referralCode}", method = RequestMethod.GET)
	public void claimGiftLogin(@PathVariable("referralCode") String referralCode, HttpServletResponse httpResponse) {
        Cookie cookie = new Cookie(REFERRAL_CODE_KEY, referralCode);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_EXPIRE_TIME);
    
        httpResponse.addCookie(cookie);
        try {
            String url = config.getString(WEB_CLIENT_LOGIN_URL);
            httpResponse.sendRedirect(url);
        } catch (IOException e) {
            logger.error("failed to redirect user", e); 
        } 
	}
	
	@RequestMapping(value = "/register/fb/",  method = RequestMethod.POST)
	public RegisterUserResponse registerFacebookUser(@RequestBody RegisterUserRequest request, final HttpServletResponse httpResponse){
		RegisterUserResponse response = new RegisterUserResponse();
		StatusType status = new StatusType();
		status.setCode(StatusCode.OK.ordinal());
		response.setStatus(status);
		try {
			response.setUserId(service.registerUser(request.getUser()));
		} catch (Exception e) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status.setCode(StatusCode.ERROR.ordinal());
			logger.error(e,e);
		}
		return response;
	}
	
	@RequestMapping(value="/friends/fb/{userId}", method=RequestMethod.POST)
	public UpdateFriendResponse updateFriends(@PathVariable("userId") Long userId,@RequestBody UpdateFriendsRequest request,
			final HttpServletResponse httpResponse){
		
		
		UpdateFriendResponse response = new UpdateFriendResponse();
		StatusType status = new StatusType();
		status.setCode(StatusCode.OK.ordinal());
		response.setStatus(status);
		try {
			service.updateFriends(userId, request.getFriends());
		} catch (Exception e) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status.setCode(StatusCode.ERROR.ordinal());
			logger.error(e,e);
		}
		
		return response;
	}
	
	@RequestMapping( value = "/offer/{userId}", method = RequestMethod.POST)
	public GetUserOffersResponse offersRequest(@PathVariable("userId") Long userId, 
					@RequestBody GetUserOffersRequest request, final HttpServletResponse httpResponse){
		
		GetUserOffersResponse response = new GetUserOffersResponse();
		StatusType status = new StatusType();
		status.setCode(StatusCode.OK.ordinal());
		response.setStatus(status);
		try {
			response.getOffers().addAll(service.getOffers(userId, request.getUserLocation()));
		} catch (Exception e) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status.setCode(StatusCode.ERROR.ordinal());
			logger.error(e,e);
		}
		return response;
	}
	
	@RequestMapping( value="/devicetoken/{userId}", method = RequestMethod.POST)
	public DeviceTokenUpdateResponse deviceTokenUpdate(@PathVariable("userId") Long userId,
					@RequestBody DeviceTokenUpdateRequest request, final HttpServletResponse httpResponse){
		
		DeviceTokenUpdateResponse response = new DeviceTokenUpdateResponse();
		StatusType status = new StatusType();
		status.setCode(StatusCode.OK.ordinal());
		response.setStatus(status);
		try {
			service.persistDeviceToken(userId, request.getToken());
		} catch (Exception e) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status.setCode(StatusCode.ERROR.ordinal());
			logger.error(e,e);
		}
		return response;
	}
}
