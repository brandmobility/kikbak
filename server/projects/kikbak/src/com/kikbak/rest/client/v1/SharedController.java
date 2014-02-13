package com.kikbak.rest.client.v1;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kikbak.client.service.v1.SharedExperienceService;
import com.kikbak.client.service.v1.StoryTemplateService;
import com.kikbak.dao.enums.Channel;
import com.kikbak.jaxb.v1.share.MessageTemplateType;
import com.kikbak.jaxb.v1.share.ShareExperienceRequest;
import com.kikbak.jaxb.v1.share.ShareExperienceResponse;
import com.kikbak.jaxb.v1.share.SharedType;
import com.kikbak.jaxb.v2.share.ChannelType;
import com.kikbak.jaxb.v2.share.StoryType;

@Controller
@RequestMapping("/ShareExperience")
public class SharedController extends AbstractController {

    private static final Logger logger = Logger.getLogger(SharedController.class);

    @Autowired
    private SharedExperienceService sharedExperienceService;

    @Autowired
    private StoryTemplateService storyService;

    @RequestMapping(value = "/{userId}", method = RequestMethod.POST)
    public ShareExperienceResponse postShareExperience(@PathVariable("userId") Long userId,
            @RequestBody ShareExperienceRequest experienceRequest, final HttpServletResponse httpResponse) {
        try {
            ensureCorrectUser(userId);

            ShareExperienceResponse response = new ShareExperienceResponse();
            SharedType experience = experienceRequest.getExperience();

            SharedExperienceService.ShareInfo share = new SharedExperienceService.ShareInfo();
            share.caption = experience.getCaption();
            share.email = null;
            share.employeeId = experience.getEmployeeId();
            share.imageUrl = experience.getImageUrl();
            share.locationId = experience.getLocationId();
            share.offerId = experience.getOfferId();
            share.phoneNumber = null;
            share.channel = Channel.valueOf(experience.getType());
            share.userId = userId;
            share.latitude = experience.getLatitude() == null ? null : Double.parseDouble(experience.getLatitude());
            share.longitude = experience.getLongitude() == null ? null : Double.parseDouble(experience.getLongitude());

            String code = sharedExperienceService.registerSharingAndNotify(share);
            response.setReferrerCode(code);

            if (StringUtils.isNotBlank(experience.getType()) && StringUtils.isNotBlank(experience.getPlatform())) {
                ChannelType channel = ChannelType.fromValue(experience.getType());
                StoryType story = storyService.getStory(code, experience.getPlatform(), channel);

                MessageTemplateType template = new MessageTemplateType();
                template.setSubject(story.getSubject());
                template.setBody(story.getBody());
                template.setLandingUrl(story.getLandingUrl());
                response.setTemplate(template);
            }
            return response;
        } catch (WrongUserException e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        } catch (IllegalArgumentException e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } catch (Exception e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }
}
