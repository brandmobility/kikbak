package com.kikbak.client.service.impl;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.kikbak.client.service.FbLoginException;
import com.kikbak.client.service.FbLoginService;
import com.kikbak.jaxb.register.UserType;

@Service
public class FbLoginServiceImpl implements FbLoginService {

	@Override
	public UserType getUserInfo(String accessToken) throws FbLoginException {
		if (StringUtils.isBlank(accessToken)) {
			throw new FbLoginException("cannot get fb user info empty access token");
		}

		UserType user = null;
		try {
			RestTemplate template = new RestTemplate();
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("access_token", accessToken);
			String response = template.getForObject("https://graph.facebook.com/me?access_token={access_token}", String.class, parameters);
			JSONObject json = new JSONObject(response);
			
			if (null != json) {
				user = new UserType();
				user.setAccessToken(accessToken);
				user.setEmail(URLDecoder.decode(json.optString("email"), "UTF-8"));
				user.setFirstName(URLDecoder.decode(json.optString("first_name", ""), "UTF-8"));
				user.setLastName(URLDecoder.decode(json.optString("last_name", ""), "UTF-8"));
				user.setGender(URLDecoder.decode(json.optString("gender", ""), "UTF-8"));
				user.setId(json.getLong("id"));
				user.setLink(URLDecoder.decode(json.optString("link", ""), "UTF-8"));
				user.setLocale(URLDecoder.decode(json.optString("locale", ""), "UTF-8"));
				user.setTimezone(json.getInt("timezone"));
				user.setUpdatedTime(URLDecoder.decode(json.optString("updated_time", ""), "UTF-8"));
				user.setVerified(json.getBoolean("verified"));
			}
			
		} catch (Exception e) {
			throw new FbLoginException("cannot get fb user info", e);
		}
		
		return user;
	}
}
