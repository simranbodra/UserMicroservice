package com.bridgelabz.microservice.fundoonotes.user.rabbitmq;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bridgelabz.microservice.fundoonotes.user.models.Mail;
import com.bridgelabz.microservice.fundoonotes.user.service.MailService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SQSListener implements MessageListener, MailConsumerService {

	@Autowired
	private MailService mailService;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void onMessage(Message message) {

		try {
			ObjectMessage objectMessage = (ObjectMessage) message;

			Mail mail = objectMapper.convertValue(objectMessage.getObject(), Mail.class);
			if (mail != null) {
				mailService.sendMail(mail);

			}
		} catch (JMSException | MessagingException e) {
			// throw new MailNotSentException("Error while sending mail");
		}

	}

	@Override
	public void receive(Mail mail) throws MessagingException {
		// TODO Auto-generated method stub

	}

}