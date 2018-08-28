
/**
 * 
 */
package com.terrier.finances.gestion.budget.model.transformer;

import org.jasypt.util.text.BasicTextEncryptor;


/**
 * Transformer de données entre les couches
 * @author vzwingma
 *
 */
public abstract class IDataTransformer<B, D> {

	/**
	 * @return l'encryptor de l'utilisateur courant
	
	public BasicTextEncryptor getEncryptor(){
		if(this.encryptor == null){
			if(SecurityContextHolder.getContext().getAuthentication() != null){
				this.encryptor = ((Utilisateur)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEncryptor();	
			}
			else{
				this.encryptor =  UserSessionsManager.get().getSession().getUtilisateurCourant().getEncryptor();
			}
		}
		return this.encryptor;
	}
	 */


	/**
	 * Transformation d'un DTO en BO
	 * @param dto data object (Mongo)
	 * @return bo business object
	 */
	public abstract B transformDTOtoBO(D dto, BasicTextEncryptor decryptor);
	/**
	 * Transformation d'un BO en TO
	 * @return dto data object (Mongo)
	 * @param bo business object
	 */
	public abstract D transformBOtoDTO(B bo, BasicTextEncryptor encryptor);
	

}
