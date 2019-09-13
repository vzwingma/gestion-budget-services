package com.terrier.finances.gestion.test.config;

import org.springframework.context.annotation.Configuration;

import com.terrier.finances.gestion.services.communs.api.AbstractHTTPClient;

@Configuration
public class MockApiClient extends AbstractHTTPClient{

	
	private String baseURL;
	
	@Override
	public String getBaseURL() {
		return this.baseURL;
	}

	/**
	 * @param baseURL the baseURL to set
	 */
	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}
}