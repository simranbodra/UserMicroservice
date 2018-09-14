package com.bridgelabz.microservice.fundoonotes.user.rabbitmq;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bridgelabz.microservice.fundoonotes.user.models.Mail;

//@Service
public class MailProducerServiceImpl implements MailProducerService {

	@Autowired
	private AmqpTemplate rabbitTemplate;

	@Value("${javainuse.rabbitmq.exchange}")
	private String exchange;

	@Value("${javainuse.rabbitmq.routingkey}")
	private String routingkey;

	@Override
	public void send(Mail mail) {
		rabbitTemplate.convertAndSend(exchange, routingkey, mail);

	}
}
