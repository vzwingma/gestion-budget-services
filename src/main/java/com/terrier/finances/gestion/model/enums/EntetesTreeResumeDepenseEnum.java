package com.terrier.finances.gestion.model.enums;



/**
 * Entete du tableau
 * @author vzwingma
 *
 */
public enum EntetesTreeResumeDepenseEnum {

	CATEGORIE("categorie", "Catégorie"),
	VALEUR_NOW("valeurnow", "Au "),
	VALEUR_FIN("valeurfin", "Fin ");
	
	
	private String id;
	private String libelle;
	
	/**
	 * Constructeur
	 * @param id
	 * @param libelle
	 */
	private EntetesTreeResumeDepenseEnum(String id, String libelle){
		this.id = id;
		this.libelle = libelle;
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
	
	
}
