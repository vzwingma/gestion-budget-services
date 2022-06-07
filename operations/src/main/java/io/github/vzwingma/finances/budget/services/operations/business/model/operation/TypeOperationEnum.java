package io.github.vzwingma.finances.budget.services.operations.business.model.operation;

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
	
	

	private final String id;
	private final String libelle;
	
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
