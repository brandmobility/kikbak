package com.kikbak.pushnotification.apple;

import javax.xml.bind.DatatypeConverter;

public class ApsToken{

    protected byte[] token;

    public ApsToken(String token) {
        this.token = DatatypeConverter.parseBase64Binary(token);
    }

    public byte[] getTokenBytes() {
        return token;
    }    
        
}