package com.bridgelabz.microservice.fundoonotes.user.exceptions;

public class MailNotSentException extends Exception {

	private static final long serialVersionUID = -2568334262671528753L;

	public MailNotSentException(String message) {
		super(message);
	}
}
