package com.kikbak.client.service.v1;

@SuppressWarnings("serial")
public class OfferExpiredException extends Exception {

    public OfferExpiredException(String msg) {
        super(msg);
    }

    public OfferExpiredException(String msg, Throwable t) {
        super(msg, t);
    }
}
