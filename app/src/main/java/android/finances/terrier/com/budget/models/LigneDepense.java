package android.finances.terrier.com.budget.models;

import android.finances.terrier.com.budget.models.enums.EtatLigneDepenseEnum;
import android.finances.terrier.com.budget.models.enums.TypeDepenseEnum;
import android.finances.terrier.com.budget.utils.Logger;

import java.io.Serializable;
import java.util.Date;

/**
 * Ligne de dépense dans un budget mensuel
 *
 * @author vzwingma
 */
public class LigneDepense implements Comparable<LigneDepense>, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2932267709864103657L;
    // Logger
    private static final Logger LOGGER = new Logger(BudgetMensuel.class);
    // Id
    private String id;
    // SS Catégorie
    private DepenseCategorie ssCategorie;
    private String idSSCategorie;
    private String idCategorie;
    // Libellé
    private String libelle;
    // Type de dépense
    private TypeDepenseEnum typeDepense;
    // Etat de la ligne
    private EtatLigneDepenseEnum etat;
    // Valeur
    private float valeur;
    // Date operation
    private Date dateOperation;
    // Date mise à jour
    private Date dateMaj;
    // Auteur MAJ
    private String auteur;
    // Périodique
    private boolean periodique;
    // tag comme dernière opération
    private boolean derniereOperation;

    // Constructeur
    public LigneDepense() {
    }

    /**
     * @return the id
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
     * @return the ssCategorie
     */
    public DepenseCategorie getSsCategorie() {
        return ssCategorie;
    }

    /**
     * @param ssCategorie the ssCategorie to set
     */
    public void setSsCategorie(DepenseCategorie ssCategorie) {
        LOGGER.trace("> MAJ de la catégorie de la dépense : " + ssCategorie);
        this.idSSCategorie = ssCategorie != null ? ssCategorie.getId() : null;
        this.idCategorie = ssCategorie != null ? ssCategorie.getIdCategorieParente() : null;
        this.ssCategorie = ssCategorie;
    }

    /**
     * @return the categorie
     */
    public DepenseCategorie getCategorie() {
        return this.ssCategorie != null ? this.ssCategorie.getCategorieParente() : null;
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
     * @return the typeDepense
     */
    public TypeDepenseEnum getTypeDepense() {
        return typeDepense;
    }

    /**
     * @param typeDepense the typeDepense to set
     */
    public void setTypeDepense(TypeDepenseEnum typeDepense) {
        this.typeDepense = typeDepense;
    }

    /**
     * @return the valeur
     */
    public float getValeur() {
        return valeur;
    }

    /**
     * @param valeur the valeur to set
     */
    public void setValeur(float valeur) {
        this.valeur = valeur;
    }

    /**
     * @return the dateOperation
     */
    public Date getDateOperation() {
        return dateOperation;
    }

    /**
     * @param dateOperation the dateOperation to set
     */
    public void setDateOperation(Date dateOperation) {
        this.dateOperation = dateOperation;
    }

    /**
     * @return the dateMaj
     */
    public Date getDateMaj() {
        return dateMaj;
    }

    /**
     * @param dateMaj the dateMaj to set
     */
    public void setDateMaj(Date dateMaj) {
        this.dateMaj = dateMaj;
    }

    /**
     * @return the auteur
     */
    public String getAuteur() {
        return auteur;
    }

    /**
     * @param auteur the auteur to set
     */
    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    /**
     * @return the etat
     */
    public EtatLigneDepenseEnum getEtat() {
        return etat;
    }

    /**
     * @param etat the etat to set
     */
    public void setEtat(EtatLigneDepenseEnum etat) {
        this.etat = etat;
    }

    /**
     * @return the periodique
     */
    public boolean isPeriodique() {
        return periodique;
    }

    /**
     * @param periodique the periodique to set
     */
    public void setPeriodique(boolean periodique) {
        this.periodique = periodique;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "LigneDepense [id=" + id + ", ssCategorie=" + idSSCategorie + " (" + ssCategorie
                + "), libelle=" + libelle + ", typeDepense=" + typeDepense
                + ", etat=" + etat + ", valeur=" + valeur + ", dateOperation="
                + dateOperation + ", dateMaj=" + dateMaj + ", auteur=" + auteur
                + ", periodique=" + periodique + "]";
    }

    /**
     * @return the idSSCategorie
     */
    public String getIdSSCategorie() {
        return idSSCategorie;
    }

    /**
     * @param idSSCategorie the idSSCategorie to set
     */
    public void setIdSSCategorie(String idSSCategorie) {
        this.idSSCategorie = idSSCategorie;
    }

    /**
     * @return the idCategorie
     */
    public String getIdCategorie() {
        return idCategorie;
    }

    /**
     * @param idCategorie the idCategorie to set
     */
    public void setIdCategorie(String idCategorie) {
        this.idCategorie = idCategorie;
    }

    @Override
    public int compareTo(LigneDepense o) {
        if (o != null) {
            return this.getId().compareTo(o.getId());
        }
        return 0;
    }

    /**
     * @return the derniereOperation
     */
    public boolean isDerniereOperation() {
        return derniereOperation;
    }

    /**
     * @param derniereOperation the derniereOperation to set
     */
    public void setDerniereOperation(boolean derniereOperation) {
        this.derniereOperation = derniereOperation;
    }
}
