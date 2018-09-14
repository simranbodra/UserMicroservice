package com.bridgelabz.microservice.fundoonotes.user.repositories;

import java.util.Optional;

import com.bridgelabz.microservice.fundoonotes.user.exceptions.ElasticsearchFailException;
import com.bridgelabz.microservice.fundoonotes.user.models.User;

public interface ElasticsearchRepository {
	
	public void save(User user) throws ElasticsearchFailException;

	public Optional<User> findByEmail(String email) throws ElasticsearchFailException;
	
	public Optional<User> findById(String userId) throws ElasticsearchFailException;
}
