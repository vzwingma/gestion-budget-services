/**
 * 
 */
package com.terrier.finances.gestion.data.transformer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.terrier.finances.gestion.data.ParametragesDatabaseService;
import com.terrier.finances.gestion.model.IDataTransformer;
import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.model.data.parametrage.CategorieDepenseDTO;
import com.terrier.finances.gestion.model.data.parametrage.CategorieDepenseXO;

/**
 * DataTransformer
 * @author vzwingma
 *
 */
@Component("dataTransformerCategoriesDepense")
public class DataTransformerCategoriesDepense extends IDataTransformer<CategorieDepense, CategorieDepenseDTO, CategorieDepenseXO> {

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
	public CategorieDepense transformDTOtoBO(CategorieDepenseDTO dto) {
		return null;
	}


	
	/**
	 * @param bos lliste de BOs
	 * @param encryptor encryptor
	 * @return liste des DTO
	 */
	public List<CategorieDepenseDTO> transformBOstoDTOs(List<CategorieDepense> bos) {
		
		List<CategorieDepenseDTO> dtos = new ArrayList<>();
		for (CategorieDepense categorieDepenseBO : bos) {
			CategorieDepenseDTO dto = transformBOtoDTO(categorieDepenseBO);
			dtos.add(dto);
		}
		return dtos;
	}
	
	
	/**
	 * Transformation des BO en XO
	 * @param bos liste de BO
	 * @return liste de XO
	 */
	public List<CategorieDepenseXO> transformBOstoXOs(List<CategorieDepense> bos) {
		
		List<CategorieDepenseXO> xos = new ArrayList<>();
		for (CategorieDepense categorieDepenseBO : bos) {
			CategorieDepenseXO xo = transformBOtoXO(categorieDepenseBO);
			xos.add(xo);
		}
		return xos;
	}
	
	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.IDataTransformer#transformBOtoDTO(java.lang.Object, org.jasypt.util.text.BasicTextEncryptor)
	 */
	@Override
	public CategorieDepenseDTO transformBOtoDTO(CategorieDepense bo) {
		CategorieDepenseDTO dto = new CategorieDepenseDTO();
		dto.setId(bo.getId());
		dto.setCategorie(bo.isCategorie());
		dto.setIdCategorieParente(bo.getIdCategorieParente());
		dto.setLibelle(bo.getLibelle());
		for (CategorieDepense ssCatBO : bo.getListeSSCategories()) {
			dto.getListeSSCategories().add(transformBOtoDTO(ssCatBO));
		}
		return dto;
	}
	
	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.IDataTransformer#transformBOtoXO(java.lang.Object)
	 */
	@Override
	public CategorieDepenseXO transformBOtoXO(CategorieDepense bo) {
		CategorieDepenseXO xo = new CategorieDepenseXO();
		xo.setId(bo.getId());
		xo.setCategorie(bo.isCategorie());
		xo.setIdCategorieParente(bo.getIdCategorieParente());
		xo.setLibelle(bo.getLibelle());
		for (CategorieDepense ssCatBO : bo.getListeSSCategories()) {
			xo.getListeSSCategories().add(transformBOtoXO(ssCatBO));
		}
		return xo;
	}
}
