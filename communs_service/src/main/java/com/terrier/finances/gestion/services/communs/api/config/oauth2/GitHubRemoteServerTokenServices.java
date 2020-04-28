package com.terrier.finances.gestion.services.communs.api.config.oauth2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;



/** 
 * Queries the /check_token endpoint to obtain the contents of an access token.
 *
 * If the endpoint returns a 400 response, this indicates that the token is invalid.
 *
 */
@Service
public class GitHubRemoteServerTokenServices implements ResourceServerTokenServices{

	private static final Logger LOGGER = LoggerFactory.getLogger(GitHubRemoteServerTokenServices.class);

	private RestOperations restTemplate;

	private AccessTokenConverter tokenConverter = new GitHubAccessTokenConverter();

	private Map<String, OAuth2Authentication> cacheAuthentication = new HashMap<>();
	
	/**
	 * Constructeur
	 */
	public GitHubRemoteServerTokenServices() {
		restTemplate = new RestTemplate();
		((RestTemplate) restTemplate).setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			// Ignore 400
			public void handleError(ClientHttpResponse response) throws IOException {
				if (response.getRawStatusCode() != 400) {
					super.handleError(response);
				}
			}
		});
	}


    /**
     * Charge l'authentification à partir de l'access token
     */
    @Override
	public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {
    	LOGGER.debug("[OAuth2={}] Load", accessToken);
    	OAuth2Authentication authentication = cacheAuthentication.get(accessToken);
    	if(authentication == null) {
    		LOGGER.debug("[OAuth2={}] Chargement depuis l'Authentication Server", accessToken);
    		authentication = loadAuthenticationFromRemote(accessToken);
    	}
    	if(authentication != null) {
    		LOGGER.debug("[OAuth2={}] Authentification [p={}, auth={}]", accessToken, authentication.getPrincipal(), authentication.isAuthenticated());
    		authentication.setDetails(accessToken);
    		return authentication;
    	}
    	else {
    		LOGGER.warn("[OAuth2={}] Authentification non trouvée", accessToken);
    		return null;
    	}
    	
    	
    }
    
    /**
     * Charge l'authentification à partir de l'access token sur Github
     */
	public OAuth2Authentication loadAuthenticationFromRemote(String accessToken) throws AuthenticationException, InvalidTokenException {

		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		Map<String, Object> map = getForUserInfo("https://api.github.com/user", headers);

		if (map.containsKey("error")) {
			LOGGER.error("check_token returned error: {}", map.get("error"));
			throw new InvalidTokenException(accessToken);
		}

		// gh-838
		if (map.containsKey(GitHubAccessTokenConverter.GITHUB_ACTIVE_ATTRIBUTE) && !"true".equals(String.valueOf(map.get(GitHubAccessTokenConverter.GITHUB_ACTIVE_ATTRIBUTE)))) {
			LOGGER.debug("check_token returned active attribute: {}", map.get(GitHubAccessTokenConverter.GITHUB_ACTIVE_ATTRIBUTE));
			throw new InvalidTokenException(accessToken);
		}

		return tokenConverter.extractAuthentication(map);
	}

	@Override
	public OAuth2AccessToken readAccessToken(String accessToken) {
		throw new UnsupportedOperationException("Not supported: read access token");
	}

	/**
	 * @param path chemin du GitHub User
	 * @param headers entête
	 * @return map du User @GitHub
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> getForUserInfo(String path, HttpHeaders headers) {
		if (headers.getContentType() == null) {
			headers.setContentType(MediaType.APPLICATION_JSON);
		}
		return restTemplate.exchange(path, HttpMethod.GET,
				new HttpEntity<MultiValueMap<String, String>>(null, headers), Map.class).getBody();
	}

}