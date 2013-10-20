package com.kikbak.client.service.impl;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.kikbak.client.service.UserService;


public class CookieAuthenticationFilter extends GenericFilterBean {

    private static final Logger log = Logger.getLogger(CookieAuthenticationFilter.class);

	public static final String COOKIE_TOKEN_KEY = "token";

	public static final String COOKIE_USER_ID_KEY = "userid";
	
    @Autowired
    @Qualifier("staticPropertiesConfiguration")
    private PropertiesConfiguration config;
    
    @Autowired
    private UserService userService;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
    ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

    	Long userId = null;
    	String password = null;
    	boolean success = false;
        try {
            for (Cookie cookie : request.getCookies()) {
            	if (StringUtils.equalsIgnoreCase(COOKIE_TOKEN_KEY, cookie.getName())) {
            		password = cookie.getValue();
            	}
            	if (StringUtils.equalsIgnoreCase(COOKIE_USER_ID_KEY, cookie.getName())) {
            		userId = Long.parseLong(cookie.getValue());
            	}
            }
            success = userService.verifyUserToken(userId, password);
        } catch (Exception e) {
            log.error("verify cookie fails", e);
            success = false;
        }
        
        if (!success) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Set the Security Context for this thread
        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userId.toString(), password);
            SecurityContextHolder.getContext().setAuthentication(token);
            chain.doFilter(request, response);
        }
        finally {
            // Free the thread of the context
            SecurityContextHolder.clearContext();
        }
    }
}

