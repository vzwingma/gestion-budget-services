/*

 */
package io.github.vzwingma.finances.budget.services.operations.business.model.operation;

/**
 * Type de dépenses
 * @author vzwingma
 *
 */
public enum OperationEtatEnum {

	// Ligne prévue
	PREVUE("prevue", "Prévue"),
	// Ligne passée
	REALISEE("realisee", "Réalisée"), 
	// Ligne reportée
	REPORTEE("reportee" , "Reportée"), 
	// Ligne annulée
	ANNULEE("annulee", "Annulée");
	
	
	private final String id;
	private final String libelle;
	
	/**
	 * Constructeur
	 * @param id : id de l'enum
	 * @param libelle : libellé de l'enum
	 */
	OperationEtatEnum(String id, String libelle){
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
	
	
	public static OperationEtatEnum getEnum(String idEnum){
		for (OperationEtatEnum enums : values()) {
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
