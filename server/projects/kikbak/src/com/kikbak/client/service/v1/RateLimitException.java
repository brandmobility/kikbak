package com.kikbak.client.service.v1;

@SuppressWarnings("serial")
public class RateLimitException extends Exception {

    public RateLimitException(String msg) {
        super(msg);
    }

    public RateLimitException(String msg, Throwable t) {
        super(msg, t);
    }
}
