/**
 *
 */
package android.finances.terrier.com.budget.models.data.transformers;

import org.jasypt.util.text.BasicTextEncryptor;

/**
 * Transformer
 *
 * @author vzwingma
 */
public interface IDataTransformer<BO, DTO> {


    /**
     * Transformation d'un DTO en BO
     *
     * @param dto       data object (Mongo)
     * @param decryptor décrypteur de DTO
     * @return bo business object
     */
    public abstract BO transformDTOtoBO(DTO dto, BasicTextEncryptor decryptor);

    /**
     * Transformation d'un BO en TO
     *
     * @param bo        business object
     * @param encryptor encrypteur de BO
     * @return dto data object (Mongo)
     */
    public abstract DTO transformBOtoDTO(BO bo, BasicTextEncryptor encryptor);
}
