package com.terrier.finances.gestion.services.communs.api.config.oauth2;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.stereotype.Component;


@Component
public class GitHubAccessTokenConverter implements AccessTokenConverter {

	private String clientIdAttribute = "GitHub";

	private static final String GITHUB_USER_ATTRIBUTE = "login";
	private static final String GITHUB_AUTH_ATTRIBUTE = "type";
	public static final String GITHUB_ACTIVE_ATTRIBUTE = "active";
	/**
	 *
	 */
	public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
		Map<String, String> parameters = new HashMap<String, String>();
		
		Authentication user = null;
		if (map.containsKey(GITHUB_USER_ATTRIBUTE)) {
			Object principal = map.get(GITHUB_USER_ATTRIBUTE);
			Collection<? extends GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList((String)map.get(GITHUB_AUTH_ATTRIBUTE));
			user = new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
		}
		parameters.put(CLIENT_ID, clientIdAttribute);
	
		Collection<? extends GrantedAuthority> authorities = null;
		if (user==null && map.containsKey(AUTHORITIES)) {
			@SuppressWarnings("unchecked")
			String[] roles = ((Collection<String>)map.get(AUTHORITIES)).toArray(new String[0]);
			authorities = AuthorityUtils.createAuthorityList(roles);
		}
		OAuth2Request request = new OAuth2Request(parameters, clientIdAttribute, authorities, true, null, null, null, null,
				null);
		return new OAuth2Authentication(request, user);
	}

	@Override
	public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
		throw new UnsupportedOperationException("Not supported: convertAccessToken");
	}

	@Override
	public OAuth2AccessToken extractAccessToken(String value, Map<String, ?> map) {
		throw new UnsupportedOperationException("Not supported: extractAccessToken");
	}

}