package com.kikbak.rest.client;

import java.io.IOException;
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

import com.kikbak.client.service.RewardService;
import com.kikbak.client.service.SharedExperienceService;
import com.kikbak.dao.ReadOnlyAllocatedGiftDAO;
import com.kikbak.dao.ReadOnlyGiftDAO;
import com.kikbak.dao.ReadOnlyLocationDAO;
import com.kikbak.dao.ReadOnlyMerchantDAO;
import com.kikbak.dao.ReadOnlyUserDAO;
import com.kikbak.jaxb.rewards.GiftType;

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
	
	@RequestMapping( value = "/landing", method = RequestMethod.GET)
	public ModelAndView getLandingPage(final HttpServletRequest request,
			final HttpServletResponse httpResponse) {

		String code = request.getParameter("code");
		try {
			request.setCharacterEncoding("UTF-8");
			GiftType gift = rewardService.getGiftByReferredCode(code);
			
			if (gift == null) {
				httpResponse.sendRedirect(config.getString("landing.code_not_found.url"));
				return new ModelAndView();
			}
			
			String title = config.getString(SHARE_TEMPLATE_TITLE_FB).replace("%NAME%", gift.getShareInfo().get(0).getFriendName());
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
			request.setAttribute("location", gift.getMerchant().getLocations().get(0));
			request.setAttribute("validationType", gift.getValidationType());
			request.setAttribute("locationType", gift.getRedemptionLocationType());
			return new ModelAndView("landing.jsp");
		} catch (Exception e) {
			log.error("Error to get gift of code " + code, e);
			try {
				httpResponse.sendRedirect(config.getString("landing.code_not_found.url"));
			} catch (IOException e1) {
				log.error("Error to get gift of code " + code, e);
				httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			return new ModelAndView();
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
