/**
 *
 */
package android.finances.terrier.com.budget.models.enums;

/**
 * Type de dépenses
 *
 * @author vzwingma
 */
public enum EtatLigneDepenseEnum {

    // Ligne prévue
    PREVUE("prevue", "Prévue"),
    // Ligne passée
    REALISEE("realisee", "Réalisée"),
    // Ligne reportée
    REPORTEE("reportee", "Reportée"),
    // Ligne annulée
    ANNULEE("annulee", "Annulée");


    private final String id;
    private final String libelle;

    /**
     * Constructeur
     *
     * @param id      id de l'enum
     * @param libelle libellé de l'enum
     */
    private EtatLigneDepenseEnum(String id, String libelle) {
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
