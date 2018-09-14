package com.bridgelabz.microservice.fundoonotes.user.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.bridgelabz.microservice.fundoonotes.user.rabbitmq.SQSListener;

@Configuration
public class JMSSQSConfiguration {

	@Value("${s3AccessKey}")
	String accessKeyID;

	@Value("${s3SecretAccessKey}")
	String secretAccessKey;

	@Value("${queueendpoint}")
	private String endpoint;

	@Value("${queuename}")
	private String queueName;

	@Autowired
	private SQSListener sqsListener;

	@Autowired
	private SQSConnectionFactory sqsConnectionFactory;

	@Bean
	public DefaultMessageListenerContainer jmsListenerContainer() {

		DefaultMessageListenerContainer dmlc = new DefaultMessageListenerContainer();
		dmlc.setConnectionFactory(sqsConnectionFactory);
		dmlc.setDestinationName(queueName);
		dmlc.setMessageListener(sqsListener);

		return dmlc;

	}

	@Bean
	public JmsTemplate createJMSTemplate() {

		JmsTemplate jmsTemplate = new JmsTemplate(sqsConnectionFactory);
		jmsTemplate.setDefaultDestinationName(queueName);
		jmsTemplate.setDeliveryPersistent(false);

		return jmsTemplate;

	}

}
