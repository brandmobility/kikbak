package com.kikbak.rest.client;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kikbak.jaxb.ShareExperienceRequest;
import com.kikbak.jaxb.ShareExperienceResponse;

@Controller
@RequestMapping("/ShareExperience")
public class ShareExperienceController {

	@RequestMapping( value = "/{fbUserId}", method = RequestMethod.POST)
	public ShareExperienceResponse postShareExperience(@PathVariable String fbUserId,
			@RequestBody ShareExperienceRequest experienceRequest, final HttpServletResponse httpResponse)
	{
		
		return new ShareExperienceResponse();
	}
}
