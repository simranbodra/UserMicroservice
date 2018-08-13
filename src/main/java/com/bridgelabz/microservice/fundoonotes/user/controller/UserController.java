package com.bridgelabz.microservice.fundoonotes.user.controller;

import javax.mail.MessagingException;
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

import com.bridgelabz.microservice.fundoonotes.user.exceptions.LoginException;
import com.bridgelabz.microservice.fundoonotes.user.exceptions.RegistrationException;
import com.bridgelabz.microservice.fundoonotes.user.exceptions.UserNotActivatedException;
import com.bridgelabz.microservice.fundoonotes.user.exceptions.UserNotFoundException;
import com.bridgelabz.microservice.fundoonotes.user.models.Login;
import com.bridgelabz.microservice.fundoonotes.user.models.Registration;
import com.bridgelabz.microservice.fundoonotes.user.models.ResetPassword;
import com.bridgelabz.microservice.fundoonotes.user.models.Response;
import com.bridgelabz.microservice.fundoonotes.user.service.FacebookService;
import com.bridgelabz.microservice.fundoonotes.user.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	private FacebookService facebookService;

	/**
	 * TO register a user
	 * 
	 * @param registration
	 * @return ResponseDTO
	 * @throws RegistrationException
	 * @throws MessagingException
	 */
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<Response> register(@RequestBody Registration registration)
			throws RegistrationException, MessagingException {
		userService.register(registration);

		Response response = new Response();
		response.setMessage("Registration Successful");
		response.setStatus(10);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/createFacebookAuthorization", method = RequestMethod.GET)
	public String createFacebookAuthorization() {
		return facebookService.createFacebookAuthorizationURL();
	}

	@RequestMapping(value = "/facebook", method = RequestMethod.GET)
	public void createFacebookAccessToken(@RequestParam String code) {
		facebookService.createFacebookAccessToken(code);
	}

	@RequestMapping(value = "/getName", method = RequestMethod.GET)
	public String getNameResponse() {
		return facebookService.getName();
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
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<Response> login(@RequestBody Login login, HttpServletResponse resp)
			throws LoginException, UserNotFoundException, UserNotActivatedException {

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
	 */
	@RequestMapping(value = "/activateaccount", method = RequestMethod.GET)
	public ResponseEntity<Response> activateaccount(@RequestHeader("token") String token) throws LoginException {

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
	 */
	@RequestMapping(value = "/resetPasswordLink", method = RequestMethod.GET)
	public ResponseEntity<Response> resetPasswordLink(@RequestParam("email") String email)
			throws MessagingException, UserNotFoundException {

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
	 */
	@RequestMapping(value = "/resetPassword", method = RequestMethod.PUT)
	public ResponseEntity<Response> resetPassword(@RequestHeader("token") String token,
			@RequestBody ResetPassword resetPassword) throws LoginException, UserNotFoundException {
		userService.passwordReset(token, resetPassword);

		Response response = new Response();
		response.setMessage("Password reset successful");
		response.setStatus(32);

		return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
	}
}
