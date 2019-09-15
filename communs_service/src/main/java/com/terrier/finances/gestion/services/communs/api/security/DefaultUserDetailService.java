package com.terrier.finances.gestion.services.communs.api.security;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.terrier.finances.gestion.communs.utilisateur.model.Utilisateur;

@Service
public class DefaultUserDetailService implements UserDetailsService {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUserDetailService.class);

	@Override
	public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
		LOGGER.info("[idUser=?] Tentative d'authentification de {}", login);
		Utilisateur utilisateur = new Utilisateur();
		utilisateur.setId("vzwingmann");
		utilisateur.setLogin("vzwingmann");

		LOGGER.warn("[idUser={}] Création de l'utilisateur [{}] ", utilisateur.getId(), login);
		List<GrantedAuthority> grantedAuthorities = utilisateur.getDroits()
				.entrySet()
				.stream()
				.filter(Entry::getValue)
				.map(e -> new SimpleGrantedAuthority(e.getKey().name()))
				.collect(Collectors.toList());

		LOGGER.info(" Droits {}", grantedAuthorities); 
		return new User(utilisateur.getLogin(), utilisateur.getPassword(), grantedAuthorities);

	}

}
