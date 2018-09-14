package com.bridgelabz.microservice.fundoonotes.user.exceptionhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.amazonaws.AmazonClientException;
import com.bridgelabz.microservice.fundoonotes.user.exceptions.ElasticsearchFailException;
import com.bridgelabz.microservice.fundoonotes.user.exceptions.FileConversionException;
import com.bridgelabz.microservice.fundoonotes.user.exceptions.LoginException;
import com.bridgelabz.microservice.fundoonotes.user.exceptions.MailNotSentException;
import com.bridgelabz.microservice.fundoonotes.user.exceptions.RegistrationException;
import com.bridgelabz.microservice.fundoonotes.user.exceptions.S3ConnectionException;
import com.bridgelabz.microservice.fundoonotes.user.exceptions.UserNotActivatedException;
import com.bridgelabz.microservice.fundoonotes.user.exceptions.UserNotFoundException;
import com.bridgelabz.microservice.fundoonotes.user.models.Response;

@ControllerAdvice
public class UserExceptionHandler {

	private final Logger logger = LoggerFactory.getLogger(UserExceptionHandler.class);

	@ExceptionHandler(RegistrationException.class)
	public ResponseEntity<Response> handleRegistrationException(RegistrationException exception) {
		logger.info("Error occured while registration " + exception.getMessage(), exception);

		Response response = new Response();
		response.setMessage(exception.getMessage());
		response.setStatus(1);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(LoginException.class)
	public ResponseEntity<Response> handleLoginException(LoginException exception) {
		logger.info("Error occured while login " + exception.getMessage(), exception);

		Response response = new Response();
		response.setMessage(exception.getMessage());
		response.setStatus(2);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<Response> handleUserNotFoundException(UserNotFoundException exception) {
		logger.info("Error occured while login " + exception.getMessage(), exception);

		Response response = new Response();
		response.setMessage(exception.getMessage());
		response.setStatus(3);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UserNotActivatedException.class)
	public ResponseEntity<Response> handleUserNotActivatedException(UserNotActivatedException exception) {
		logger.info("Error occured while login " + exception.getMessage(), exception);

		Response response = new Response();
		response.setMessage(exception.getMessage());
		response.setStatus(4);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MailNotSentException.class)
	public ResponseEntity<Response> handleMailNotSentException(MailNotSentException exception) {
		logger.info("Error occured while sending mail " + exception.getMessage(), exception);

		Response response = new Response();
		response.setMessage(exception.getMessage());
		response.setStatus(4);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AmazonClientException.class)
	public ResponseEntity<Response> handleAmazonClientException(AmazonClientException exception) {
		logger.error("Error occured for " + exception.getMessage(), exception);

		Response response = new Response();
		response.setMessage("cannot load aws credentials, please check credentials detils");
		response.setStatus(5);

		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(S3ConnectionException.class)
	public ResponseEntity<Response> handleS3ConnectionException(S3ConnectionException exception) {
		logger.error("Error occured for " + exception.getMessage(), exception);

		Response response = new Response();
		response.setMessage("cannot load aws credentials, please check credentials detils");
		response.setStatus(5);

		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(FileConversionException.class)
	public ResponseEntity<Response> handleFileConversionException(FileConversionException exception) {
		logger.error("Error occured for " + exception.getMessage(), exception);

		Response response = new Response();
		response.setMessage("cannot load aws credentials, please check credentials detils");
		response.setStatus(5);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ElasticsearchFailException.class)
	public ResponseEntity<Response> handleElasticsearchFailException(ElasticsearchFailException exception) {
		logger.error("Error occured for " + exception.getMessage(), exception);

		Response response = new Response();
		response.setMessage("cannot load aws credentials, please check credentials detils");
		response.setStatus(5);

		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Response> handleException(Exception exception) {
		logger.error("Error occured for " + exception.getMessage(), exception);

		Response response = new Response();
		response.setMessage("Something went wrong");
		response.setStatus(0);

		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
