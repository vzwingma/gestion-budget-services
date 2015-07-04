/**
 * 
 */
package com.terrier.finances.gestion.data.transformer;

import java.util.ArrayList;
import java.util.List;

import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.terrier.finances.gestion.data.ParametragesDatabaseService;
import com.terrier.finances.gestion.model.IDataTransformer;
import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.model.data.parametrage.CategorieDepenseDTO;

/**
 * DataTransformer
 * @author vzwingma
 *
 */
@Component("dataTransformerCategoriesDepense")
public class DataTransformerCategoriesDepense implements IDataTransformer<CategorieDepense, CategorieDepenseDTO> {

	@Autowired
	private ParametragesDatabaseService parametrageService;
	/**
	 * Constructeur pour Spring
	 */
	public DataTransformerCategoriesDepense(){ }

	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.IDataTransformer#transformDTOtoBO(java.lang.Object, org.jasypt.util.text.BasicTextEncryptor)
	 */
	@Override
	public CategorieDepense transformDTOtoBO(CategorieDepenseDTO dto,
			BasicTextEncryptor decryptor) {
		// TODO Auto-generated method stub
		return null;
	}


	
	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.IDataTransformer#transformBOtoDTO(java.lang.Object, org.jasypt.util.text.BasicTextEncryptor)
	 */
	public List<CategorieDepenseDTO> transformBOstoDTOs(List<CategorieDepense> bos,
			BasicTextEncryptor encryptor) {
		
		List<CategorieDepenseDTO> dtos = new ArrayList<>();
		for (CategorieDepense categorieDepenseBO : bos) {
			CategorieDepenseDTO dto = transformBOtoDTO(categorieDepenseBO, encryptor);
			dtos.add(dto);
		}
		return dtos;
	}
	
	
	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.IDataTransformer#transformBOtoDTO(java.lang.Object, org.jasypt.util.text.BasicTextEncryptor)
	 */
	@Override
	public CategorieDepenseDTO transformBOtoDTO(CategorieDepense bo,
			BasicTextEncryptor encryptor) {
		CategorieDepenseDTO dto = new CategorieDepenseDTO();
		dto.setId(bo.getId());
		dto.setCategorie(bo.isCategorie());
		dto.setIdCategorieParente(bo.getIdCategorieParente());
		dto.setLibelle(bo.getLibelle());
		for (CategorieDepense ssCatBO : bo.getListeSSCategories()) {
			dto.getListeSSCategories().add(transformBOtoDTO(ssCatBO, encryptor));
		}
		return dto;
	}

}
