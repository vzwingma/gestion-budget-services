
/**
 * 
 */
package com.terrier.finances.gestion.services.budget.model.transformer;

import org.jasypt.util.text.BasicTextEncryptor;


/**
 * Transformer de données entre les couches
 * @author vzwingma
 *
 */
public interface IDataTransformer<B, D> {


	/**
	 * Transformation d'un DTO en BO
	 * @param dto data object (Mongo)
	 * @return bo business object
	 */
	public B transformDTOtoBO(D dto, BasicTextEncryptor decryptor);
	/**
	 * Transformation d'un BO en TO
	 * @return dto data object (Mongo)
	 * @param bo business object
	 */
	public D transformBOtoDTO(B bo, BasicTextEncryptor encryptor);
	

}
