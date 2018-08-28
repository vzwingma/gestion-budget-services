
/**
 * 
 */
package com.terrier.finances.gestion.data.transformer;

import java.util.ArrayList;
import java.util.List;

import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.terrier.finances.gestion.data.ParametragesDatabaseService;
import com.terrier.finances.gestion.model.business.budget.LigneDepense;
import com.terrier.finances.gestion.model.data.budget.LigneDepenseDTO;
import com.terrier.finances.gestion.model.enums.EtatLigneDepenseEnum;
import com.terrier.finances.gestion.model.enums.TypeDepenseEnum;

/**
 * DataTransformer
 * @author vzwingma
 *
 */
@Component("dataTransformerLigneDepense")
public class DataTransformerLigneDepense extends IDataTransformer<LigneDepense, LigneDepenseDTO> {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DataTransformerLigneDepense.class);
	
	@Autowired
	private ParametragesDatabaseService parametrageService;
	
	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.IDataTransformer#transformDTOtoBO(java.lang.Object, org.jasypt.util.text.BasicTextEncryptor)
	 */
	@Override
	public LigneDepense transformDTOtoBO(LigneDepenseDTO dto, BasicTextEncryptor decryptor) {
		
		LigneDepense bo = new LigneDepense(true);
		bo.setId(dto.getId());
		if(dto.getAuteur() !=null){
			bo.setAuteur(decryptor.decrypt(dto.getAuteur()));
		}
		bo.setDateMaj(dto.getDateMaj());
		bo.setDateOperation(dto.getDateOperation());
		bo.setDerniereOperation(dto.isDerniereOperation());
		bo.setEtat(EtatLigneDepenseEnum.valueOf(decryptor.decrypt(dto.getEtat())));
		bo.setSsCategorie(parametrageService.chargeCategorieParId(decryptor.decrypt(dto.getIdSSCategorie())));
		bo.setLibelle(decryptor.decrypt(dto.getLibelle()));
		bo.setPeriodique(dto.isPeriodique());
		bo.setTypeDepense(TypeDepenseEnum.valueOf(decryptor.decrypt(dto.getTypeDepense())));

		bo.setValeurAbsStringToDouble(decryptor.decrypt(dto.getValeur()));





		LOGGER.trace("	[{}] \n > Transformation en BO > [{}]", dto, bo);
		return bo;
	}
	
	

	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.IDataTransformer#transformBOtoDTO(java.lang.Object, org.jasypt.util.text.BasicTextEncryptor)
	 */
	@Override
	public LigneDepenseDTO transformBOtoDTO(LigneDepense bo, BasicTextEncryptor encryptor) {
		
		LigneDepenseDTO dto = new LigneDepenseDTO();
		dto.setAuteur( encryptor.encrypt(bo.getAuteur()));
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
		
		Double depenseVal =  Math.abs(bo.getValeur());
		if(bo.getTypeDepense().equals(TypeDepenseEnum.DEPENSE)){
				depenseVal = -depenseVal;
		}
		dto.setValeur(encryptor.encrypt(String.valueOf(depenseVal)));
		
		LOGGER.trace("	[{}] \n > Transformation en DTO > [{}]", bo, dto);
		return dto;
	}



	
	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.IDataTransformer#transformBOtoDTO(java.lang.Object)
	 */
	public List<LigneDepenseDTO> transformBOtoDTO(List<LigneDepense> listeBO, BasicTextEncryptor encryptor) {
		List<LigneDepenseDTO> listeDepensesDTO = new ArrayList<>();
		if(listeBO != null){
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
		if(listeDTO != null){
			for (LigneDepenseDTO dto : listeDTO) {
				listeDepensesBO.add(transformDTOtoBO(dto, decryptor));
			}
		}
		return listeDepensesBO;
	}

}
