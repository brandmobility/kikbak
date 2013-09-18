package com.kikbak.push.apple;

import java.io.IOException;
import java.net.Socket;

import javax.net.ssl.SSLSocket;

import org.apache.commons.configuration.PropertiesConfiguration;

import com.kikbak.config.ContextUtil;

//class MyHandshakeListener implements HandshakeCompletedListener {
//    @Override
//    public void handshakeCompleted(HandshakeCompletedEvent e) {
//      System.out.println("Handshake succesful!");
//      System.out.println("Using cipher suite: " + e.getCipherSuite());
//    }
//}

public class ApsConnection {

	private static PropertiesConfiguration config = ContextUtil.getBean("staticPropertiesConfiguration", PropertiesConfiguration.class);
	
	private ServerType connectionType;
	private KeyLoader keyLoader;
	private ApsResponseHandler handler;
	private Socket socket;
//	private Socket feedbackSocket;
//	private ApsResponseHandler feedbackHandler;
	
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
		socket = keyLoader.getSSLContext().getSocketFactory().createSocket(connectionType.getHost(), connectionType.getPort());
		socket.setTcpNoDelay(true);
//		((SSLSocket)socket).addHandshakeCompletedListener(new MyHandshakeListener());
		((SSLSocket)socket).startHandshake();
//        socket.connect(new InetSocketAddress(connectionType.getHost(), connectionType.getPort()), config.getInteger("aps.connect.timeout_ms", 3000));
        handler = new ApsResponseHandler(socket);
        handler.start();
        
//        feedbackSocket = keyLoader.getSSLContext().getSocketFactory().createSocket(connectionType.getFeedbackHost(), connectionType.getFeedbackPort());
//        feedbackSocket.setTcpNoDelay(true);
//        ((SSLSocket)feedbackSocket).addHandshakeCompletedListener(new MyHandshakeListener());
//        ((SSLSocket)feedbackSocket).startHandshake();
//        feedbackHandler = new ApsResponseHandler(socket);
//        feedbackHandler.start();
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
