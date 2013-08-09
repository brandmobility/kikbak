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

import com.kikbak.client.service.RewardService;
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
	private SharedExperienceService sharedExperienceService;

	@Autowired
	RewardService rewardService;
	
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
			response.setReferrerCode(sharedExperienceService.registerSharing(userId, experience));
		} catch (Exception e) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			status.setCode(StatusCode.ERROR.ordinal());
			logger.error(e,e);
			return response;
		}
		
		if (StringUtils.isNotBlank(experience.getType())
				&& StringUtils.isNotBlank(experience.getPlatform())) {
			String titleKey = "share.template.title."
					+ experience.getType().toLowerCase() + "."
					+ experience.getPlatform().toLowerCase();
			String bodyKey = "share.template.body."
					+ experience.getType().toLowerCase() + "."
					+ experience.getPlatform().toLowerCase();

			String loginUrl = config.getString("share.template.login.url")
					.replace("%CODE%", response.getReferrerCode());

			String titleTemplate = config.getString(titleKey, "");
			String bodyTemplate = config.getString(bodyKey, "");

			MessageTemplateType template = new MessageTemplateType();
			response.setTemplate(template);

			try {
				if (StringUtils.isNotBlank(bodyTemplate)) {
					User user = readOnlyUserDAO.findById(userId);
					Location location = readOnlyLocationDAO.findById(experience
							.getLocationId());
					Merchant merchant = readOnlyMerchantDAO.findById(experience
							.getMerchantId());
					Gift gift = readOnlyGiftDAO.findByOfferId(experience
							.getOfferId());
					if (StringUtils.isNotBlank(titleTemplate)) {
						String title = fillTemplate(experience, loginUrl, titleTemplate, user,
								location, merchant, gift);
						template.setSubject(title);
					}
					if (StringUtils.isNotBlank(bodyTemplate)) {
						String body = fillTemplate(experience, loginUrl, bodyTemplate, user,
								location, merchant, gift);
						template.setBody(body);
					}
				}
			} catch (Exception e) {
				httpResponse
						.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				status.setCode(StatusCode.ERROR.ordinal());
				logger.error(e, e);
				return response;
			}
		}
		
		return response;
	}
	
	private String fillTemplate(SharedType experience, String loginUrl,
			String template, User user, Location location,
			Merchant merchant, Gift gift) {
		String result;
		result = template.replace("%CAPTION%", StringUtils.trimToEmpty(experience.getCaption()));
		result = result.replace("%EMPLOYEE_ID%", StringUtils.isNotBlank(experience.getEmployeeId()) ?
				experience.getEmployeeId() : "employee");
		result = result.replace("%IMAGE_URL%", StringUtils.trimToEmpty(experience.getImageUrl()));
		result = result.replace("%DESC%", StringUtils.trimToEmpty(gift.getDescription()));
		result = result.replace("%DESC_DETAIL%", StringUtils.trimToEmpty(gift.getDetailedDesc()));
		result = result.replace("%LOGIN_URL%", loginUrl);
		result = result.replace("%MERCHANT%", StringUtils.trimToEmpty(merchant.getName()));
		result = result.replace("%NAME%", StringUtils.trimToEmpty(user.getFirstName()) + " "
				+ StringUtils.trimToEmpty(user.getLastName()));
		result = result.replace("%ADDRESS%", StringUtils.trimToEmpty(location.getAddress1()) + " "
				+ StringUtils.trimToEmpty(location.getAddress2())
				+ ", " + StringUtils.trimToEmpty(location.getCity()));
		
		return result;
	}
}
