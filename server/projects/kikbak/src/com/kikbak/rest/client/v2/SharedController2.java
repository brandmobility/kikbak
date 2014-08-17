package com.kikbak.rest.client.v2;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.kikbak.client.service.v1.BlockedNumberException;
import com.kikbak.client.service.v1.RateLimitException;
import com.kikbak.client.service.v1.SharedExperienceService;
import com.kikbak.client.service.v1.StoryTemplateService;
import com.kikbak.dao.ReadOnlyBlockedNumberDAO;
import com.kikbak.dao.ReadOnlyLocationDAO;
import com.kikbak.dao.enums.Channel;
import com.kikbak.dto.Location;
import com.kikbak.jaxb.v2.share.StoriesResponse;
import com.kikbak.jaxb.v2.share.StoryStatus;
import com.kikbak.jaxb.v2.share.StoryType;
import com.kikbak.jaxb.v2.share.ZipEligibility;
import com.kikbak.jaxb.v2.share.ZipValidationResponse;

@Controller
@RequestMapping("/v2/share")
public class SharedController2 {

    private static final Logger logger = Logger.getLogger(SharedController2.class);

    @Autowired
    private SharedExperienceService sharedService;

    @Autowired
    private StoryTemplateService storyService;
    
    @Autowired
    private ReadOnlyLocationDAO readOnlyLocationDAO;
    
    @Autowired
    private ReadOnlyBlockedNumberDAO roBlockedNumberDAO;

    @RequestMapping(value = "/getstories", method = RequestMethod.GET)
    public StoriesResponse getStories(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse) {

        try {
            SharedExperienceService.ShareInfo share = new SharedExperienceService.ShareInfo();

            String tmp = httpRequest.getParameter("userid");
            share.userId = Long.parseLong(tmp);

            tmp = httpRequest.getParameter("offerid");
            share.offerId = Long.parseLong(tmp);

            share.channel = Channel.web;

            tmp = httpRequest.getParameter("locationid");
            if (tmp != null) {
                share.locationId = Long.parseLong(tmp);
            } else {
            	tmp = httpRequest.getParameter("sitename");
            	if (StringUtils.isNotBlank(tmp)) {
                    Location location = readOnlyLocationDAO.findBySiteName(tmp);
            	    if (null != location) {
            	        share.locationId = location.getId();
            	    }
            	}
            }

            share.imageUrl = ServletRequestUtils.getStringParameter(httpRequest, "imageurl");
            share.email = ServletRequestUtils.getStringParameter(httpRequest, "email");
            share.phoneNumber = ServletRequestUtils.getStringParameter(httpRequest, "phonenumber");
            share.caption = ServletRequestUtils.getStringParameter(httpRequest, "caption");
            share.employeeId = ServletRequestUtils.getStringParameter(httpRequest, "employeeid");
            share.zipCode = ServletRequestUtils.getStringParameter(httpRequest, "zipCode");
            share.longitude = ServletRequestUtils.getDoubleParameter(httpRequest, "longitude");
            share.latitude = ServletRequestUtils.getDoubleParameter(httpRequest, "latitude");

            if( roBlockedNumberDAO.isBlockedNumber(share.phoneNumber)){
            	throw new BlockedNumberException("Blocked number");
            }
            
            String code = sharedService.registerSharing(share);

            String platform = ServletRequestUtils.getStringParameter(httpRequest, "platform");
            List<StoryType> stories = storyService.getStories(code, platform);

            StoriesResponse response = new StoriesResponse();
            response.setStatus(StoryStatus.OK);
            response.setCode(code);
            response.getStories().addAll(stories);
            return response;
        } catch (RateLimitException e) {
            StoriesResponse response = new StoriesResponse();
            response.setStatus(StoryStatus.LIMIT_REACH);
            return response;
        } catch (BlockedNumberException e){
        	StoriesResponse response = new StoriesResponse();
            response.setStatus(StoryStatus.BLOCKED_NUMBER);
            return response;
        }
        catch (IllegalArgumentException e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } catch (Exception e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    @RequestMapping(value = "/addsharetype", method = RequestMethod.GET)
    public void updateShare(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse) {

        try {
            String code = httpRequest.getParameter("code");
            String type = httpRequest.getParameter("type");
            Channel ch = Channel.valueOf(type);
            sharedService.addShareType(code, ch);
        } catch (IllegalArgumentException e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        } catch (Exception e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
    }

    @RequestMapping(value = "/validateZip", method = RequestMethod.GET)
    public ZipValidationResponse validateZip(@RequestParam("zipCode") String zipCode,
            @RequestParam("offerId") Long offerId, @RequestParam(value="isLandingPage", required=false) Boolean isLandingPage, final HttpServletResponse httpResponse) {
        try {
            boolean eligible = sharedService.validateZipCodeEligibility(offerId, zipCode, isLandingPage);
            ZipValidationResponse response = new ZipValidationResponse();
            response.setStatus(eligible ? ZipEligibility.OK : ZipEligibility.INELIGIBLE);
            return response;
        } catch (Exception e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

}
