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
import com.kikbak.jaxb.statustype.StatusType;
import com.kikbak.jaxb.suggest.SuggestBusinessRequest;
import com.kikbak.jaxb.suggest.SuggestBusinessResponse;
import com.kikbak.rest.StatusCode;

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
        SuggestBusinessResponse response = new SuggestBusinessResponse();
        StatusType status = new StatusType();
        status.setCode(StatusCode.OK.ordinal());
        response.setStatus(status);
        try {
            suggestService.suggestBusiness(userId, request.getBusiness());
        } catch (Exception e) {
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            status.setCode(StatusCode.ERROR.ordinal());
            logger.error(e, e);
        }
        return response;
    }
}
