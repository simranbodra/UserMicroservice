package com.bridgelabz.microservice.fundoonotes.user.service;

import javax.mail.MessagingException;

import org.springframework.web.multipart.MultipartFile;

import com.bridgelabz.microservice.fundoonotes.user.exceptions.ElasticsearchFailException;
import com.bridgelabz.microservice.fundoonotes.user.exceptions.FileConversionException;
import com.bridgelabz.microservice.fundoonotes.user.exceptions.LoginException;
import com.bridgelabz.microservice.fundoonotes.user.exceptions.RegistrationException;
import com.bridgelabz.microservice.fundoonotes.user.exceptions.UserNotActivatedException;
import com.bridgelabz.microservice.fundoonotes.user.exceptions.UserNotFoundException;
import com.bridgelabz.microservice.fundoonotes.user.models.Login;
import com.bridgelabz.microservice.fundoonotes.user.models.Registration;
import com.bridgelabz.microservice.fundoonotes.user.models.ResetPassword;
import com.bridgelabz.microservice.fundoonotes.user.models.UserProfile;

public interface UserService {

	public void register(Registration registrationDto) throws RegistrationException, MessagingException, ElasticsearchFailException;

	public String login(Login loginDto) throws LoginException, UserNotFoundException, UserNotActivatedException, ElasticsearchFailException;

	public void activate(String token) throws LoginException, ElasticsearchFailException;

	public void sendPasswordLink(String email) throws MessagingException, UserNotFoundException, ElasticsearchFailException;

	public void passwordReset(String email, ResetPassword resetPassword) throws LoginException, UserNotFoundException, ElasticsearchFailException;

	public String addProfilePicture(String userId, MultipartFile image) throws FileConversionException, ElasticsearchFailException;

	public void removeProfilePicture(String userId) throws ElasticsearchFailException;

	public UserProfile getProfileDetails(String userId) throws ElasticsearchFailException;

}
