package com.kikbak.push.apple;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.commons.configuration.PropertiesConfiguration;

import com.kikbak.config.ContextUtil;

public class ApsConnection {

	private static PropertiesConfiguration config = ContextUtil.getBean("staticPropertiesConfiguration", PropertiesConfiguration.class);
	
	private ServerType connectionType;
	private KeyLoader keyLoader;
	private ApsResponseHandler handler;
	private Socket socket;
	
	public ApsConnection() {
		if(config.getString("aps.gateway.type").equals(ServerType.development.name())){
    		connectionType = ServerType.development;
    	}
    	else{
    		connectionType = ServerType.production;
    	}
		
		keyLoader = new KeyLoader();
	}
	
	public void connect() throws IOException, Exception{
		socket = keyLoader.getSSLContext().getSocketFactory().createSocket();
		socket.setTcpNoDelay(true);
        socket.connect(new InetSocketAddress(connectionType.getHost(), connectionType.getPort()), config.getInteger("aps.connect.timeout_ms", 3000));
        handler = new ApsResponseHandler(socket);
        handler.start();
	}
	
	
	public void sendPush(NotificationPayload payload) throws IOException{
		
		byte[] data = payload.serializeNotification();
		
	   try {
            socket.getOutputStream().write(data);
        } catch (IOException e) {
            socket.close();
            throw e;
        }
	}
	
	public boolean isClosed(){
		return socket.isClosed();
	}
	
}
