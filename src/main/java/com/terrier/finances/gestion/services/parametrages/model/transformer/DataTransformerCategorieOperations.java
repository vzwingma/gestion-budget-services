package com.terrier.finances.gestion.services.parametrages.model.transformer;

import com.terrier.finances.gestion.communs.parametrages.model.CategorieOperation;
import com.terrier.finances.gestion.services.parametrages.model.CategorieOperationDTO;

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
				bo.getListeSSCategories().add(ssBO);
			});
		return bo;
	}

	/**
	 * @param bo business object à transformer
	 * @return dto correspondant
	 */
	public CategorieOperationDTO transformBOtoDTO(CategorieOperation bo) {
		CategorieOperationDTO dto = new CategorieOperationDTO();
		dto.setActif(bo.isActif());
		dto.setCategorie(bo.isCategorie());
		dto.setId(bo.getId());
		dto.setLibelle(bo.getLibelle());
		bo.getListeSSCategories()
			.stream()
			.forEach(ssBO -> {
				CategorieOperationDTO ssDTO = new CategorieOperationDTO();
				ssDTO.setActif(ssBO.isActif());
				ssDTO.setCategorie(ssBO.isCategorie()); // normalement false
				ssDTO.setId(ssBO.getId());
				ssDTO.setLibelle(ssBO.getLibelle());
				ssDTO.setListeSSCategories(null); // déjà une sous catégorie
				dto.getListeSSCategories().add(ssDTO);				
			});
		return dto;
	}

}
