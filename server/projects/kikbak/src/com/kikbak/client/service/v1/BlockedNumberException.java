package com.kikbak.client.service.v1;

@SuppressWarnings("serial")
public class BlockedNumberException extends Exception {

    public BlockedNumberException(String msg) {
        super(msg);
    }

    public BlockedNumberException(String msg, Throwable t) {
        super(msg, t);
    }
}
