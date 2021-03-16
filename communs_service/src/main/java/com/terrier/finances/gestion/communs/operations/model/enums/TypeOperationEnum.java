package com.terrier.finances.gestion.communs.operations.model.enums;

/**
 * Type de dépenses
 * @author vzwingma
 *
 */
public enum TypeOperationEnum {

	// Crédit
	CREDIT("CREDIT", "+"),
	// Dépense
	DEPENSE("DEPENSE", "-");
	
	

	private String id;
	private String libelle;
	
	/**
	 * Constructeur
	 * @param id : id de l'énum
	 * @param libelle : libellé de l'enum
	 */
    TypeOperationEnum(String id, String libelle){
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
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString(){
		return getLibelle();
	}
}
