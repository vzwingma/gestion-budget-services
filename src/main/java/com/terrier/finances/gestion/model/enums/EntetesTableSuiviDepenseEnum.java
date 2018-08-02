package com.terrier.finances.gestion.model.enums;

/**
 * Entete du tableau
 * @author vzwingma
 *
 */
public enum EntetesTableSuiviDepenseEnum {

	CATEGORIE		("Catégorie"),
	SSCATEGORIE		("Ss catégorie"),
	LIBELLE			("Description"),
	TYPE			("Operation"),
	VALEUR			("Valeur"),
	PERIODIQUE		("Mensuel"),
	DATE_OPERATION	("Jour opération"),
	ACTIONS			("Actions"),
	DATE_MAJ		("Date MAJ"),
	AUTEUR			("Auteur");
	
	
	private String libelle;

	/**
	 * Constructeur
	 * @param id
	 * @param libelle
	 */
	private EntetesTableSuiviDepenseEnum(String libelle){
		this.libelle = libelle;
	}

	
	/**
	 * @return the libelle
	 */
	public String getLibelle() {
		return libelle;
	}
}
