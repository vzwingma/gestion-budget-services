/*

 */
package com.terrier.finances.gestion.communs.operations.model.enums;

/**
 * Type de dépenses
 * @author vzwingma
 *
 */
public enum EtatOperationEnum {

	// Ligne prévue
	PREVUE("prevue", "Prévue"),
	// Ligne passée
	REALISEE("realisee", "Réalisée"), 
	// Ligne reportée
	REPORTEE("reportee" , "Reportée"), 
	// Ligne annulée
	ANNULEE("annulee", "Annulée");
	
	
	private String id;
	private String libelle;
	
	/**
	 * Constructeur
	 * @param id : id de l'enum
	 * @param libelle : libellé de l'enum
	 */
	EtatOperationEnum(String id, String libelle){
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
	
	
	public static EtatOperationEnum getEnum(String idEnum){
		for (EtatOperationEnum enums : values()) {
			if(enums.getId().equals(idEnum)){
				return enums;
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString(){
		return getLibelle();
	}
}
