package android.finances.terrier.com.budget.models.data;

import android.finances.terrier.com.budget.models.CompteBancaire;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Budget du mois
 *
 * @author vzwingma
 */
public class BudgetMensuelDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 4393433203514049021L;
    private String id;
    /**
     * Mois du budget
     */
    private int mois;
    private int annee;
    /**
     * Budget actif
     */
    private boolean actif = false;
    /**
     * Date de mise à jour
     */
    private Date dateMiseAJour;
    /**
     * Compte bancaire
     */
    private CompteBancaire compteBancaire;
    /**
     * Résultat du mois précédent
     */
    private String resultatMoisPrecedent;

    private String margeSecurite;
    private String margeSecuriteFinMois;

    /**
     * Totaux
     */
    private String nowArgentAvance;
    private String nowCompteReel;
    private String finArgentAvance;
    private String finCompteReel;

    private Map<String, String[]> totalParCategories = new HashMap<>();
    private Map<String, String[]> totalParSSCategories = new HashMap<>();

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
     * @return the nowArgentAvance
     */
    public String getNowArgentAvance() {
        return nowArgentAvance;
    }

    /**
     * @param nowArgentAvance the nowArgentAvance to set
     */
    public void setNowArgentAvance(String nowArgentAvance) {
        this.nowArgentAvance = nowArgentAvance;
    }

    /**
     * @return the nowCompteReel
     */
    public String getNowCompteReel() {
        return nowCompteReel;
    }

    /**
     * @param nowCompteReel the nowCompteReel to set
     */
    public void setNowCompteReel(String nowCompteReel) {
        this.nowCompteReel = nowCompteReel;
    }

    /**
     * @return the finArgentAvance
     */
    public String getFinArgentAvance() {
        return finArgentAvance;
    }

    /**
     * @param finArgentAvance the finArgentAvance to set
     */
    public void setFinArgentAvance(String finArgentAvance) {
        this.finArgentAvance = finArgentAvance;
    }

    /**
     * @return the finCompteReel
     */
    public String getFinCompteReel() {
        return finCompteReel;
    }

    /**
     * @param finCompteReel the finCompteReel to set
     */
    public void setFinCompteReel(String finCompteReel) {
        this.finCompteReel = finCompteReel;
    }

    /**
     * @return the mois
     */
    public int getMois() {
        return mois;
    }

    /**
     * @param mois the mois to set
     */
    public void setMois(int mois) {
        this.mois = mois;
    }

    /**
     * @return the annee
     */
    public int getAnnee() {
        return annee;
    }

    /**
     * @param annee the annee to set
     */
    public void setAnnee(int annee) {
        this.annee = annee;
    }

    /**
     * @return the resultatMoisPrecedent
     */
    public String getResultatMoisPrecedent() {
        return resultatMoisPrecedent;
    }

    /**
     * @param resultatMoisPrecedent the resultatMoisPrecedent to set
     */
    public void setResultatMoisPrecedent(String resultatMoisPrecedent) {
        this.resultatMoisPrecedent = resultatMoisPrecedent;
    }

    /**
     * @return the margeSecurite
     */
    public String getMargeSecurite() {
        return margeSecurite;
    }


    /**
     * @param margeSecurite the margeSecurite to set
     */
    public void setMargeSecurite(String margeSecurite) {
        this.margeSecurite = margeSecurite;
    }


    /**
     * @return the dateMiseAJour
     */
    public Date getDateMiseAJour() {
        return dateMiseAJour;
    }


    /**
     * @param dateMiseAJour the dateMiseAJour to set
     */
    public void setDateMiseAJour(Date dateMiseAJour) {
        this.dateMiseAJour = dateMiseAJour;
    }


    /**
     * @return the margeSecuriteFinMois
     */
    public String getMargeSecuriteFinMois() {
        return margeSecuriteFinMois;
    }


    /**
     * @param margeSecuriteFinMois the margeSecuriteFinMois to set
     */
    public void setMargeSecuriteFinMois(String margeSecuriteFinMois) {
        this.margeSecuriteFinMois = margeSecuriteFinMois;
    }


    /**
     * @return the compteBancaire
     */
    public CompteBancaire getCompteBancaire() {
        return compteBancaire;
    }


    /**
     * @param compteBancaire the compteBancaire to set
     */
    public void setCompteBancaire(CompteBancaire compteBancaire) {
        this.compteBancaire = compteBancaire;
    }


    public Map<String, String[]> getTotalParCategories() {
        return totalParCategories;
    }

    public void setTotalParCategories(Map<String, String[]> totalParCategories) {
        this.totalParCategories = totalParCategories;
    }

    public Map<String, String[]> getTotalParSSCategories() {
        return totalParSSCategories;
    }

    public void setTotalParSSCategories(Map<String, String[]> totalParSSCategories) {
        this.totalParSSCategories = totalParSSCategories;
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

    @Override
    public String toString() {
        return "BudgetMensuelDTO{" +
                "id='" + id + '\'' +
                ", mois=" + mois +
                ", annee=" + annee +
                ", actif=" + actif +
                ", dateMiseAJour=" + dateMiseAJour +
                ", compteBancaire=" + compteBancaire +
                ", resultatMoisPrecedent='" + resultatMoisPrecedent + '\'' +
                ", margeSecurite='" + margeSecurite + '\'' +
                ", margeSecuriteFinMois='" + margeSecuriteFinMois + '\'' +
                ", nowArgentAvance='" + nowArgentAvance + '\'' +
                ", nowCompteReel='" + nowCompteReel + '\'' +
                ", finArgentAvance='" + finArgentAvance + '\'' +
                ", finCompteReel='" + finCompteReel + '\'' +
                '}';
    }
}
