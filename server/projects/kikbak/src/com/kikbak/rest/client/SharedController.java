package com.kikbak.rest.client;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kikbak.client.service.SharedExperienceService;
import com.kikbak.jaxb.ShareExperienceRequest;
import com.kikbak.jaxb.ShareExperienceResponse;
import com.kikbak.jaxb.StatusType;
import com.kikbak.rest.StatusCode;

@Controller
@RequestMapping("/ShareExperience")
public class SharedController {

	private static final Logger logger = Logger.getLogger(SharedController.class);
	
	@Autowired
	SharedExperienceService service;
	
	@RequestMapping( value = "/{userId}", method = RequestMethod.POST)
	public ShareExperienceResponse postShareExperience(@PathVariable("userId") Long userId,
			@RequestBody ShareExperienceRequest experienceRequest, final HttpServletResponse httpResponse)
	{
		ShareExperienceResponse response = new ShareExperienceResponse();
		StatusType status = new StatusType();
		status.setCode(StatusCode.OK.ordinal());
		response.setStatus(status);
		try {
			service.registerSharing(userId, experienceRequest.getExperience());
		} catch (Exception e) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status.setCode(StatusCode.ERROR.ordinal());
			logger.error(e,e);
		}
		
		return response;
	}
}
