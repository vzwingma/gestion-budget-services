
/**
 * 
 */
package com.terrier.finances.gestion.data.transformer;

import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.security.core.context.SecurityContextHolder;

import com.terrier.finances.gestion.model.business.parametrage.Utilisateur;


/**
 * Transformer de données entre les couches
 * @author vzwingma
 *
 */
public abstract class IDataTransformer<B, D> {


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
		//		this.encryptor =  UserSessionsManager.get().getSession().getUtilisateurCourant().getEncryptor();
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
	public abstract B transformDTOtoBO(D dto);
	/**
	 * Transformation d'un BO en TO
	 * @return dto data object (Mongo)
	 * @param bo business object
	 */
	public abstract D transformBOtoDTO(B bo);
	

}
