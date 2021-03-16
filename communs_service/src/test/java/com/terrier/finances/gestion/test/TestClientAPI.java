package com.terrier.finances.gestion.test;

import com.terrier.finances.gestion.communs.api.AbstractHTTPReactiveClient;
import com.terrier.finances.gestion.communs.api.config.ApiUrlConfigEnum;

import java.util.UUID;

/**
 *  Client API de test pour les TU
 */
public class TestClientAPI extends AbstractHTTPReactiveClient {


    @Override
    public ApiUrlConfigEnum getConfigServiceURI() {
        return ApiUrlConfigEnum.APP_CONFIG_URL_PARAMS;
    }

    @Override
    public String getCorrId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String getAccessToken() {
        return UUID.randomUUID().toString();
    }
}
