package com.kikbak.client.service;

public class RedemptionException extends Exception {

    /** Generated serial version ID */
    private static final long serialVersionUID = 1887841953635628486L;

    /**
     * Construct an exception with the message.
     * 
     * @param message the exception message
     */
    public RedemptionException(final String message) {
        super(message);
    }

    /**
     * Construct an exception with the message and the cause of the exception.
     * 
     * @param message the exception message
     * @param thrown the cause of the exception
     */
    public RedemptionException(final String message, final Throwable thrown) {
        super(message, thrown);
    }
}
