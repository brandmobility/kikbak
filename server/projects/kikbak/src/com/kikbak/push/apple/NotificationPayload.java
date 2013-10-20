package com.kikbak.push.apple;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.kikbak.jaxb.applepushnotification.AppleNotificationPayload;


public class NotificationPayload {

	private static final Logger logger = Logger.getLogger(NotificationPayload.class);
	
	private AppleNotificationPayload aps;
	private ApsToken token;
	private Byte command = 0;
	
	public NotificationPayload(AppleNotificationPayload aps, ApsToken token){
		this.aps = aps;
		this.token = token;
	}
	
	
	protected String serializePayload() throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		mapper.writeValue(out, aps);
		return out.toString();
	}
	
	public byte[] serializeNotification() throws JsonGenerationException, JsonMappingException, IOException{
		
		String payload = serializePayload();
		int size = Byte.SIZE / Byte.SIZE + Short.SIZE / Byte.SIZE + token.getTokenBytes().length + Short.SIZE
                / Byte.SIZE + payload.getBytes("UTF-8").length;

        ByteArrayOutputStream bao = new ByteArrayOutputStream(size);

        bao.write(command);
        short tokenLength = (short) token.getTokenBytes().length;
        bao.write((byte) (tokenLength & 0xFF00) >> 8);
        bao.write((byte) (tokenLength & 0xFF));
        bao.write(token.getTokenBytes());

        short payloadLength = (short) payload.getBytes("UTF-8").length;
        bao.write((byte) ((payloadLength & 0xFF00) >> 8));
        bao.write((byte) (payloadLength & 0xFF));
        bao.write(payload.getBytes("UTF-8"));

        if (logger.isTraceEnabled()) {
            logger.trace("payload length " + payload.length() + " payload " + payload);
        }
        
        return bao.toByteArray();
	}

}
