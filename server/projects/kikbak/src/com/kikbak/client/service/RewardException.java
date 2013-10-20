package com.kikbak.client.service;

@SuppressWarnings("serial")
public class RewardException extends Exception {

    public RewardException(String msg) {
        super(msg);
    }

    public RewardException(String msg, Throwable t) {
        super(msg, t);
    }
}
