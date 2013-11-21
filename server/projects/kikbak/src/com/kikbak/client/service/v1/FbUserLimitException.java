package com.kikbak.client.service.v1;

@SuppressWarnings("serial")
public class FbUserLimitException extends Exception {

    public FbUserLimitException(String msg) {
        super(msg);
    }

    public FbUserLimitException(String msg, Throwable t) {
        super(msg, t);
    }
}
