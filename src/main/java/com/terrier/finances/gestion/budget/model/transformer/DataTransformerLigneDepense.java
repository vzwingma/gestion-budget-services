
/**
 * 
 */
package com.terrier.finances.gestion.budget.model.transformer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.terrier.finances.gestion.budget.model.LigneDepenseDTO;
import com.terrier.finances.gestion.model.enums.EtatLigneOperationEnum;
import com.terrier.finances.gestion.model.enums.TypeOperationEnum;
import com.terrier.finances.gestion.operations.model.LigneOperation;
import com.terrier.finances.gestion.parametrages.data.ParametragesDatabaseService;

/**
 * DataTransformer
 * @author vzwingma
 *
 */
@Component("dataTransformerLigneDepense")
public class DataTransformerLigneDepense extends IDataTransformer<LigneOperation, LigneDepenseDTO> {

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
	public LigneOperation transformDTOtoBO(LigneDepenseDTO dto, BasicTextEncryptor decryptor) {
		
		LigneOperation bo = new LigneOperation();
		bo.setId(dto.getId());
		if(dto.getAuteur() !=null){
			bo.setAuteur(decryptor.decrypt(dto.getAuteur()));
		}
		bo.setDateMaj(dto.getDateMaj());
		bo.setDateOperation(dto.getDateOperation());
		bo.setDerniereOperation(dto.isDerniereOperation());
		bo.setEtat(EtatLigneOperationEnum.valueOf(decryptor.decrypt(dto.getEtat())));
		bo.setSsCategorie(parametrageService.chargeCategorieParId(decryptor.decrypt(dto.getIdSSCategorie())));
		bo.setLibelle(decryptor.decrypt(dto.getLibelle()));
		bo.setPeriodique(dto.isPeriodique());
		bo.setTypeDepense(TypeOperationEnum.valueOf(decryptor.decrypt(dto.getTypeDepense())));

		bo.setValeurAbsStringToDouble(decryptor.decrypt(dto.getValeur()));
		LOGGER.trace("	[{}] \n > Transformation en BO > [{}]", dto, bo);
		return bo;
	}
	
	

	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.IDataTransformer#transformBOtoDTO(java.lang.Object, org.jasypt.util.text.BasicTextEncryptor)
	 */
	@Override
	public LigneDepenseDTO transformBOtoDTO(LigneOperation bo, BasicTextEncryptor encryptor) {
		
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
		if(bo.getTypeDepense().equals(TypeOperationEnum.DEPENSE)){
				depenseVal = -depenseVal;
		}
		dto.setValeur(encryptor.encrypt(String.valueOf(depenseVal)));
		
		LOGGER.trace("	[{}] \n > Transformation en DTO > [{}]", bo, dto);
		return dto;
	}



	
	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.IDataTransformer#transformBOtoDTO(java.lang.Object)
	 */
	public List<LigneDepenseDTO> transformBOtoDTO(List<LigneOperation> listeBO, BasicTextEncryptor encryptor) {
		List<LigneDepenseDTO> listeDepensesDTO = new ArrayList<>();
		if(listeBO != null){
			listeBO.stream().forEach(bo -> listeDepensesDTO.add(transformBOtoDTO(bo, encryptor)));
		}
		return listeDepensesDTO;
	}

	

	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.IDataTransformer#transformDTOtoBO(java.lang.Object)
	 */
	public List<LigneOperation> transformDTOtoBO(List<LigneDepenseDTO> listeDTO, BasicTextEncryptor decryptor) {
		List<LigneOperation> listeDepensesBO = new ArrayList<>();
		if(listeDTO != null){
			listeDTO.stream().forEach(dto -> listeDepensesBO.add(transformDTOtoBO(dto, decryptor)));
		}
		return listeDepensesBO;
	}

	
	/**
	 * @return Ligne dépense clonée
	 * @throws CloneNotSupportedException
	 */
	public LigneOperation cloneDepenseToMoisSuivant(LigneOperation ligneOperation) {
		LigneOperation ligneOperationClonee = new LigneOperation();
		ligneOperationClonee.setId(UUID.randomUUID().toString());
		ligneOperationClonee.setLibelle(ligneOperation.getLibelle());
		ligneOperationClonee.setSsCategorie(ligneOperation.getSsCategorie());
		ligneOperationClonee.setDateMaj(Calendar.getInstance().getTime());
		ligneOperationClonee.setDateOperation(null);
		ligneOperationClonee.setEtat(EtatLigneOperationEnum.PREVUE);
		ligneOperationClonee.setPeriodique(ligneOperation.isPeriodique());
		ligneOperationClonee.setTypeDepense(ligneOperation.getTypeDepense());
		ligneOperationClonee.setValeurAbsStringToDouble(Double.toString(Math.abs(ligneOperation.getValeur())));
		ligneOperationClonee.setDerniereOperation(false);
		return ligneOperationClonee;
	}
}
