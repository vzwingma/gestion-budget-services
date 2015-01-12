package android.finances.terrier.com.budget.models;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Catégorie de dépense
 *
 * @author vzwingma
 */
public class DepenseCategorie implements Serializable, Comparable<DepenseCategorie> {

    /**
     *
     */
    private static final long serialVersionUID = -1755547774539137070L;
    /**
     * Liste des sous catégories
     */
    private final Set<DepenseCategorie> listeSSCategories = new HashSet<DepenseCategorie>();
    /**
     * Identifiant
     */
    private String id;
    /**
     * Libelle
     */
    private String libelle;
    /**
     * Actif
     */
    private boolean actif;
    /**
     * Liste des id des sous catégories
     */
    private Set<String> listeIdsSSCategories;
    /**
     * Catégorie
     */
    private DepenseCategorie categorieParente;

    /**
     * Catégorie parente
     */
    private String idCategorieParente;
    /**
     * Est ce une catégorie ?
     */
    private boolean categorie = true;


    /**
     * Constructeur pour Spring Data MongSB
     */
    public DepenseCategorie() {
    }


    /**
     * @return the libelle
     */
    public String getLibelle() {
        return libelle;
    }

    /**
     * @param libelle the libelle to set
     */
    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }


    /**
     * @return the listeSSCategories
     */
    public Set<DepenseCategorie> getListeSSCategories() {
        return listeSSCategories;
    }


    /**
     * @return the categorieParente
     */
    public DepenseCategorie getCategorieParente() {
        return categorieParente;
    }

    /**
     * @param categorieParente the categorieParente to set
     */
    public void setCategorieParente(DepenseCategorie categorieParente) {
        this.categorieParente = categorieParente;
    }

    /**
     * @return the categorie
     */
    public boolean isCategorie() {
        return categorie;
    }

    /**
     * @param categorie the categorie to set
     */
    public void setCategorie(boolean categorie) {
        this.categorie = categorie;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.libelle;
    }

    /**
     * @return the idf
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the actif
     */
    public boolean isActif() {
        return actif;
    }

    /**
     * @param actif the actif to set
     */
    public void setActif(boolean actif) {
        this.actif = actif;
    }

    /**
     * @return the listeIdsSSCategories
     */
    public Set<String> getListeIdsSSCategories() {
        return listeIdsSSCategories;
    }

    /**
     * @param listeIdsSSCategories the listeIdsSSCategories to set
     */
    public void setListeIdsSSCategories(Set<String> listeIdsSSCategories) {
        this.listeIdsSSCategories = listeIdsSSCategories;
    }

    /**
     * @return the idCategorieParente
     */
    public String getIdCategorieParente() {
        return idCategorieParente;
    }

    /**
     * @param idCategorieParente the idCategorieParente to set
     */
    public void setIdCategorieParente(String idCategorieParente) {
        this.idCategorieParente = idCategorieParente;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(DepenseCategorie o) {
        if (o != null) {
            return this.libelle.compareTo(o.getLibelle());
        }
        return 0;
    }
}
