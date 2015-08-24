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
import com.terrier.finances.gestion.model.business.budget.LigneDepense;
import com.terrier.finances.gestion.model.data.budget.LigneDepenseDTO;
import com.terrier.finances.gestion.model.enums.EtatLigneDepenseEnum;
import com.terrier.finances.gestion.model.enums.TypeDepenseEnum;
import com.terrier.finances.gestion.model.exception.DataNotFoundException;

/**
 * DataTransformer
 * @author vzwingma
 *
 */
@Component("dataTransformerLigneDepense")
public class DataTransformerLigneDepense extends IDataTransformer<LigneDepense, LigneDepenseDTO> {

	
	@Autowired
	private ParametragesDatabaseService parametrageService;
	
	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.IDataTransformer#transformDTOtoBO(java.lang.Object, org.jasypt.util.text.BasicTextEncryptor)
	 */
	@Override
	public LigneDepense transformDTOtoBO(LigneDepenseDTO dto) {
		
		BasicTextEncryptor decryptor = getEncryptor();
		
		LigneDepense bo = new LigneDepense();
		bo.setId(dto.getId());
		if(dto.getAuteur() !=null){
			bo.setAuteur(decryptor.decrypt(dto.getAuteur()));
		}
		bo.setDateMaj(dto.getDateMaj());
		bo.setDateOperation(dto.getDateOperation());
		bo.setDerniereOperation(dto.isDerniereOperation());
		bo.setEtat(EtatLigneDepenseEnum.valueOf(decryptor.decrypt(dto.getEtat())));
		try {
			bo.setSsCategorie(parametrageService.chargeCategorieParId(decryptor.decrypt(dto.getIdSSCategorie())));
		} catch (DataNotFoundException e) {	}
		bo.setLibelle(decryptor.decrypt(dto.getLibelle()));
		if(dto.getNotes() != null){
			bo.setNotes(decryptor.decrypt(dto.getNotes()));
		}
		
		bo.setPeriodique(dto.isPeriodique());
		bo.setTypeDepense(TypeDepenseEnum.valueOf(decryptor.decrypt(dto.getTypeDepense())));
		bo.setValeur(Float.valueOf(decryptor.decrypt(dto.getValeur())));
		return bo;
	}
	
	

	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.IDataTransformer#transformBOtoDTO(java.lang.Object, org.jasypt.util.text.BasicTextEncryptor)
	 */
	@Override
	public LigneDepenseDTO transformBOtoDTO(LigneDepense bo) {
		
		BasicTextEncryptor encryptor = getEncryptor();
		
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
		dto.setNotes(bo.getNotes() != null ? encryptor.encrypt(bo.getNotes()) : null);
		dto.setPeriodique(bo.isPeriodique());
		dto.setTypeDepense(encryptor.encrypt(bo.getTypeDepense().name()));
		dto.setValeur(encryptor.encrypt(String.valueOf(bo.getValeur())));
		return dto;
	}


	
	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.IDataTransformer#transformBOtoDTO(java.lang.Object)
	 */
	public List<LigneDepenseDTO> transformBOtoDTO(List<LigneDepense> listeBO) {
		List<LigneDepenseDTO> listeDepensesDTO = new ArrayList<>();
		if(listeBO != null){
			for (LigneDepense bo : listeBO) {
				listeDepensesDTO.add(transformBOtoDTO(bo));
			}
		}
		return listeDepensesDTO;
	}

	

	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.IDataTransformer#transformDTOtoBO(java.lang.Object)
	 */
	public List<LigneDepense> transformDTOtoBO(List<LigneDepenseDTO> listeDTO) {
		List<LigneDepense> listeDepensesBO = new ArrayList<>();
		if(listeDTO != null){
			for (LigneDepenseDTO dto : listeDTO) {
				listeDepensesBO.add(transformDTOtoBO(dto));
			}
		}
		return listeDepensesBO;
	}
	
	


	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.IDataTransformer#transformDTOtoBO(java.lang.Object)
	 */
	public List<LigneDepenseDTO> decryptDTO(List<LigneDepenseDTO> listeDTO, BasicTextEncryptor decryptor) {
		List<LigneDepenseDTO> listeDepensesDTO = new ArrayList<>();
		if(listeDTO != null){
			for (LigneDepenseDTO dto : listeDTO) {
				listeDepensesDTO.add(decryptDTO(dto, decryptor));
			}
		}
		return listeDepensesDTO;
	}
	
	

	/* (non-Javadoc)
	 * @see com.terrier.finances.gestion.model.IDataTransformer#transformDTOtoBO(java.lang.Object, org.jasypt.util.text.BasicTextEncryptor)
	 */
	
	private LigneDepenseDTO decryptDTO(LigneDepenseDTO dto, BasicTextEncryptor decryptor) {
		if(dto.getAuteur() !=null){
			dto.setAuteur(decryptor.decrypt(dto.getAuteur()));
		}
		dto.setEtat(decryptor.decrypt(dto.getEtat()));
		dto.setIdCategorie(decryptor.decrypt(dto.getIdCategorie()));
		dto.setIdSSCategorie(decryptor.decrypt(dto.getIdSSCategorie()));
		dto.setLibelle(decryptor.decrypt(dto.getLibelle()));
		dto.setNotes(decryptor.decrypt(dto.getNotes()));
		dto.setTypeDepense(decryptor.decrypt(dto.getTypeDepense()));
		dto.setValeur(decryptor.decrypt(dto.getValeur()));
		return dto;
	}
	
	
}
