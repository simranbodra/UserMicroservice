package com.bridgelabz.microservice.fundoonotes.user.rabbitmq;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.bridgelabz.microservice.fundoonotes.user.models.Mail;

@Service
public class SQSProducerImpl implements MailProducerService {

	@Autowired
	private JmsTemplate jmsTemplate;

	@Value("${queuename}")
	private String queueName;

	@Override
	public void send(Mail message) {

		jmsTemplate.send(queueName, new MessageCreator() {

			public Message createMessage(Session session) throws JMSException {

				return session.createObjectMessage(message);
			}

		});

	}

}
