package com.terrier.finances.gestion.test.config;

import java.util.UUID;

import org.springframework.context.annotation.Configuration;

import com.terrier.finances.gestion.communs.api.config.ApiUrlConfigEnum;
import com.terrier.finances.gestion.communs.utilisateur.model.api.UtilisateurPrefsAPIObject;
import com.terrier.finances.gestion.services.communs.api.AbstractHTTPClient;

@Configuration
public class  MockApiClient extends AbstractHTTPClient<UtilisateurPrefsAPIObject>{

	public MockApiClient() {
		super(UtilisateurPrefsAPIObject.class);
	}
	
	@Override
	public ApiUrlConfigEnum getConfigServiceURI() {
		return ApiUrlConfigEnum.APP_CONFIG_URL_COMPTES;
	}

	@Override
	public String getCorrId() {
		return UUID.randomUUID().toString();
	}
}