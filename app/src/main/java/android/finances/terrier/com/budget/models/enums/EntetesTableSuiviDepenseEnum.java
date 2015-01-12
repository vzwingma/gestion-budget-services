package android.finances.terrier.com.budget.models.enums;


/**
 * Entete du tableau
 *
 * @author vzwingma
 */
public enum EntetesTableSuiviDepenseEnum {

    /**
     * IMPORTANT : utilise invoke pour setter dynamiquement les properties
     * L'id doit respecter la norme suivante :
     * 1er caractère en majuscule
     * doit correspondre à la méthode setXXXX de LigneDepense
     */
    CATEGORIE("Categorie", "Catégorie"),
    SSCATEGORIE("SsCategorie", "Ss catégorie"),
    LIBELLE("Libelle", "Description"),
    TYPE("TypeDepense", "Operation"),
    VALEUR("Valeur", "Valeur"),
    PERIODIQUE("Periodique", "Mensuel"),
    DATE_OPERATION("DateOperation", "Jour opération"),
    ACTIONS("Actions", "Actions"),
    DATE_MAJ("DateMaj", "Date MAJ"),
    AUTEUR("Auteur", "Auteur");


    private final String id;
    private final String libelle;

    /**
     * Constructeur
     *
     * @param id      id de l'enum
     * @param libelle libellé de l'enum
     */
    private EntetesTableSuiviDepenseEnum(String id, String libelle) {
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
