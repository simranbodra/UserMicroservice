package com.bridgelabz.microservice.fundoonotes.user.rabbitmq;

import javax.mail.MessagingException;

import com.bridgelabz.microservice.fundoonotes.user.models.Mail;

public interface MailConsumerService {

	/**
	 * TO receive the mail from the queue
	 * @param mail
	 * @throws MessagingException
	 */
	public void receive(Mail mail) throws MessagingException;
		
}