package android.finances.terrier.com.budget.models;

import android.finances.terrier.com.budget.utils.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Budget du mois
 *
 * @author vzwingma
 */
public class BudgetMensuel implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = 4393433203514049021L;

    // Logger
    private static final Logger LOGGER = new Logger(BudgetMensuel.class);

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
    private Calendar dateMiseAJour;
    /**
     * Compte bancaire
     */
    private CompteBancaire compteBancaire;
    /**
     * Résultat du mois précédent
     */
    private Double resultatMoisPrecedent;

    private Double margeSecurite;
    private Double margeSecuriteFinMois;

    /**
     * Liste des dépenses
     */
    private List<LigneDepense> listeDepenses = new ArrayList<>();

    private Map<String, Double[]> totalParCategories = new HashMap<>();
    private Map<String, Double[]> totalParSSCategories = new HashMap<>();

    /**
     * Totaux
     */
    private double nowArgentAvance;
    private double nowCompteReel;
    private double finArgentAvance;
    private double finCompteReel;


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
     * @return the totalParCategories
     */
    public Map<String, Double[]> getTotalParCategories() {
        return totalParCategories;
    }

    public void setTotalParCategories(Map<String, Double[]> totalParCategories) {
        this.totalParCategories = totalParCategories;
    }

    /**
     * @return the totalParSSCategories
     */
    public Map<String, Double[]> getTotalParSSCategories() {
        return totalParSSCategories;
    }

    public void setTotalParSSCategories(Map<String, Double[]> totalParSSCategories) {
        this.totalParSSCategories = totalParSSCategories;
    }

    /**
     * @return the nowArgentAvance
     */
    public double getNowArgentAvance() {
        return nowArgentAvance;
    }

    public void setNowArgentAvance(double nowArgentAvance) {
        this.nowArgentAvance = nowArgentAvance;
    }

    /**
     * @return the nowCompteReel
     */
    public double getNowCompteReel() {
        return nowCompteReel;
    }

    public void setNowCompteReel(double nowCompteReel) {
        this.nowCompteReel = nowCompteReel;
    }

    /**
     * @return the finArgentAvance
     */
    public double getFinArgentAvance() {
        return finArgentAvance;
    }

    public void setFinArgentAvance(double finArgentAvance) {
        this.finArgentAvance = finArgentAvance;
    }

    /**
     * @return the finCompteReel
     */
    public double getFinCompteReel() {
        return finCompteReel;
    }

    public void setFinCompteReel(double finCompteReel) {
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
     * /**
     *
     * @return the resultatMoisPrecedent
     */
    public Double getResultatMoisPrecedent() {
        return resultatMoisPrecedent;
    }

    public void setResultatMoisPrecedent(Double resultatMoisPrecedent) {
        this.resultatMoisPrecedent = resultatMoisPrecedent;
    }

    /**
     * @return the listeDepenses
     */
    public List<LigneDepense> getListeDepenses() {
        return listeDepenses;
    }

    /**
     * @param listeDepenses the listeDepenses to set
     */
    public void setListeDepenses(List<LigneDepense> listeDepenses) {
        this.listeDepenses = listeDepenses;
    }


    /**
     * @return the margeSecurite
     */
    public Double getMargeSecurite() {
        return margeSecurite;
    }


    /**
     * @param margeSecurite the margeSecurite to set
     */
    public void setMargeSecurite(Double margeSecurite) {
        this.margeSecurite = margeSecurite;
    }


    /**
     * @return the dateMiseAJour
     */
    public Calendar getDateMiseAJour() {
        return dateMiseAJour;
    }


    /**
     * @param dateMiseAJour the dateMiseAJour to set
     */
    public void setDateMiseAJour(Calendar dateMiseAJour) {
        this.dateMiseAJour = dateMiseAJour;
    }


    /**
     * @return the margeSecuriteFinMois
     */
    public Double getMargeSecuriteFinMois() {
        return margeSecuriteFinMois;
    }


    /**
     * @param margeSecuriteFinMois the margeSecuriteFinMois to set
     */
    public void setMargeSecuriteFinMois(Double margeSecuriteFinMois) {
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
        return "BudgetMensuel{" +
                "id='" + id + '\'' +
                ", mois=" + mois +
                ", annee=" + annee +
                ", actif=" + actif +
                ", dateMiseAJour=" + dateMiseAJour +
                ", compteBancaire=" + compteBancaire +
                ", resultatMoisPrecedent=" + resultatMoisPrecedent +
                ", margeSecurite=" + margeSecurite +
                ", margeSecuriteFinMois=" + margeSecuriteFinMois +
                ", listeDepenses=" + listeDepenses +
                ", totalParCategories=" + totalParCategories +
                ", totalParSSCategories=" + totalParSSCategories +
                ", nowArgentAvance=" + nowArgentAvance +
                ", nowCompteReel=" + nowCompteReel +
                ", finArgentAvance=" + finArgentAvance +
                ", finCompteReel=" + finCompteReel +
                '}';
    }
}
