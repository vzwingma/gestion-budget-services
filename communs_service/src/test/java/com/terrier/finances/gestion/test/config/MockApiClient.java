package com.terrier.finances.gestion.test.config;

import org.springframework.context.annotation.Configuration;

import com.terrier.finances.gestion.services.communs.api.AbstractHTTPClient;

@Configuration
public class MockApiClient extends AbstractHTTPClient{

	@Override
	public String getBaseURL() {
		return "http://localhost:8090";
	}
}