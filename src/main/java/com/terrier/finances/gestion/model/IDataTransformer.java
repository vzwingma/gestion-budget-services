/**
 * 
 */
package com.terrier.finances.gestion.model;

import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import com.terrier.finances.gestion.model.business.parametrage.Utilisateur;
import com.terrier.finances.gestion.ui.sessions.UISessionManager;


/**
 * Transformer
 * @author vzwingma
 *
 */
public abstract class IDataTransformer<BO, DTO> {

	
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(IDataTransformer.class);

	
	
	/**
	 * @return l'encryptor de l'utilisateur courant
	 */
	public BasicTextEncryptor getEncryptor(){
		if(SecurityContextHolder.getContext().getAuthentication() != null){
			return ((Utilisateur)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEncryptor();	
		}
		else{
			return  UISessionManager.getSession().getUtilisateurCourant().getEncryptor();
		}
		
	}
	/**
	 * Transformation d'un DTO en BO
	 * @param dto data object (Mongo)
	 * @return bo business object
	 */
	public abstract BO transformDTOtoBO(DTO dto);
	/**
	 * Transformation d'un BO en TO
	 * @return dto data object (Mongo)
	 * @param bo business object
	 */
	public abstract DTO transformBOtoDTO(BO bo);
}
