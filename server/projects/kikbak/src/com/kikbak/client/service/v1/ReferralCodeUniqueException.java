package com.kikbak.client.service.v1;

@SuppressWarnings("serial")
public class ReferralCodeUniqueException extends Exception {

    public ReferralCodeUniqueException(String msg) {
        super(msg);
    }

    public ReferralCodeUniqueException(String msg, Throwable t) {
        super(msg, t);
    }
}
