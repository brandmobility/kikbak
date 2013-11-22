package com.kikbak.rest.client.v2;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kikbak.client.service.v1.RateLimitException;
import com.kikbak.client.service.v1.SharedExperienceService;
import com.kikbak.jaxb.v2.share.StoriesResponse;
import com.kikbak.jaxb.v2.share.StoryStatus;

@Controller
@RequestMapping("/v2/share")
public class SharedController2 {
	
	private static final Logger logger = Logger.getLogger(SharedController2.class);
	
	@Autowired
	private SharedExperienceService sharedService;
	
	@RequestMapping(value = "/getstories", method = RequestMethod.GET)
	public StoriesResponse getStories(final HttpServletRequest httpRequest,
				final HttpServletResponse httpResponse) {

        try {
        	String tmp = httpRequest.getParameter("userid");
        	Long userId = null;
        	if( tmp != null){
        		userId = Long.parseLong(tmp);
        	}
        	
        	tmp = httpRequest.getParameter("offerid");
        	Long offerId = null;
        	if( tmp != null ){
        		offerId = Long.parseLong(tmp);
        	}
        	String imageUrl = ServletRequestUtils.getStringParameter(httpRequest, "imageurl");
        	String platform = ServletRequestUtils.getStringParameter(httpRequest, "platform");
        	String email = ServletRequestUtils.getStringParameter(httpRequest, "email");
        	String phonenumber = ServletRequestUtils.getStringParameter(httpRequest, "phonenumber");
        	String caption = ServletRequestUtils.getStringParameter(httpRequest, "caption");
        	String employeeId = ServletRequestUtils.getStringParameter(httpRequest, "employeeid");
        	
        	if(imageUrl == null){
        		imageUrl = "";
        	}
        	
        	if( email == null){
        		email = "";
        	}
        	
        	if( userId == null || offerId == null ){
        		throw new IllegalArgumentException("userId or offerId was null");
        	}
        	
        	StoriesResponse response = new StoriesResponse();
        	response.setStatus(StoryStatus.OK);
        	sharedService.getShareStories(userId, offerId, URLDecoder.decode(imageUrl, "UTF-8"), platform, 
        				URLDecoder.decode(email, "UTF-8"), phonenumber, caption, employeeId, response);
        	return response;
        }
        catch( RateLimitException e){
        	
        	return null;
        }
        catch (Exception e) {
            logger.error("cannot generate barcode", e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new StoriesResponse();
        }
    }
	
	@RequestMapping(value = "/addsharetype", method = RequestMethod.GET)
	public void updateShare(final HttpServletRequest httpRequest,
			final HttpServletResponse httpResponse){
		
		try{
			String code = httpRequest.getParameter("code");
			String type = httpRequest.getParameter("type");
			sharedService.addShareType(code, type);
		}
		catch(Exception e){
			logger.error("cannot generate barcode", e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
		}
	}
}
