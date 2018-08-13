package com.bridgelabz.microservice.fundoonotes.user.service;

import javax.mail.MessagingException;

import com.bridgelabz.microservice.fundoonotes.user.models.Mail;

public interface MailService {

	/**
	 * to send mail
	 * 
	 * @param mail
	 *            contains to, subject and body
	 * @throws MessagingException
	 */
	public void sendMail(Mail mail) throws MessagingException;
}
