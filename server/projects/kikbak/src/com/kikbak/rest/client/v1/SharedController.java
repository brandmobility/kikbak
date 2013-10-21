package com.kikbak.rest.client.v1;

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

import com.kikbak.client.service.v1.RewardService;
import com.kikbak.client.service.v1.SharedExperienceService;
import com.kikbak.dao.ReadOnlyGiftDAO;
import com.kikbak.dao.ReadOnlyLocationDAO;
import com.kikbak.dao.ReadOnlyMerchantDAO;
import com.kikbak.dao.ReadOnlyUserDAO;
import com.kikbak.dto.Gift;
import com.kikbak.dto.Location;
import com.kikbak.dto.Merchant;
import com.kikbak.dto.User;
import com.kikbak.jaxb.v1.share.MessageTemplateType;
import com.kikbak.jaxb.v1.share.ShareExperienceRequest;
import com.kikbak.jaxb.v1.share.ShareExperienceResponse;
import com.kikbak.jaxb.v1.share.SharedType;

@Controller
@RequestMapping("/ShareExperience")
public class SharedController extends AbstractController {

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

    @RequestMapping(value = "/{userId}", method = RequestMethod.POST)
    public ShareExperienceResponse postShareExperience(@PathVariable("userId") Long userId,
            @RequestBody ShareExperienceRequest experienceRequest, final HttpServletResponse httpResponse) {
        try {
            ensureCorrectUser(userId);

            ShareExperienceResponse response = new ShareExperienceResponse();
            SharedType experience = experienceRequest.getExperience();
            response.setReferrerCode(sharedExperienceService.registerSharing(userId, experience));

            if (StringUtils.isNotBlank(experience.getType()) && StringUtils.isNotBlank(experience.getPlatform())) {
                String titleKey = "share.template.title." + experience.getType().toLowerCase() + "."
                        + experience.getPlatform().toLowerCase();
                String bodyKey = "share.template.body." + experience.getType().toLowerCase() + "."
                        + experience.getPlatform().toLowerCase();
                String loginUrl = config.getString("share.template.login.url").replace("%CODE%",
                        response.getReferrerCode());

                String titleTemplate = config.getString(titleKey, "");
                String bodyTemplate = config.getString(bodyKey, "");

                MessageTemplateType template = new MessageTemplateType();
                response.setTemplate(template);

                if (StringUtils.isNotBlank(bodyTemplate)) {
                    User user = readOnlyUserDAO.findById(userId);
                    Location location = null;
                    if( experience.getLocationId() != null ) {
                        readOnlyLocationDAO.findById(experience.getLocationId());
                    }
                    Merchant merchant = readOnlyMerchantDAO.findById(experience.getMerchantId());
                    Gift gift = readOnlyGiftDAO.findByOfferId(experience.getOfferId());
                    if (StringUtils.isNotBlank(titleTemplate)) {
                        String title = fillTemplate(experience, loginUrl, titleTemplate, user, location, merchant, gift);
                        template.setSubject(title);
                    }
                    if (StringUtils.isNotBlank(bodyTemplate)) {
                        String body = fillTemplate(experience, loginUrl, bodyTemplate, user, location, merchant, gift);
                        template.setBody(body);
                    }
                }
                template.setLandingUrl(loginUrl);
            }

            return response;
        } catch (WrongUserException e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        } catch (Exception e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    private String fillTemplate(SharedType experience, String loginUrl, String template, User user, Location location,
            Merchant merchant, Gift gift) {
        String result;
        result = template.replace("%CAPTION%", StringUtils.trimToEmpty(experience.getCaption()));
        result = result.replace("%EMPLOYEE_ID%",
                StringUtils.isNotBlank(experience.getEmployeeId()) ? experience.getEmployeeId() : "employee");
        result = result.replace("%IMAGE_URL%", StringUtils.trimToEmpty(experience.getImageUrl()));
        result = result.replace("%DESC%", StringUtils.trimToEmpty(gift.getDescription()));
        result = result.replace("%DESC_DETAIL%", StringUtils.trimToEmpty(gift.getDetailedDesc()));
        result = result.replace("%LOGIN_URL%", loginUrl);
        result = result.replace("%MERCHANT%", StringUtils.trimToEmpty(merchant.getName()));
        result = result
                .replace(
                        "%NAME%",
                        StringUtils.trimToEmpty(user.getFirstName())
                                + (StringUtils.isBlank(user.getFirstName()) ? StringUtils.trimToEmpty(user
                                        .getLastName()) : ""));
        
        if( location != null){
            result = result.replace(
                    "%ADDRESS%",
                    StringUtils.trimToEmpty(location.getAddress1())
                            + (StringUtils.isBlank(location.getAddress2()) ? "" : " " + location.getAddress2()) + ", "
                            + StringUtils.trimToEmpty(location.getCity()) + ", "
                            + StringUtils.trimToEmpty(location.getState()));
        }
        else {
            result = result.replace(
                    "%ADDRESS%","");
        }

        return result;
    }
}