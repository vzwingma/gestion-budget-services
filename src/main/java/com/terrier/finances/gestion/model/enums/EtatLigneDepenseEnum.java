/**
 * 
 */
package com.terrier.finances.gestion.model.enums;

/**
 * Type de dépenses
 * @author vzwingma
 *
 */
public enum EtatLigneDepenseEnum {

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
	 * @param id
	 * @param libelle
	 */
	private EtatLigneDepenseEnum(String id, String libelle){
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
	
	
	public static EtatLigneDepenseEnum getEnum(String idEnum){
		for (EtatLigneDepenseEnum enums : values()) {
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
