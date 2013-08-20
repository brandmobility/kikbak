package com.kikbak.rest.client;

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

import com.kikbak.client.service.FbLoginService;
import com.kikbak.client.service.UserService;
import com.kikbak.client.service.impl.CookieAuthenticationFilter;
import com.kikbak.jaxb.devicetoken.DeviceTokenUpdateRequest;
import com.kikbak.jaxb.devicetoken.DeviceTokenUpdateResponse;
import com.kikbak.jaxb.friends.UpdateFriendResponse;
import com.kikbak.jaxb.friends.UpdateFriendsRequest;
import com.kikbak.jaxb.offer.GetUserOffersRequest;
import com.kikbak.jaxb.offer.GetUserOffersResponse;
import com.kikbak.jaxb.register.RegisterUserRequest;
import com.kikbak.jaxb.register.RegisterUserResponse;
import com.kikbak.jaxb.register.UserType;
import com.kikbak.jaxb.statustype.StatusType;
import com.kikbak.rest.StatusCode;

@Controller
@RequestMapping("/user")
public class UserController extends AbstractController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private FbLoginService fbLoginService;

	@Autowired
	private PropertiesConfiguration config;
    
    static final String REFERRAL_CODE_KEY = "rc";
	
	private static final String USER_COOKIE_SECURE = "user.cookie.secure";
		
	private static final int COOKIE_EXPIRE_TIME = 10 * 365 * 24 * 60 * 60;

    private static final Logger logger = Logger.getLogger(UserController.class);
		
	@RequestMapping(value = "/register/fb/",  method = RequestMethod.POST)
	public RegisterUserResponse registerFacebookUser(@RequestBody RegisterUserRequest request, final HttpServletResponse httpResponse){
		RegisterUserResponse response = new RegisterUserResponse();
		StatusType status = new StatusType();
		status.setCode(StatusCode.OK.ordinal());
		response.setStatus(status);
		try {
			UserType user = fbLoginService.getUserInfo(request.getUser().getAccessToken());
			response.setUserId(userService.registerUser(user));
			String token = userService.getUserToken(response.getUserId().getUserId());
			Cookie cookie = new Cookie(CookieAuthenticationFilter.COOKIE_TOKEN_KEY, token);
			if (config.getBoolean(USER_COOKIE_SECURE)) {
				cookie.setSecure(true);
			}
			cookie.setPath("/");
			cookie.setMaxAge(COOKIE_EXPIRE_TIME);
			httpResponse.addCookie(cookie);
			
			cookie = new Cookie(CookieAuthenticationFilter.COOKIE_USER_ID_KEY, Long.toString(response.getUserId().getUserId()));
			if (config.getBoolean(USER_COOKIE_SECURE)) {
				cookie.setSecure(true);
			}
			cookie.setPath("/");
			cookie.setMaxAge(COOKIE_EXPIRE_TIME);
			httpResponse.addCookie(cookie);

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

    	try {
    		Long actualUserId = getCurrentUserId();
    		if (!userId.equals(actualUserId)) {
                logger.error("Wrong user expect " + userId + " actually " + actualUserId);
    			httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return null;
    		}
    	} catch (Exception e) {
            logger.error(e,e);
			httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
    	}
		
		UpdateFriendResponse response = new UpdateFriendResponse();
		StatusType status = new StatusType();
		status.setCode(StatusCode.OK.ordinal());
		response.setStatus(status);
		try {
			userService.updateFriends(userId, request.getFriends());
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
			response.getOffers().addAll(userService.getOffers(userId, request.getUserLocation()));
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

    	try {
    		Long actualUserId = getCurrentUserId();
    		if (!userId.equals(actualUserId)) {
                logger.error("Wrong user expect " + userId + " actually " + actualUserId);
    			httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return null;
    		}
    	} catch (Exception e) {
            logger.error(e,e);
			httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
    	}
    	
		DeviceTokenUpdateResponse response = new DeviceTokenUpdateResponse();
		StatusType status = new StatusType();
		status.setCode(StatusCode.OK.ordinal());
		response.setStatus(status);
		try {
			userService.persistDeviceToken(userId, request.getToken());
		} catch (Exception e) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status.setCode(StatusCode.ERROR.ordinal());
			logger.error(e,e);
		}
		return response;
	}
}
