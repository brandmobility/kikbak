package com.kikbak.rest.client;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kikbak.client.service.SharedExperienceService;
import com.kikbak.dao.ReadOnlyGiftDAO;
import com.kikbak.dao.ReadOnlyLocationDAO;
import com.kikbak.dao.ReadOnlyMerchantDAO;
import com.kikbak.dao.ReadOnlyUserDAO;
import com.kikbak.dto.Gift;
import com.kikbak.dto.Location;
import com.kikbak.dto.Merchant;
import com.kikbak.dto.User;
import com.kikbak.jaxb.share.MessageTemplateType;
import com.kikbak.jaxb.share.ShareExperienceRequest;
import com.kikbak.jaxb.share.ShareExperienceResponse;
import com.kikbak.jaxb.share.SharedType;
import com.kikbak.jaxb.statustype.StatusType;
import com.kikbak.rest.StatusCode;

@Controller
@RequestMapping("/ShareExperience")
public class SharedController {

	private static final Logger logger = Logger.getLogger(SharedController.class);
	
	@Autowired
	private PropertiesConfiguration config;
	
	@Autowired
	private SharedExperienceService service;
	
	@Autowired
	private ReadOnlyUserDAO readOnlyUserDAO;
	
	@Autowired
	private ReadOnlyMerchantDAO readOnlyMerchantDAO;
	
	@Autowired
	private ReadOnlyLocationDAO readOnlyLocationDAO;
	
	@Autowired
	private ReadOnlyGiftDAO readOnlyGiftDAO;
	
	@RequestMapping( value = "/{userId}", method = RequestMethod.POST)
	public ShareExperienceResponse postShareExperience(@PathVariable("userId") Long userId,
			@RequestBody ShareExperienceRequest experienceRequest, final HttpServletResponse httpResponse)
	{
		ShareExperienceResponse response = new ShareExperienceResponse();
		StatusType status = new StatusType();
		status.setCode(StatusCode.OK.ordinal());
		response.setStatus(status);
		SharedType experience = experienceRequest.getExperience();
		try {
			response.setReferrerCode(service.registerSharing(userId, experience));
		} catch (Exception e) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status.setCode(StatusCode.ERROR.ordinal());
			logger.error(e,e);
			return response;
		}
		
		String titleKey = "share.template.title" + experience.getType().toLowerCase() + "." 
				+ experience.getPlatform().toLowerCase();

		String bodyKey = "share.template.body" + experience.getType().toLowerCase() + "." 
				+ experience.getPlatform().toLowerCase();

		String loginUrl = config.getString("share.template.login.url").replace("%CODE%", response.getReferrerCode());
				
		String titleTemplate = config.getString(titleKey, "");
		String bodyTemplate = config.getString(bodyKey, "");

		MessageTemplateType template = new MessageTemplateType();
		response.setTemplate(template);
		
		try {
			if (StringUtils.isNotBlank(bodyTemplate)) {
				User user = readOnlyUserDAO.findById(userId);
				Location location = readOnlyLocationDAO.findById(experience.getLocationId());
				Merchant merchant = readOnlyMerchantDAO.findById(experience.getMerchantId());
				Gift gift = readOnlyGiftDAO.findByOfferId(experience.getOfferId());
				if (StringUtils.isNotBlank(titleTemplate)) {
					fillTemplate(experience, loginUrl, titleTemplate, user,
							location, merchant, gift);
					template.setSubject(titleTemplate);
				}
				if (StringUtils.isNotBlank(bodyTemplate)) {
					fillTemplate(experience, loginUrl, bodyTemplate, user,
							location, merchant, gift);
					template.setSubject(bodyTemplate);
				}
			}
		}  catch (Exception e) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status.setCode(StatusCode.ERROR.ordinal());
			logger.error(e,e);
			return response;
		}
		
		return response;
	}

	private void fillTemplate(SharedType experience, String loginUrl,
			String titleTemplate, User user, Location location,
			Merchant merchant, Gift gift) {
		titleTemplate = titleTemplate.replace("%CAPTION%", experience.getCaption());
		titleTemplate = titleTemplate.replace("%EMPLOYEE_ID%", StringUtils.isNotBlank(experience.getEmployeeId()) ? experience.getEmployeeId() : "employee");
		titleTemplate = titleTemplate.replace("%IMAGE_URL%", experience.getImageUrl());
		titleTemplate = titleTemplate.replace("%DESC%", gift.getDescription());
		titleTemplate = titleTemplate.replace("%DESC_DETAIL%", gift.getDetailedDesc());
		titleTemplate = titleTemplate.replace("%LOGIN_URL%", loginUrl);
		titleTemplate = titleTemplate.replace("%MERCHANT%", merchant.getName());
		titleTemplate = titleTemplate.replace("%NAME%", user.getFirstName() + " " + user.getLastName());
		titleTemplate = titleTemplate.replace("%ADDRESS%", location.getAddress1() + " " + location.getAddress2()
				+ ", " + location.getCity());
	}
}
