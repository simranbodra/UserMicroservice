package com.bridgelabz.microservice.fundoonotes.user.exceptions;

public class ElasticsearchFailException extends Exception {

	private static final long serialVersionUID = -1369043246630564465L;

	public ElasticsearchFailException(String message) {
		super(message);
	}
}
