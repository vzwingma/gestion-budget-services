/**
 * 
 */
package com.terrier.finances.gestion.business.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.terrier.finances.gestion.business.AuthenticationService;
import com.terrier.finances.gestion.model.business.parametrage.Utilisateur;

/**
 * Controleur REST pour récupérer les budgets
 * @author vzwingma
 *
 */
@RestController
@RequestMapping(value="/rest/admin")
public class AdminRestController {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminRestController.class);

	@Autowired
	private AuthenticationService authService;

	/**
	 * Appel PING
	 * @return résultat du ping
	 */
	@RequestMapping(value="/v1/password/{login}/{oldpassword}/{newpassword}", method=RequestMethod.GET)
	public String ping(@PathVariable("login") String login, @PathVariable("oldpassword") String oldpassword, @PathVariable("newpassword") String newPassword){
		LOGGER.info("Changement du mot de passe pour {}", login);
		
		Utilisateur utilisateur = authService.authenticate(login, oldpassword);
		
		if(utilisateur != null){
			authService.changePassword(utilisateur, oldpassword, newPassword);
			String returnOK = "Le mot de passe de "+utilisateur.getLibelle()+ " a bien été modifié : \n " + utilisateur.toFullString();
			LOGGER.error(returnOK);
			return returnOK;
		}
		else{
			String returnErr = "L'utilisateur est introuvable ou le mot de passe est incorrect";
			LOGGER.error(returnErr);
			return returnErr;
		}
		
		
	}
	
}
