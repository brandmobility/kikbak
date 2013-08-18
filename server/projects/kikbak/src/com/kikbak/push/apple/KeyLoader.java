package com.kikbak.push.apple;

import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.springframework.core.io.FileSystemResource;

import com.kikbak.config.ContextUtil;

public class KeyLoader {
	
	private static final Logger log = Logger.getLogger(KeyLoader.class);
	
    private static PropertiesConfiguration config = ContextUtil.getBean("staticPropertiesConfiguration", PropertiesConfiguration.class);
    

    private final String certType;
    private final String password;
    private final String certLocation;
    private final String algorithm;
    private final String protocol;
    
    public KeyLoader(){
    	certType = config.getString("aps.cert.type");
    	password = config.getString("aps.password");
    	certLocation = config.getString("aps.location");
    	algorithm = config.getString("aps.algorithm");
    	protocol = config.getString("aps.protocol");
    }
    
    public SSLContext getSSLContext() throws Exception{
    	KeyStore ts = KeyStore.getInstance(certType);
        log.trace("cert location: " + ContextUtil.getRealPath() + certLocation + " password: " + password);
        ts.load((new FileSystemResource(ContextUtil.getRealPath() + certLocation)).getInputStream(), password.toCharArray());
        KeyManagerFactory tmf = KeyManagerFactory.getInstance(algorithm);
        tmf.init(ts, password.toCharArray());

        SSLContext sslContext = SSLContext.getInstance(protocol);
        sslContext.init(tmf.getKeyManagers(), null, null);
        
        return sslContext;
    }
}
