
/**
 * 
 */
package com.terrier.finances.gestion.services.budgets.model.transformer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.terrier.finances.gestion.communs.operations.model.LigneOperation;
import com.terrier.finances.gestion.communs.operations.model.enums.EtatOperationEnum;
import com.terrier.finances.gestion.communs.operations.model.enums.TypeOperationEnum;
import com.terrier.finances.gestion.communs.parametrages.model.CategorieOperation;
import com.terrier.finances.gestion.services.budgets.api.client.ParametragesAPIClient;
import com.terrier.finances.gestion.services.budgets.model.LigneDepenseDTO;

/**
 * DataTransformer
 * @author vzwingma
 *
 */
@Component("dataTransformerLigneDepense")
public class DataTransformerLigneOperation implements IDataTransformer<LigneOperation, LigneDepenseDTO> {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DataTransformerLigneOperation.class);
	
	@Autowired
	private ParametragesAPIClient parametrageService;
	
	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.IDataTransformer#transformDTOtoBO(java.lang.Object, org.jasypt.util.text.BasicTextEncryptor)
	 */
	@Override
	public LigneOperation transformDTOtoBO(LigneDepenseDTO dto) {
		LigneOperation bo = new LigneOperation();
		bo.setId(dto.getId());
		if(dto.getAuteur() !=null){
			bo.setAuteur(dto.getAuteur());
		}
		bo.setDateMaj(dto.getDateMaj());
		bo.setDateOperation(dto.getDateOperation());
		bo.setDerniereOperation(dto.isDerniereOperation());
		bo.setEtat(EtatOperationEnum.valueOf(dto.getEtat()));
		bo.setIdSsCategorie(dto.getIdSSCategorie());
		CategorieOperation ssCat = parametrageService.getCategorieParId(bo.getIdSsCategorie());
		if(ssCat != null){
			bo.setSsCategorie(ssCat);
		}
		else{
			return null;
		}
		
		bo.setLibelle(dto.getLibelle());
		bo.setPeriodique(dto.isPeriodique());
		bo.setTypeDepense(TypeOperationEnum.valueOf(dto.getTypeDepense()));

		bo.setValeurAbsStringToDouble(dto.getValeur());
		LOGGER.trace("	[{}] \n > Transformation en BO > [{}]", dto, bo);
		return bo;
	}
	
	

	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.IDataTransformer#transformBOtoDTO(java.lang.Object, org.jasypt.util.text.BasicTextEncryptor)
	 */
	@Override
	public LigneDepenseDTO transformBOtoDTO(LigneOperation bo) {
		
		LigneDepenseDTO dto = new LigneDepenseDTO();
		dto.setAuteur(bo.getAuteur());
		dto.setDateMaj(bo.getDateMaj());
		dto.setDateOperation(bo.getDateOperation());
		dto.setDerniereOperation(bo.isDerniereOperation());
		dto.setEtat(bo.getEtat().name());
		dto.setId(bo.getId());
		dto.setIdSSCategorie(bo.getIdSsCategorie());
		dto.setLibelle(bo.getLibelle());
		dto.setPeriodique(bo.isPeriodique());
		dto.setTypeDepense(bo.getTypeDepense().name());
		
		Double depenseVal =  Math.abs(bo.getValeur());
		if(bo.getTypeDepense().equals(TypeOperationEnum.DEPENSE)){
				depenseVal = -depenseVal;
		}
		dto.setValeur(String.valueOf(depenseVal));
		
		LOGGER.trace("	[{}] \n > Transformation en DTO > [{}]", bo, dto);
		return dto;
	}



	
	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.IDataTransformer#transformBOtoDTO(java.lang.Object)
	 */
	public List<LigneDepenseDTO> transformBOtoDTO(List<LigneOperation> listeBO) {
		List<LigneDepenseDTO> listeDepensesDTO = new ArrayList<>();
		if(listeBO != null){
			listeBO.stream().forEach(bo -> listeDepensesDTO.add(transformBOtoDTO(bo)));
		}
		return listeDepensesDTO;
	}

	

	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.IDataTransformer#transformDTOtoBO(java.lang.Object)
	 */
	public List<LigneOperation> transformDTOtoBO(List<LigneDepenseDTO> listeDTO) {
		List<LigneOperation> listeDepensesBO = new ArrayList<>();
		if(listeDTO != null && !listeDTO.isEmpty()){
			listeDTO.stream().forEach(dto -> {
				LigneOperation bo = transformDTOtoBO(dto);
				if(dto != null && bo != null){
					listeDepensesBO.add(bo);	
				}
			});
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
		ligneOperationClonee.setIdSsCategorie(ligneOperation.getIdSsCategorie());
		ligneOperationClonee.setDateMaj(Calendar.getInstance().getTime());
		ligneOperationClonee.setDateOperation(null);
		ligneOperationClonee.setEtat(EtatOperationEnum.PREVUE);
		ligneOperationClonee.setPeriodique(ligneOperation.isPeriodique());
		ligneOperationClonee.setTypeDepense(ligneOperation.getTypeDepense());
		ligneOperationClonee.setValeurAbsStringToDouble(Double.toString(Math.abs(ligneOperation.getValeur())));
		ligneOperationClonee.setDerniereOperation(false);
		return ligneOperationClonee;
	}
}
