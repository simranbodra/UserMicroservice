package com.bridgelabz.microservice.fundoonotes.user;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bridgelabz.microservice.fundoonotes.user.models.Registration;
import com.bridgelabz.microservice.fundoonotes.user.models.Response;
import com.bridgelabz.microservice.fundoonotes.user.models.User;
import com.bridgelabz.microservice.fundoonotes.user.repositories.ElasticsearchRepository;
import com.bridgelabz.microservice.fundoonotes.user.service.UserService;

public class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private ElasticsearchRepository elasticsearchRepository;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void addUserTest() {
		//when(elasticsearchRepository.save((User) any(User.class))).thenReturn(new Response());

	}

}
