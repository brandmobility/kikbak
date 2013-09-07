package com.kikbak.rest.client;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kikbak.client.service.SuggestService;
import com.kikbak.jaxb.statustype.SuccessStatus;
import com.kikbak.jaxb.suggest.SuggestBusinessRequest;
import com.kikbak.jaxb.suggest.SuggestBusinessResponse;

@Controller
@RequestMapping("/suggest")
public class SuggestController extends AbstractController {

    @Autowired
    private SuggestService suggestService;

    @Autowired
    private PropertiesConfiguration config;

    private static final Logger logger = Logger.getLogger(SuggestController.class);

    @RequestMapping(value = "/business/{userId}", method = RequestMethod.POST)
    public SuggestBusinessResponse registerFacebookUser(@PathVariable("userId") Long userId,
            @RequestBody SuggestBusinessRequest request, final HttpServletResponse httpResponse) {
        try {
            suggestService.suggestBusiness(userId, request.getBusiness());

            SuggestBusinessResponse response = new SuggestBusinessResponse();
            response.setStatus(SuccessStatus.OK);
            return response;
        } catch (Exception e) {
            logger.error(e, e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }
}
