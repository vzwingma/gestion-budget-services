package io.github.vzwingma.finances.budget.services.operations.business.model.operation;

/**
 * Périodicité d'une opération
 * @author vzwingma
 *
 */
public enum OperationPeriodiciteEnum {

	PONCTUELLE("Ponctuelle", "0"),
	MENSUELLE("Mensuelle", "1"),
	// Dépense
	TRIMESTRIELLE("Trimestrielle", "3"),
	SEMESTRIELLE("Semestrielle", "6"),
	ANNUELLE("Annuelle", "12");



	private final String id;
	private final String nbMois;

	/**
	 * Constructeur
	 * @param id : id de l'énum
	 * @param nbMois : période de l'enum
	 */
    OperationPeriodiciteEnum(String id, String nbMois){
		this.id = id;
		this.nbMois = nbMois;
	}

	
	
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}




	/**
	 * @return the période
	 */
	public int getNbMois() {
		return Integer.parseInt(nbMois);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString(){
		return getId();
	}
}
