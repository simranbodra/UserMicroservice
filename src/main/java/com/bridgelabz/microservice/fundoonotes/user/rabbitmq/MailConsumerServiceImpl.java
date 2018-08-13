package com.bridgelabz.microservice.fundoonotes.user.rabbitmq;

import javax.mail.MessagingException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bridgelabz.microservice.fundoonotes.user.models.Mail;
import com.bridgelabz.microservice.fundoonotes.user.service.MailService;

@Service
public class MailConsumerServiceImpl implements MailConsumerService {

	@Autowired
	private MailService mailService;

	@Override
	@RabbitListener(queues = "javainuse.queue")
	public void receive(Mail mail) throws MessagingException {

		mailService.sendMail(mail);

	}
}