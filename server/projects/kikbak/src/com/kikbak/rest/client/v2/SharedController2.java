package com.kikbak.rest.client.v2;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kikbak.client.service.v1.SharedExperienceService;
import com.kikbak.jaxb.v2.share.StoriesResponse;
import com.kikbak.jaxb.v2.share.StoryType;

@Controller
@RequestMapping("/v2/share")
public class SharedController2 {
	
	private static final Logger logger = Logger.getLogger(SharedController2.class);
	
	@Autowired
	private SharedExperienceService sharedService;
	
	@RequestMapping(value = "/getstories/{userId}/{offerId}/{platform}/{imageUrl}/", method = RequestMethod.GET)
	public StoriesResponse getStories(@PathVariable("userId") Long userId, 
			@PathVariable("offerId") Long offerId,
			@PathVariable("platform") String platform,
			@PathVariable("imageUrl") String imageUrl,
			final HttpServletResponse httpResponse) {

        try {
        	StoriesResponse response = new StoriesResponse();
        	List<StoryType> stories = new ArrayList<StoryType>();
        	sharedService.getShareStories(userId, offerId, URLDecoder.decode(imageUrl, "UTF-8"), platform, stories);
        	response.getStories().addAll(stories);
        	return response;
        } catch (Exception e) {
            logger.error("cannot generate barcode", e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new StoriesResponse();
        }
    }
	
	@RequestMapping(value = "/addsharetype/{code}/{type}/", method = RequestMethod.GET)
	public void updateShare(@PathVariable("code") String code,
			@PathVariable("type") String type,
			final HttpServletResponse httpResponse){
		
		try{
			sharedService.addShareType(code, type);
		}
		catch(Exception e){
			logger.error("cannot generate barcode", e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
		}
	}
}
