/**
 *
 */
package android.finances.terrier.com.budget.models.data.transformers;

import android.finances.terrier.com.budget.models.LigneDepense;
import android.finances.terrier.com.budget.models.data.LigneDepenseDTO;
import android.finances.terrier.com.budget.models.enums.EtatLigneDepenseEnum;
import android.finances.terrier.com.budget.models.enums.TypeDepenseEnum;

import org.jasypt.util.text.BasicTextEncryptor;

import java.util.ArrayList;
import java.util.List;

/**
 * DataTransformer
 *
 * @author vzwingma
 */
public class DataTransformerLigneDepense implements IDataTransformer<LigneDepense, LigneDepenseDTO> {


    /**
     * Logger
     */
    // private static final Logger LOGGER = new Logger(DataTransformerLigneDepense.class);

//	private ParametragesDatabaseService parametrageService;

    /* (non-Javadoc)
     * @see com.terrier.finances.gestion.model.IDataTransformer#transformDTOtoBO(java.lang.Object, org.jasypt.util.text.BasicTextEncryptor)
     */
    @Override
    public LigneDepense transformDTOtoBO(LigneDepenseDTO dto, BasicTextEncryptor decryptor) {
        LigneDepense bo = new LigneDepense();
        bo.setId(dto.getId());
        bo.setAuteur(decryptor.decrypt(dto.getAuteur()));
        bo.setDateMaj(dto.getDateMaj());
        bo.setDateOperation(dto.getDateOperation());
        bo.setDerniereOperation(dto.isDerniereOperation());
        bo.setEtat(EtatLigneDepenseEnum.valueOf(decryptor.decrypt(dto.getEtat())));
        /*try {
            bo.setSsCategorie(parametrageService.chargeCategorieParId(decryptor.decrypt(dto.getIdSSCategorie())));
		} catch (DataNotFoundException e) {	}
		*/
        bo.setLibelle(decryptor.decrypt(dto.getLibelle()));
        bo.setPeriodique(dto.isPeriodique());
        bo.setTypeDepense(TypeDepenseEnum.valueOf(decryptor.decrypt(dto.getTypeDepense())));
        bo.setValeur(Float.valueOf(decryptor.decrypt(dto.getValeur())));
        return bo;
    }


    /* (non-Javadoc)
     * @see com.terrier.finances.gestion.model.IDataTransformer#transformBOtoDTO(java.lang.Object, org.jasypt.util.text.BasicTextEncryptor)
     */
    @Override
    public LigneDepenseDTO transformBOtoDTO(LigneDepense bo, BasicTextEncryptor encryptor) {
        LigneDepenseDTO dto = new LigneDepenseDTO();
        dto.setAuteur(encryptor.encrypt(bo.getAuteur()));
        dto.setDateMaj(bo.getDateMaj());
        dto.setDateOperation(bo.getDateOperation());
        dto.setDerniereOperation(bo.isDerniereOperation());
        dto.setEtat(encryptor.encrypt(bo.getEtat().name()));
        dto.setId(bo.getId());
        dto.setIdCategorie(bo.getCategorie() != null ? encryptor.encrypt(bo.getCategorie().getId()) : null);
        dto.setIdSSCategorie(bo.getSsCategorie() != null ? encryptor.encrypt(bo.getSsCategorie().getId()) : null);
        dto.setLibelle(encryptor.encrypt(bo.getLibelle()));
        dto.setPeriodique(bo.isPeriodique());
        dto.setTypeDepense(encryptor.encrypt(bo.getTypeDepense().name()));
        dto.setValeur(encryptor.encrypt(String.valueOf(bo.getValeur())));
        return dto;
    }


    /* (non-Javadoc)
     * @see com.terrier.finances.gestion.model.IDataTransformer#transformBOtoDTO(java.lang.Object)
     */
    public List<LigneDepenseDTO> transformBOtoDTO(List<LigneDepense> listeBO, BasicTextEncryptor encryptor) {
        List<LigneDepenseDTO> listeDepensesDTO = new ArrayList<>();
        if (listeBO != null) {
            for (LigneDepense bo : listeBO) {
                listeDepensesDTO.add(transformBOtoDTO(bo, encryptor));
            }
        }
        return listeDepensesDTO;
    }


    /* (non-Javadoc)
     * @see com.terrier.finances.gestion.model.IDataTransformer#transformDTOtoBO(java.lang.Object)
     */
    public List<LigneDepense> transformDTOtoBO(List<LigneDepenseDTO> listeDTO, BasicTextEncryptor decryptor) {
        List<LigneDepense> listeDepensesBO = new ArrayList<>();
        if (listeDTO != null) {
            for (LigneDepenseDTO dto : listeDTO) {
                listeDepensesBO.add(transformDTOtoBO(dto, decryptor));
            }
        }
        return listeDepensesBO;
    }
}
