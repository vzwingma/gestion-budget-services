package com.terrier.finances.gestion.model.enums;

import java.util.Date;

import com.terrier.finances.gestion.model.business.parametrage.CategorieDepense;
import com.terrier.finances.gestion.ui.components.budget.mensuel.ActionsLigneBudget;
import com.vaadin.ui.PopupView;



/**
 * Entete du tableau
 * @author vzwingma
 *
 */
public enum EntetesTableSuiviDepenseEnum {

	/**
	 * IMPORTANT : utilise invoke pour setter dynamiquement les properties
	 * L'id doit respecter la norme suivante :
	 * 	1er caractère en majuscule
	 * 	doit correspondre à la méthode setXXXX de LigneDepense
	 */
	CATEGORIE		("Categorie", 		"Catégorie", 		CategorieDepense.class),
	SSCATEGORIE		("SsCategorie", 	"Ss catégorie", 	CategorieDepense.class),
	LIBELLE_VIEW	("LibelleView",		"Description", 		PopupView.class),
	LIBELLE			("Libelle",			"Description", 		String.class),
	TYPE			("TypeDepense", 	"Operation", 		TypeDepenseEnum.class),
	VALEUR			("Valeur", 			"Valeur", 			Float.class),
	PERIODIQUE		("Periodique", 		"Mensuel", 			Boolean.class),
	DATE_OPERATION	("DateOperation",	"Jour opération", 	Date.class),
	ACTIONS			("Actions", 		"Actions", 			ActionsLigneBudget.class),
	DATE_MAJ		("DateMaj", 		"Date MAJ", 		Date.class),
	AUTEUR			("Auteur", 			"Auteur", 			String.class);
	
	
	private String id;
	private String libelle;
	private Class<?> type;
	/**
	 * Constructeur
	 * @param id
	 * @param libelle
	 */
	private EntetesTableSuiviDepenseEnum(String id, String libelle, Class<?> type){
		this.id = id;
		this.libelle = libelle;
		this.type = type;
	}

	
	
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}




	/**
	 * @return the libelle
	 */
	public String getLibelle() {
		return libelle;
	}




	/**
	 * @return the type
	 */
	public Class<?> getType() {
		return type;
	}
	
	
	
}
