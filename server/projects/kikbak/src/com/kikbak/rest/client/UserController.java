package com.kikbak.rest.client;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kikbak.client.service.UserService;
import com.kikbak.jaxb.DeviceTokenUpdateRequest;
import com.kikbak.jaxb.DeviceTokenUpdateResponse;
import com.kikbak.jaxb.GetUserOffersRequest;
import com.kikbak.jaxb.GetUserOffersResponse;
import com.kikbak.jaxb.RegisterUserRequest;
import com.kikbak.jaxb.RegisterUserResponse;
import com.kikbak.jaxb.StatusType;
import com.kikbak.jaxb.UpdateFriendResponse;
import com.kikbak.jaxb.UpdateFriendsRequest;
import com.kikbak.rest.StatusCode;

@Controller
@RequestMapping("/user")
public class UserController {
	
	private static final Logger logger = Logger.getLogger(UserController.class);
	
	@Autowired
	UserService service;

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
	public UpdateFriendResponse updateFriends(@PathVariable String userId,@RequestBody UpdateFriendsRequest request,
			final HttpServletResponse httpResponse){
		
		UpdateFriendResponse response = new UpdateFriendResponse();
		StatusType status = new StatusType();
		status.setCode(StatusCode.OK.ordinal());
		response.setStatus(status);
		try {
			service.updateFriends(Long.parseLong(userId), request.getFriends());
		} catch (Exception e) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status.setCode(StatusCode.ERROR.ordinal());
			logger.error(e,e);
		}
		
		return new UpdateFriendResponse();
	}
	
	@RequestMapping( value = "/offer/{userId}", method = RequestMethod.POST)
	public GetUserOffersResponse offersRequest(@PathVariable Long userId, 
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
	public DeviceTokenUpdateResponse deviceTokenUpdate(@PathVariable Long userId,
					@RequestBody DeviceTokenUpdateRequest request, final HttpServletResponse httpResponse){
		
		return new DeviceTokenUpdateResponse();
	}
}
