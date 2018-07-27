/**
 * 
 */
package com.terrier.finances.gestion.ui.components.budget.mensuel.binder;

import java.util.Set;
import java.util.stream.Collectors;

import com.terrier.finances.gestion.business.ParametragesService;
import com.terrier.finances.gestion.model.business.budget.LigneDepense;
import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.model.enums.EtatLigneDepenseEnum;
import com.terrier.finances.gestion.model.enums.TypeDepenseEnum;
import com.terrier.finances.gestion.ui.components.budget.mensuel.ActionsLigneBudget;
import com.vaadin.data.Binder;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;

/**
 * Binder des champs de LigneDepenses dans le cas du mode éditable
 * @author vzwingma
 *
 */
public class LigneDepenseBinder extends Binder<LigneDepense> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8849882826136832053L;

	private ParametragesService serviceParam ;

	public LigneDepenseBinder(ParametragesService serviceParam){
		this.serviceParam = serviceParam;
	}

	/**
	 * @return binding du libellé
	 */
	public Binding<LigneDepense, String> bindLibelle(){
		return bind(new TextField(), LigneDepense::getLibelle, LigneDepense::setLibelle);
	}


	/**
	 * @return binding du type dépense
	 */
	public Binding<LigneDepense, TypeDepenseEnum> bindTypeDepense(){
		ComboBox<TypeDepenseEnum> cTypes = new ComboBox<TypeDepenseEnum>();
		cTypes.setItems(TypeDepenseEnum.values());
		return bind(cTypes, LigneDepense::getTypeDepense, LigneDepense::setTypeDepense);
	}


	/**
	 * @return binding de la valeur
	 */
	public Binding<LigneDepense, String> bindValeur(){
		return bind(new TextField(), LigneDepense::getValeurS, LigneDepense::setValeurS);
	}


	/**
	 * @return binding périodique
	 */
	public Binding<LigneDepense, Boolean> bindPeriodique(){
		return bind(new CheckBox(), LigneDepense::isPeriodique, LigneDepense::setPeriodique);
	}

	/**
	 * @return binding périodique
	 */
	public Binding<LigneDepense, CategorieDepense> bindCategories(){
		ComboBox<CategorieDepense> ssCategories = new  ComboBox<CategorieDepense>();

		// Liste des sous catégories
		Set<CategorieDepense> sousCategories = serviceParam.getCategories().stream()
				.map(c -> c.getListeSSCategories())
				.flatMap(c -> c.stream())
				.sorted((c1, c2) -> c1.getLibelle().compareTo(c2.getLibelle()))
				.collect(Collectors.toSet());
		ssCategories.setItems(sousCategories);
		ssCategories.setEmptySelectionAllowed(false);
		ssCategories.setTextInputAllowed(false);
		ssCategories.setEnabled(true);
		return bind( ssCategories, LigneDepense::getSsCategorie, LigneDepense::setSsCategorie);
	}
}
