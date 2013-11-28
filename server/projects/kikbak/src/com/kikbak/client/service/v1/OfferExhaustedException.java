package com.kikbak.client.service.v1;

@SuppressWarnings("serial")
public class OfferExhaustedException extends Exception {

    public OfferExhaustedException(String msg) {
        super(msg);
    }

    public OfferExhaustedException(String msg, Throwable t) {
        super(msg, t);
    }
}
