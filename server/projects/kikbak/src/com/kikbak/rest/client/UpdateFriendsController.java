package com.kikbak.rest.client;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kikbak.jaxb.UpdateFriendResponse;
import com.kikbak.jaxb.UpdateFriendsRequest;

@Controller
@RequestMapping("/UpdateFriends")
public class UpdateFriendsController {

	@RequestMapping(value="/{fbUserId}", method=RequestMethod.POST)
	public UpdateFriendResponse updateFriends(@PathVariable String fbUserId,@RequestBody UpdateFriendsRequest request,
			final HttpServletResponse response){
		
		return new UpdateFriendResponse();
	}
	
}
