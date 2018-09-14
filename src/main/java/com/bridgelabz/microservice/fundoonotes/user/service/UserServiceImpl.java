package com.bridgelabz.microservice.fundoonotes.user.service;

import java.util.Optional;

import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bridgelabz.microservice.fundoonotes.user.exceptions.ElasticsearchFailException;
import com.bridgelabz.microservice.fundoonotes.user.exceptions.FileConversionException;
import com.bridgelabz.microservice.fundoonotes.user.exceptions.LoginException;
import com.bridgelabz.microservice.fundoonotes.user.exceptions.RegistrationException;
import com.bridgelabz.microservice.fundoonotes.user.exceptions.UserNotActivatedException;
import com.bridgelabz.microservice.fundoonotes.user.exceptions.UserNotFoundException;
import com.bridgelabz.microservice.fundoonotes.user.models.Login;
import com.bridgelabz.microservice.fundoonotes.user.models.Mail;
import com.bridgelabz.microservice.fundoonotes.user.models.Registration;
import com.bridgelabz.microservice.fundoonotes.user.models.ResetPassword;
import com.bridgelabz.microservice.fundoonotes.user.models.User;
import com.bridgelabz.microservice.fundoonotes.user.models.UserProfile;
import com.bridgelabz.microservice.fundoonotes.user.rabbitmq.MailProducerService;
import com.bridgelabz.microservice.fundoonotes.user.repositories.ElasticsearchRepository;
import com.bridgelabz.microservice.fundoonotes.user.repositories.TokenRepository;
import com.bridgelabz.microservice.fundoonotes.user.repositories.UserRepository;
import com.bridgelabz.microservice.fundoonotes.user.utility.JWTokenProvider;
import com.bridgelabz.microservice.fundoonotes.user.utility.UserUtility;

@Service
public class UserServiceImpl implements UserService {

	private static final String SUFFIX = "/";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JWTokenProvider tokenProvider;

	@Autowired
	private MailProducerService producer;

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private ImageStorageService imageStorageService;

	@Autowired
	private ElasticsearchRepository elasticsearchRepository;

	@Value("${activationLink}")
	private String activationLink;

	@Value("${resetPasswordLink}")
	private String resetPasswordLink;

	@Value("${defaultProfileImage}")
	private String defaultProfileImage;

	@Value("${profilePictures}")
	private String profilePictures;

	@Value("${standardQueueUrl}")
	private String standardQueueUrl;

	@Override
	public void register(Registration registrationDto) throws RegistrationException, MessagingException, ElasticsearchFailException {
		UserUtility.validateUserForRegistration(registrationDto);

		if (elasticsearchRepository.findByEmail(registrationDto.getEmailId()).isPresent()) {
			throw new RegistrationException("Email already registered");
		}

		User user = new User();
		user.setEmail(registrationDto.getEmailId());
		user.setName(registrationDto.getUserName());
		user.setPhoneNumber(registrationDto.getPhoneNumber());

		String password = passwordEncoder.encode(registrationDto.getPassword());

		user.setPassword(password);

		userRepository.save(user);

		elasticsearchRepository.save(user);

		String token = tokenProvider.tokenGenerator(user.getUserId());
		Mail mail = new Mail();
		mail.setTo(user.getEmail());
		mail.setSubject("Account Activation Mail");
		mail.setBody(activationLink + token);

		producer.send(mail);

	}

	@Override
	public String login(Login loginDto)
			throws LoginException, UserNotFoundException, UserNotActivatedException, ElasticsearchFailException {
		UserUtility.validateUserForLogin(loginDto);

		Optional<User> user = elasticsearchRepository.findByEmail(loginDto.getEmail());

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
	public void activate(String token) throws LoginException, ElasticsearchFailException {
		String userId = tokenProvider.parseJWT(token);

		Optional<User> optionalUser = elasticsearchRepository.findById(userId);

		User user = optionalUser.get();

		user.setActive(true);

		userRepository.save(user);

		elasticsearchRepository.save(user);

	}

	@Override
	public void sendPasswordLink(String email)
			throws MessagingException, UserNotFoundException, ElasticsearchFailException {

		Optional<User> user = elasticsearchRepository.findByEmail(email);

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
	public void passwordReset(String token, ResetPassword resetPassword)
			throws LoginException, UserNotFoundException, ElasticsearchFailException {
		UserUtility.validateUserForResetPassword(resetPassword);

		String userId = tokenRepository.get(token);

		Optional<User> optionalUser = elasticsearchRepository.findById(userId);

		User user = optionalUser.get();

		String password = passwordEncoder.encode(resetPassword.getPassword());

		user.setPassword(password);
		user.setActive(true);

		userRepository.save(user);

		elasticsearchRepository.save(user);

	}

	@Override
	public String addProfilePicture(String userId, MultipartFile image)
			throws FileConversionException, ElasticsearchFailException {

		String folder = userId + SUFFIX + profilePictures;

		imageStorageService.uploadFile(folder, image);

		String picture = imageStorageService.getFile(folder, image.getOriginalFilename());

		Optional<User> optionalUser = elasticsearchRepository.findById(userId);

		User user = optionalUser.get();

		user.setProfileImage(picture);

		userRepository.save(user);

		elasticsearchRepository.save(user);

		return picture;
	}

	@Override
	public void removeProfilePicture(String userId) throws ElasticsearchFailException {

		String folder = userId + SUFFIX + profilePictures;

		String defaultPicture = imageStorageService.getFile(folder, defaultProfileImage);

		Optional<User> optionalUser = userRepository.findById(userId);

		User user = optionalUser.get();

		user.setProfileImage(defaultPicture);

		userRepository.save(user);

		elasticsearchRepository.save(user);
	}

	@Override
	public UserProfile getProfileDetails(String userId) throws ElasticsearchFailException {

		Optional<User> optionalUser = elasticsearchRepository.findById(userId);

		User user = optionalUser.get();

		UserProfile userProfile = new UserProfile();

		userProfile.setUserName(user.getName());
		userProfile.setUserEmail(user.getEmail());
		userProfile.setImageUrl(user.getProfileImage());

		return userProfile;
	}
}
