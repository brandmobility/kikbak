package com.kikbak.json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.junit.Test;

import com.kikbak.jaxb.applepushnotification.AppleNotificationPayload;
import com.kikbak.jaxb.applepushnotification.ApsType;

public class JsonPaserTest {

	@Test
	public void testJSON(){
		AppleNotificationPayload aps = new AppleNotificationPayload();
		ApsType at = new ApsType();
		at.setAlert("Alert");
		at.setBadge(2);
		aps.setAps(at);
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			mapper.writeValue(out, aps);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String s = out.toString();
		System.out.println(s);
	}
}
