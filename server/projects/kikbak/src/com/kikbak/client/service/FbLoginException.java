package com.kikbak.client.service;

@SuppressWarnings("serial")
public class FbLoginException extends Exception {

    public FbLoginException(String msg) {
        super(msg);
    }

    public FbLoginException(String msg, Throwable t) {
        super(msg, t);
    }
}
