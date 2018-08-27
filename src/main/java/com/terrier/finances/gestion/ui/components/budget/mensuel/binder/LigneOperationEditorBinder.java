/**
 * 
 */
package com.terrier.finances.gestion.ui.components.budget.mensuel.binder;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.terrier.finances.gestion.business.OperationsService;
import com.terrier.finances.gestion.model.business.budget.LigneDepense;
import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.model.data.DataUtils;
import com.terrier.finances.gestion.model.enums.TypeDepenseEnum;
import com.vaadin.data.Binder;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;

/**
 * Binder des champs de LigneDepenses dans le cas du mode éditable
 * @author vzwingma
 *
 */
public class LigneOperationEditorBinder extends Binder<LigneDepense> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8849882826136832053L;

	
	private List<CategorieDepense> setCategories ;

	/**
	 * Constructeur
	 * @param serviceParam
	 */
	public LigneOperationEditorBinder(List<CategorieDepense> setCategories){
		this.setCategories = setCategories;
	}


	private ComboBox<CategorieDepense> cCategories = new  ComboBox<>();
	private ComboBox<CategorieDepense> ssCategories = new  ComboBox<>();
	private ComboBox<TypeDepenseEnum> cTypes = new ComboBox<>();
	private TypeDepenseEnum expectedType = TypeDepenseEnum.DEPENSE;

	/**
	 * @return binding du libellé
	 */
	public Binding<LigneDepense, String> bindLibelle(){
		TextField tLibelle = new TextField();
		return this.forField(tLibelle)
				.withValidator(v -> v != null && v.length() > 0, "Le libellé ne doit pas être nul")
				.bind(LigneDepense::getLibelle, LigneDepense::setLibelle);
	}


	/**
	 * @return binding du type dépense
	 */
	public Binding<LigneDepense, TypeDepenseEnum> bindTypeDepense(){
		cTypes = new ComboBox<>();
		cTypes.setTextInputAllowed(false);
		cTypes.setEmptySelectionAllowed(false);
		cTypes.setItems(TypeDepenseEnum.values());
		return this.forField(cTypes)
				.withValidator(Objects::nonNull, "Le Type de dépense ne peut pas être nul")
				.withValidator(v -> expectedType.equals(v), "L'opération est une "+expectedType.getId()+". La valeur doit être " + expectedType.getLibelle())
				.bind(LigneDepense::getTypeDepense, LigneDepense::setTypeDepense);
	}

	/**
	 * @return binding de la valeur
	 */
	public Binding<LigneDepense, String> bindValeur(){
		TextField tValeur = new TextField();
		// Validation de la valeur
		return this.forField(tValeur)
				.withConverter(DataUtils::getValueFromString, String::valueOf)
				.withValidator(v -> v != null, "La valeur ne doit pas être nulle ou incorrecte")
                .withValidator(v -> {
                    return (!Double.isInfinite(Double.valueOf(v)) && !Double.isNaN(Double.valueOf(v)));
                }, "La valeur est incorrecte")
				.bind(LigneDepense::getValeurAbsStringFromDouble, LigneDepense::setValeurAbsStringToDouble);
	}


	/**
	 * @return binding périodique
	 */
	public Binding<LigneDepense, Boolean> bindPeriodique(){
		return this.bind(new CheckBox(), LigneDepense::isPeriodique, LigneDepense::setPeriodique);
	}


	/**
	 * @return binding périodique
	 */
	public Binding<LigneDepense, Date> bindDate(){
		TextField valeurDate = new TextField();
		valeurDate.setEnabled(false);
		// Pas de validateur. Valeur en readonly
		return this.forField(valeurDate).withConverter(new DateOperationEditorConverter()).bind(LigneDepense::getDateMaj, LigneDepense::setDateMaj);
	}


	/**
	 * @return binding périodique
	 */
	public Binding<LigneDepense, CategorieDepense> bindCategories(){
		cCategories.setEnabled(false);
		return this.forField(cCategories).withValidator(Objects::nonNull, "La catégorie est obligatoire").bind(LigneDepense::getCategorie, LigneDepense::setCategorie);
	}
	/**
	 * @return binding périodique
	 */
	public Binding<LigneDepense, CategorieDepense> bindSSCategories(){


		// Liste des sous catégories 
		Set<CategorieDepense> sousCategories = setCategories.stream()
				.map(CategorieDepense::getListeSSCategories)
				.flatMap(Set::stream)
				// Sauf transfert intercompte et réservice
				.filter(c -> 
						!OperationsService.ID_SS_CAT_TRANSFERT_INTERCOMPTE.equals(c.getId()) 
						&&	! OperationsService.ID_SS_CAT_RESERVE.equals(c.getId()))
				.sorted((c1, c2) -> c1.getLibelle().compareTo(c2.getLibelle()))
				.collect(Collectors.toSet());
		ssCategories.setItems(sousCategories);
		ssCategories.setEmptySelectionAllowed(false);
		ssCategories.setTextInputAllowed(false);
		ssCategories.setEnabled(true);
		// Update auto de la catégorie et du type
		ssCategories.addSelectionListener(event -> {
			cCategories.setValue(event.getSelectedItem().get().getCategorieParente());
			if((OperationsService.ID_SS_CAT_SALAIRE.equals(this.ssCategories.getSelectedItem().get().getId()) 
					|| OperationsService.ID_SS_CAT_REMBOURSEMENT.equals(this.ssCategories.getSelectedItem().get().getId()))){
				expectedType = TypeDepenseEnum.CREDIT;
			}
			else{
				expectedType = TypeDepenseEnum.DEPENSE;
			}
			cTypes.setValue(expectedType);
			
		});
		return this.forField(ssCategories)
				.withValidator(Objects::nonNull, "La sous catégorie est obligatoire")
				.bind(LigneDepense::getSsCategorie, LigneDepense::setSsCategorie);
	}
}