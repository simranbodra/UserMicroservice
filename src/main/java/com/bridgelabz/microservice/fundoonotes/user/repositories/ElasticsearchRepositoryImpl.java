package com.bridgelabz.microservice.fundoonotes.user.repositories;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.bridgelabz.microservice.fundoonotes.user.exceptions.ElasticsearchFailException;
import com.bridgelabz.microservice.fundoonotes.user.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class ElasticsearchRepositoryImpl implements ElasticsearchRepository {

	@Value("${index}")
	private String index;

	@Value("${type}")
	private String type;

	@Autowired
	private RestHighLevelClient restHighLevelClient;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void save(User user) throws ElasticsearchFailException {
		Map dataMap = objectMapper.convertValue(user, Map.class);
		IndexRequest indexRequest = new IndexRequest(index, type, user.getUserId()).source(dataMap);

		try {
			restHighLevelClient.index(indexRequest);
		} catch (ElasticsearchException | IOException exception) {
			throw new ElasticsearchFailException("Fail to get response");
		}
	}

	@Override
	public Optional<User> findByEmail(String userEmail) throws ElasticsearchFailException {

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.matchPhrasePrefixQuery("email", userEmail));

		SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.types(type);
		searchRequest.source(sourceBuilder);

		Optional<User> optionalUser = null;
		SearchResponse searchResponse = null;

		try {
			searchResponse = restHighLevelClient.search(searchRequest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		SearchHits hits = searchResponse.getHits();

		String user = hits.getAt(0).getSourceAsString();

		try {
			optionalUser = Optional.of(objectMapper.readValue(user, User.class));
		} catch (IOException exception) {
			throw new ElasticsearchFailException("Fail to get response");
		}

		return optionalUser;
	}

	@Override
	public Optional<User> findById(String userId) throws ElasticsearchFailException {
		GetRequest getRequest = new GetRequest(index, type, userId);

		GetResponse getResponse = null;

		Optional<User> optionalUser = null;

		try {
			getResponse = restHighLevelClient.get(getRequest);

			String userData = getResponse.getSourceAsString();

			optionalUser = Optional.of(objectMapper.readValue(userData, User.class));
		} catch (IOException exception) {
			throw new ElasticsearchFailException("Fail to get response");
		}

		return optionalUser;
	}
}