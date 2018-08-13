package com.bridgelabz.microservice.fundoonotes.user.service;

import java.util.Optional;

import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bridgelabz.microservice.fundoonotes.user.exceptions.LoginException;
import com.bridgelabz.microservice.fundoonotes.user.exceptions.RegistrationException;
import com.bridgelabz.microservice.fundoonotes.user.exceptions.UserNotActivatedException;
import com.bridgelabz.microservice.fundoonotes.user.exceptions.UserNotFoundException;
import com.bridgelabz.microservice.fundoonotes.user.models.Login;
import com.bridgelabz.microservice.fundoonotes.user.models.Mail;
import com.bridgelabz.microservice.fundoonotes.user.models.Registration;
import com.bridgelabz.microservice.fundoonotes.user.models.ResetPassword;
import com.bridgelabz.microservice.fundoonotes.user.models.User;
import com.bridgelabz.microservice.fundoonotes.user.rabbitmq.MailProducerService;
import com.bridgelabz.microservice.fundoonotes.user.repositories.TokenRepository;
import com.bridgelabz.microservice.fundoonotes.user.repositories.UserElasticsearchRepository;
import com.bridgelabz.microservice.fundoonotes.user.repositories.UserRepository;
import com.bridgelabz.microservice.fundoonotes.user.utility.JWTokenProvider;
import com.bridgelabz.microservice.fundoonotes.user.utility.UserUtility;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserElasticsearchRepository userElasticsearchRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JWTokenProvider tokenProvider;

	@Autowired
	private MailProducerService producer;

	@Autowired
	private TokenRepository tokenRepository;

	@Value("${activationLink}")
	private String activationLink;

	@Value("${resetPasswordLink}")
	private String resetPasswordLink;

	@Override
	public void register(Registration registrationDto) throws RegistrationException, MessagingException {
		UserUtility.validateUserForRegistration(registrationDto);

		if (userRepository.findByEmail(registrationDto.getEmailId()).isPresent()) {
			throw new RegistrationException("Email already registered");
		}

		User user = new User();
		user.setEmail(registrationDto.getEmailId());
		user.setName(registrationDto.getUserName());
		user.setPhoneNumber(registrationDto.getPhoneNumber());

		String password = passwordEncoder.encode(registrationDto.getPassword());

		user.setPassword(password);

		userRepository.save(user);

		userElasticsearchRepository.save(user);

		String token = tokenProvider.tokenGenerator(user.getUserId());
		Mail mail = new Mail();
		mail.setTo(user.getEmail());
		mail.setSubject("Account Activation Mail");
		mail.setBody(activationLink + token);

		producer.send(mail);
	}

	@Override
	public String login(Login loginDto) throws LoginException, UserNotFoundException, UserNotActivatedException {
		UserUtility.validateUserForLogin(loginDto);

		Optional<User> user = userRepository.findByEmail(loginDto.getEmail());

		if (!user.isPresent()) {
			throw new UserNotFoundException("Email not registered");
		}

		if (!user.get().isActive()) {
			throw new UserNotActivatedException("Please Activate your account");
		}

		if (!passwordEncoder.matches(loginDto.getPassword(), user.get().getPassword())) {
			throw new UserNotFoundException("Incorrect Password");
		}

		String token = tokenProvider.tokenGenerator(user.get().getUserId());

		return token;

	}

	@Override
	public void activate(String token) throws LoginException {
		String userId = tokenProvider.parseJWT(token);

		Optional<User> optionalUser = userRepository.findById(userId);

		User user = optionalUser.get();

		user.setActive(true);

		userRepository.save(user);

		userElasticsearchRepository.save(user);

	}

	@Override
	public void sendPasswordLink(String email) throws MessagingException, UserNotFoundException {

		Optional<User> user = userRepository.findByEmail(email);

		if (!user.isPresent()) {
			throw new UserNotFoundException("Email not registered");
		}

		String token = UserUtility.generateUUID();

		tokenRepository.save(token, user.get().getUserId());

		Mail mail = new Mail();
		mail.setTo(email);
		mail.setSubject("Account Activation Mail");
		mail.setBody(resetPasswordLink + token);

		producer.send(mail);
	}

	@Override
	public void passwordReset(String token, ResetPassword resetPassword) throws LoginException, UserNotFoundException {
		UserUtility.validateUserForResetPassword(resetPassword);

		String userId = tokenRepository.get(token);

		tokenRepository.delete(token);

		Optional<User> optionalUser = userRepository.findById(userId);

		User user = optionalUser.get();

		String password = passwordEncoder.encode(resetPassword.getPassword());

		user.setPassword(password);
		user.setActive(true);

		userRepository.save(user);

	}
}
