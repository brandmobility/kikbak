package com.kikbak.rest.client.v1;

@SuppressWarnings("serial")
public class WrongUserException extends Exception {

    public WrongUserException(String msg) {
        super(msg);
    }

    public WrongUserException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
