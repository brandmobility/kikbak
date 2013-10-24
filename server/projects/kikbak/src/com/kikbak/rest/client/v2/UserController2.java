package com.kikbak.rest.client.v2;


import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kikbak.client.service.v2.UserService2;
import com.kikbak.jaxb.v1.offer.GetUserOffersRequest;
import com.kikbak.jaxb.v2.offer.GetUserOffersResponse;
import com.kikbak.rest.client.v1.AbstractController;
import com.kikbak.rest.client.v1.UserController;

@Controller
@RequestMapping("/v2/user")
public class UserController2 extends AbstractController {

    private static final Logger logger = Logger.getLogger(UserController.class);
    
    @Autowired
    protected UserService2 userService;
    
    
    @RequestMapping(value = "/offer/{userId}", method = RequestMethod.POST)
    public GetUserOffersResponse offersRequest2(@PathVariable("userId") Long userId,
            @RequestBody GetUserOffersRequest request, final HttpServletResponse httpResponse) {
        try {
            GetUserOffersResponse response = new GetUserOffersResponse();
            response.getOffers().addAll(userService.getOffers2(userId, request.getUserLocation()));
            return response;
        } catch (Exception e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }


    @RequestMapping(value = "/offer/{userId}/{merchantName}", method = RequestMethod.GET)
    public GetUserOffersResponse offersRequest(@PathVariable("userId") Long userId,
            @PathVariable("merchantName") String merchantName, final HttpServletResponse httpResponse) {
        try {
            if (StringUtils.isBlank(merchantName)) {
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return null;
            }
            GetUserOffersResponse response = new GetUserOffersResponse();
            response.getOffers().addAll(userService.getOffers2(userId, merchantName));
            return response;
        } catch (Exception e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

}
