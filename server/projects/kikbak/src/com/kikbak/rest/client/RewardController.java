package com.kikbak.rest.client;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kikbak.jaxb.RedemptionResponse;
import com.kikbak.jaxb.RewardsRequest;
import com.kikbak.jaxb.RewardsResponse;

@Controller
@RequestMapping("/reward")
public class RewardController {

	@RequestMapping(value = "/request/{userId}", method = RequestMethod.GET)
	public RewardsResponse rewardsRequest(@PathVariable String userId,
						@RequestBody RewardsRequest request, final HttpServletResponse response){
		
		return new RewardsResponse();
	}
	
	@RequestMapping(value="/redeem/{userId}", method = RequestMethod.POST)
	public RedemptionResponse redemptionRequest(@PathVariable String userId,
						@RequestBody RewardsRequest request, final HttpServletResponse response){
		
		return new RedemptionResponse();
	}
}
