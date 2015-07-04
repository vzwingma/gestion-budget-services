/**
 * 
 */
package com.terrier.finances.gestion.model;

import org.jasypt.util.text.BasicTextEncryptor;

/**
 * Transformer
 * @author vzwingma
 *
 */
public interface IDataTransformer<BO, DTO> {

	
	/**
	 * Transformation d'un DTO en BO
	 * @param dto data object (Mongo)
	 * @return bo business object
	 */
	public abstract BO transformDTOtoBO(DTO dto, BasicTextEncryptor decryptor);
	/**
	 * Transformation d'un BO en TO
	 * @return dto data object (Mongo)
	 * @param bo business object
	 */
	public abstract DTO transformBOtoDTO(BO bo, BasicTextEncryptor encryptor);
}
