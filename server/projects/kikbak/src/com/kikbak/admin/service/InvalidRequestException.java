package com.kikbak.admin.service;

public class InvalidRequestException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1476540446691400424L;
	
    /**
     * Construct an exception with the message.
     * 
     * @param message the exception message
     */
    public InvalidRequestException(final String message) {
        super(message);
    }

    /**
     * Construct an exception with the message and the cause of the exception.
     * 
     * @param message the exception message
     * @param thrown the cause of the exception
     */
    public InvalidRequestException(final String message, final Throwable thrown) {
        super(message, thrown);
    }

}
