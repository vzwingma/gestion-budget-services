/**
 * 
 */
package com.terrier.finances.gestion.model;

import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.security.core.context.SecurityContextHolder;

import com.terrier.finances.gestion.model.business.parametrage.Utilisateur;
import com.terrier.finances.gestion.ui.sessions.UISessionManager;


/**
 * Transformer de données entre les couches
 * @author vzwingma
 *
 */
public abstract class IDataTransformer<BO, DTO> {


	private BasicTextEncryptor encryptor; 

	/**
	 * @return l'encryptor de l'utilisateur courant
	 */
	public BasicTextEncryptor getEncryptor(){
		if(this.encryptor == null){
			if(SecurityContextHolder.getContext().getAuthentication() != null){
				this.encryptor = ((Utilisateur)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEncryptor();	
			}
			else{
				this.encryptor =  UISessionManager.get().getSession().getUtilisateurCourant().getEncryptor();
			}
		}
		return this.encryptor;
	}
	
	/**
	 * @param encryptor the encryptor to set
	 */
	public void setEncryptor(BasicTextEncryptor encryptor) {
		this.encryptor = encryptor;
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
