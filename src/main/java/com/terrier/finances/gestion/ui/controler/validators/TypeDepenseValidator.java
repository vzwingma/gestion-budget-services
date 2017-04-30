/**
 * 
 */
package com.terrier.finances.gestion.ui.controler.validators;

import com.terrier.finances.gestion.business.BusinessDepensesService;
import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.model.enums.TypeDepenseEnum;
import com.vaadin.v7.data.Validator;

/**
 * @author vzwingma
 *
 */
public class TypeDepenseValidator implements Validator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -624292040215572087L;

	private CategorieDepense ssCategorie;

	/**
	 * @param ssCategorie
	 */
	public TypeDepenseValidator(CategorieDepense ssCategorie){
		this.ssCategorie = ssCategorie;
	}

	/* (non-Javadoc)
	 * @see com.vaadin.data.Validator#validate(java.lang.Object)
	 */
	@Override
	public void validate(Object value) throws InvalidValueException {
		if(value != null){
			if( value instanceof TypeDepenseEnum){
				TypeDepenseEnum typeAttendu = TypeDepenseEnum.DEPENSE;
				if(BusinessDepensesService.ID_SS_CAT_SALAIRE.equals(ssCategorie.getId()) || BusinessDepensesService.ID_SS_CAT_REMBOURSEMENT.equals(ssCategorie.getId())){
					typeAttendu = TypeDepenseEnum.CREDIT;
				}
				// Cohérence type
				if(!typeAttendu.equals((TypeDepenseEnum)value)){
					throw new InvalidValueException("Le type de la dépense doit être ["+ typeAttendu.getId()+ " ("+typeAttendu.getLibelle()+")] pour une dépense de la catégorie {" + ssCategorie.getLibelle()+"}");					
				}
			}
		}
		
	}

}
