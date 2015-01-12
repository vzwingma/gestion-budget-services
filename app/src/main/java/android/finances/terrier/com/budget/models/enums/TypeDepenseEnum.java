/**
 *
 */
package android.finances.terrier.com.budget.models.enums;

/**
 * Type de dépenses
 *
 * @author vzwingma
 */
public enum TypeDepenseEnum {

    // Crédit
    CREDIT("CREDIT", "+"),
    // Dépense
    DEPENSE("DEPENSE", "-");


    private final String id;
    private final String libelle;

    /**
     * Constructeur
     *
     * @param id      id de l'enum
     * @param libelle libelle de l'enum
     */
    private TypeDepenseEnum(String id, String libelle) {
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
