package com.terrier.finances.gestion.services.parametrages.model.transformer;

import com.terrier.finances.gestion.communs.parametrages.model.CategorieOperation;
import com.terrier.finances.gestion.services.parametrages.model.v12.CategorieOperationDTO;

/**
 * Transformer
 * @author vzwingma
 *
 */
public class DataTransformerCategorieOperations {


	/**
	 * @param dto dto à transfomer
	 * @return bo business object
	 */
	public CategorieOperation transformDTOtoBO(CategorieOperationDTO dto) {
		
		CategorieOperation bo = new CategorieOperation();
		bo.setActif(dto.isActif());
		bo.setCategorie(dto.isCategorie());
		bo.setId(dto.getId());
		bo.setLibelle(dto.getLibelle());
		bo.setCategorieParente(null);

		dto.getListeSSCategories()
			.stream()
			.forEach(ssDTO -> {
				CategorieOperation ssBO = new CategorieOperation();
				ssBO.setActif(ssDTO.isActif());
				ssBO.setCategorie(ssDTO.isCategorie());
				ssBO.setId(ssDTO.getId());
				ssBO.setLibelle(ssDTO.getLibelle());
				ssBO.setCategorieParente(bo);
				ssBO.setListeSSCategories(null);
				bo.getListeSSCategories().add(ssBO);
			});
		return bo;
	}
}
