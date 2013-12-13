package com.kikbak.rest.client.v1;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.portlet.ModelAndView;

import com.kikbak.client.service.v1.RewardService;
import com.kikbak.client.service.v1.SharedExperienceService;
import com.kikbak.client.util.UAgentInfo;
import com.kikbak.dao.ReadOnlyAllocatedGiftDAO;
import com.kikbak.dao.ReadOnlyGiftDAO;
import com.kikbak.dao.ReadOnlyLocationDAO;
import com.kikbak.dao.ReadOnlyMerchantDAO;
import com.kikbak.dao.ReadOnlyUserDAO;
import com.kikbak.dao.ReadWriteLandingHostDAO;
import com.kikbak.dto.LandingHost;
import com.kikbak.jaxb.v1.rewards.AvailableCreditType;
import com.kikbak.jaxb.v1.rewards.GiftType;

@Controller
public class LandingController {

	private static final String SHARE_TEMPLATE_TITLE_FB = "share.template.title.fb";

	private static final String SHARE_TEMPLATE_BODY_FB = "share.template.body.fb";
	
	private static final Logger log = Logger.getLogger(LandingController.class);

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
    ReadOnlyAllocatedGiftDAO roAllocatedGiftDao;
	
	@Autowired
	private ReadOnlyGiftDAO readOnlyGiftDAO;
		
	@Autowired
	private ReadWriteLandingHostDAO readWriteLandingHostDAO;
	
	@RequestMapping( value = "/landing", method = RequestMethod.GET)
	public ModelAndView getLandingPage(final HttpServletRequest request,
			final HttpServletResponse httpResponse) {

		String code = request.getParameter("code");
		try {
		    String userAgent = request.getHeader("User-Agent");
		    String httpAccept = request.getHeader("Accept");
		    String referer = request.getHeader("Referer");
		    UAgentInfo detector = new UAgentInfo(userAgent, httpAccept);
		    
			request.setCharacterEncoding("UTF-8");
			GiftType gift = rewardService.getGiftByReferredCode(code);
			
			if (gift == null) {
				httpResponse.sendRedirect(config.getString("landing.code_not_found.url"));
				return null; 
			}
			
            if (StringUtils.isNotBlank(referer)) {
            	try {
            		URI uri = new URI(referer);
            		String hostname = uri.getHost();
            		LandingHost host = readWriteLandingHostDAO.increaseCount(hostname);
            		request.setAttribute("hostBlock", host.getBlock());
            	} catch (Exception e) {
        			log.error("Error check referer hostname " + referer, e);	
            	}
            }
			
			String title = config.getString(SHARE_TEMPLATE_TITLE_FB).replace("%MERCHANT%", gift.getMerchant().getName())
					.replace("%DESC%", gift.getDesc())
					.replace("%DESC_DETAIL%", gift.getDetailedDesc());
			String body = config.getString(SHARE_TEMPLATE_BODY_FB).replace("%MERCHANT%", gift.getMerchant().getName())
					.replace("%DESC%", gift.getDesc())
					.replace("%DESC_DETAIL%", gift.getDetailedDesc());
			request.setAttribute("merchantUrl", gift.getMerchant().getImageUrl());
			request.setAttribute("shareInfo", gift.getShareInfo().get(0));
			request.setAttribute("gift", gift);
			request.setAttribute("url", request.getRequestURL() + (StringUtils.isBlank(request.getQueryString()) ? "" : "?" + request.getQueryString()));
			request.setAttribute("code", code);
			request.setAttribute("encodeMerchantName", URLEncoder.encode(gift.getMerchant().getName(), "UTF-8"));
			request.setAttribute("title", title);
			request.setAttribute("body", body);
			request.setAttribute("location", gift.getShareInfo().get(0).getLocation());
			request.setAttribute("locations", gift.getMerchant().getLocations());
			request.setAttribute("validationType", gift.getValidationType());
			request.setAttribute("locationType", gift.getRedemptionLocationType());
			request.setAttribute("mobile", detector.detectMobileQuick());
	    	return new ModelAndView();
		} catch (Exception e) {
			log.error("Error to get gift of code " + code, e);
			try {
				httpResponse.sendRedirect(config.getString("landing.code_not_found.url"));
			} catch (IOException e1) {
				log.error("Error to get gift of code " + code, e);
				httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			return null; 
		}
	}
	
	@RequestMapping( value = "/reward", method = RequestMethod.GET)
	public ModelAndView getRewardPage(final HttpServletRequest request,
			final HttpServletResponse httpResponse) {

		String code = request.getParameter("code");
		try {
		    String userAgent = request.getHeader("User-Agent");
		    String httpAccept = request.getHeader("Accept");
		    UAgentInfo detector = new UAgentInfo(userAgent, httpAccept);
		    
			request.setCharacterEncoding("UTF-8");
			AvailableCreditType credit = rewardService.getCreditByCode(code);
			GiftType gift = rewardService.getLastGiftByCreditId(credit.getId());
			
			if (credit == null || gift == null) {
				httpResponse.sendRedirect(config.getString("landing.code_not_found.url"));
				return null; 
			}
			
			request.setAttribute("merchantUrl", credit.getMerchant().getImageUrl());
			request.setAttribute("shareInfo", gift.getShareInfo().get(0));
			request.setAttribute("gift", gift);
			request.setAttribute("credit", credit);
			request.setAttribute("code", code);
			request.setAttribute("encodeMerchantName", URLEncoder.encode(credit.getMerchant().getName(), "UTF-8"));
			request.setAttribute("validationType", credit.getValidationType());
			request.setAttribute("redeemCount", credit.getRedeemedGiftsCount());
            if (credit.getRedeemedGiftsCount() <= 1) {
    			request.setAttribute("redeemCountSingle", "true");
            }
			request.setAttribute("mobile", detector.detectMobileQuick());
	    	return new ModelAndView();
		} catch (Exception e) {
			log.error("Error to get gift of code " + code, e);
			try {
				httpResponse.sendRedirect(config.getString("landing.code_not_found.url"));
			} catch (IOException e1) {
				log.error("Error to get gift of code " + code, e);
				httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			return null; 
		}
	}
	
	@RequestMapping( value = "/gift/success", method = RequestMethod.GET)
	public ModelAndView getSuccessPage(final HttpServletRequest request,
			final HttpServletResponse httpResponse) {
		
		try {
			request.setCharacterEncoding("UTF-8");
			String code = request.getParameter("code");
			long userId = Long.parseLong(request.getParameter("user"));
			long agId = Long.parseLong(request.getParameter("gid"));
			GiftType gift = rewardService.getGiftByReferredCode(code);
			request.setAttribute("merchantUrl", gift.getMerchant().getImageUrl());
			request.setAttribute("shareInfo", gift.getShareInfo().get(0));
			request.setAttribute("gift", gift);
			request.setAttribute("url", "../rewards/generateBarcode/" + userId + "/" + agId + "/160/100/");
			request.setAttribute("code", code);
			request.setAttribute("encodeMerchantName", URLEncoder.encode(gift.getMerchant().getName(), "UTF-8"));
			request.setAttribute("location", gift.getMerchant().getLocations().get(0));
			return new ModelAndView("success.jsp");
		} catch (Exception e) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return new ModelAndView();
		}
	}
}
