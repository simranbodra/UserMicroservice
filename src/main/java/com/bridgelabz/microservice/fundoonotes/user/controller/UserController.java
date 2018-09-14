package com.bridgelabz.microservice.fundoonotes.user.controller;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
import com.bridgelabz.microservice.fundoonotes.user.models.Response;
import com.bridgelabz.microservice.fundoonotes.user.models.UserProfile;
import com.bridgelabz.microservice.fundoonotes.user.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserService userService;

	/**
	 * TO register a user
	 * 
	 * @param registration
	 * @return ResponseDTO
	 * @throws RegistrationException
	 * @throws MessagingException
	 * @throws ElasticsearchFailException 
	 */
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<Response> register(@RequestBody Registration registration)
			throws RegistrationException, MessagingException, ElasticsearchFailException {
		userService.register(registration);

		Response response = new Response();
		response.setMessage("Registration Successful");
		response.setStatus(10);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	/**
	 * For User login
	 * 
	 * @param login
	 * @param resp
	 * @return ResponseDTO
	 * @throws LoginException
	 * @throws UserNotFoundException
	 * @throws UserNotActivatedException
	 * @throws ElasticsearchFailException 
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<Response> login(@RequestBody Login login, HttpServletResponse resp)
			throws LoginException, UserNotFoundException, UserNotActivatedException, ElasticsearchFailException {

		String token = userService.login(login);

		resp.setHeader("token", token);

		Response response = new Response();
		response.setMessage("Login Successful");
		response.setStatus(20);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * To activate account after registration
	 * 
	 * @param token
	 * @return ResponseDTO
	 * @throws LoginException
	 * @throws ElasticsearchFailException 
	 */
	@RequestMapping(value = "/activateaccount", method = RequestMethod.GET)
	public ResponseEntity<Response> activateaccount(@RequestHeader("token") String token) throws LoginException, ElasticsearchFailException {

		userService.activate(token);

		Response response = new Response();
		response.setMessage("Account activated successfully");
		response.setStatus(12);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * To send reset password link
	 * 
	 * @param email
	 * @return ResponseDTO
	 * @throws MessagingException
	 * @throws UserNotFoundException
	 * @throws ElasticsearchFailException 
	 */
	@RequestMapping(value = "/resetPasswordLink", method = RequestMethod.GET)
	public ResponseEntity<Response> resetPasswordLink(@RequestParam("email") String email)
			throws MessagingException, UserNotFoundException, ElasticsearchFailException {

		userService.sendPasswordLink(email);

		Response response = new Response();
		response.setMessage("Successfully sent mail");
		response.setStatus(31);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * To reset password for the user
	 * 
	 * @param token
	 * @param resetPassword
	 * @return ResponseDTO
	 * @throws LoginException
	 * @throws UserNotFoundException
	 * @throws ElasticsearchFailException 
	 */
	@RequestMapping(value = "/resetPassword", method = RequestMethod.PUT)
	public ResponseEntity<Response> resetPassword(@RequestHeader("token") String token,
			@RequestBody ResetPassword resetPassword) throws LoginException, UserNotFoundException, ElasticsearchFailException {
		userService.passwordReset(token, resetPassword);

		Response response = new Response();
		response.setMessage("Password reset successful");
		response.setStatus(32);

		return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
	}

	/**
	 * To add profile picture
	 * 
	 * @param token
	 * @param image
	 * @return picture link
	 * @throws FileConversionException
	 * @throws ElasticsearchFailException 
	 */
	@RequestMapping(value = "/addProfilePicture", method = RequestMethod.PUT)
	public ResponseEntity<String> addProfilePicture(HttpServletRequest request, @RequestParam MultipartFile image)
			throws FileConversionException, ElasticsearchFailException {

		String userId = request.getHeader("UserId");

		String link = userService.addProfilePicture(userId, image);

		return new ResponseEntity<>(link, HttpStatus.OK);
	}

	/**
	 * To set default profile picture
	 * 
	 * @param token
	 * @return picture link
	 * @throws ElasticsearchFailException 
	 */
	@RequestMapping(value = "/removeProfilePicture", method = RequestMethod.PUT)
	public ResponseEntity<Response> removeProfilePicture(HttpServletRequest request) throws ElasticsearchFailException {

		String userId = request.getHeader("UserId");

		userService.removeProfilePicture(userId);

		Response response = new Response();
		response.setMessage("Default profile picture set successfully");
		response.setStatus(33);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * To get profile details
	 * 
	 * @param token
	 * @return
	 * @throws ElasticsearchFailException 
	 */
	@RequestMapping(value = "/getProfileDetails", method = RequestMethod.GET)
	public ResponseEntity<UserProfile> getProfileDetails(HttpServletRequest request) throws ElasticsearchFailException {

		String userId = request.getHeader("UserId");

		UserProfile userProfile = userService.getProfileDetails(userId);

		return new ResponseEntity<>(userProfile, HttpStatus.ACCEPTED);
	}
}
