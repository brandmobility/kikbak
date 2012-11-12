package com.kikbak.rest.admin;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kikbak.jaxb.AddLocationRequest;
import com.kikbak.jaxb.AddLocationResponse;
import com.kikbak.jaxb.AddMerchantRequest;
import com.kikbak.jaxb.AddMerchantResponse;
import com.kikbak.jaxb.AddRewardRequest;
import com.kikbak.jaxb.AddRewardResponse;
import com.kikbak.jaxb.GetMerchantsRequest;
import com.kikbak.jaxb.GetMerchantsResponse;

@Controller
@RequestMapping("/Merchant")
public class MerchantController {

	@RequestMapping(value = "/merchants", method = RequestMethod.GET)
	public GetMerchantsResponse getMerchants(@RequestBody GetMerchantsRequest request, final HttpServletResponse response){
		
		return new GetMerchantsResponse();
	}
	
	@RequestMapping(value = "/addmerchant", method = RequestMethod.POST)
	public AddMerchantResponse addMerchant(@RequestBody AddMerchantRequest request, final HttpServletResponse response){
		
		return new AddMerchantResponse();
	}
	
	@RequestMapping(value = "/addlocation", method = RequestMethod.POST)
	public AddLocationResponse addLocation(@RequestBody AddLocationRequest request, final HttpServletResponse response){
		
		return new AddLocationResponse();
	}
	
	@RequestMapping(value = "/reward", method = RequestMethod.POST)
	public AddRewardResponse addReward(@RequestBody AddRewardRequest request, final HttpServletResponse response){
		
		return new AddRewardResponse();
	}
}
