package android.finances.terrier.com.budget.models.data;

import android.finances.terrier.com.budget.models.CompteBancaire;
import android.finances.terrier.com.budget.models.Utilisateur;
import android.finances.terrier.com.budget.utils.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.jasypt.util.text.BasicTextEncryptor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contexte d'un utilisateur
 *
 * @author vzwingma
 */
public class ContexteUtilisateurDTO {


    // Logger
    private static final Logger LOG = new Logger(ContexteUtilisateurDTO.class);
    /**
     * Utilisateur
     */
    private Utilisateur utilisateur;
    /**
     * Comptes
     */
    private List<CompteBancaire> comptes;
    /**
     *
     */
    private Map<String, Date> mapMinDateCompte = new HashMap<>();
    private Map<String, Date> mapMaxDateCompte = new HashMap<>();
    @JsonIgnore
    private BasicTextEncryptor encryptor;
    @JsonIgnore
    private CompteBancaire compteCourant;

    /**
     * @return the utilisateur
     */
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    /**
     * @param utilisateur the utilisateur to set
     */
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    /**
     * @return the comptes
     */
    public List<CompteBancaire> getComptes() {
        return comptes;
    }

    /**
     * @param comptes the comptes to set
     */
    public void setComptes(List<CompteBancaire> comptes) {
        this.comptes = comptes;
        if (this.comptes != null) {
            for (CompteBancaire compte : comptes) {
                if (compte.isDefaut()) {
                    this.compteCourant = compte;
                    break;
                }
            }
        }
    }

    /**
     * @return the mapMinDateCompte
     */
    public Map<String, Date> getMapMinDateCompte() {
        return mapMinDateCompte;
    }

    /**
     * @param mapMinDateCompte the mapMinDateCompte to set
     */
    public void setMapMinDateCompte(Map<String, Date> mapMinDateCompte) {
        this.mapMinDateCompte = mapMinDateCompte;
    }

    /**
     * @return the mapMaxDateCompte
     */
    public Map<String, Date> getMapMaxDateCompte() {
        return mapMaxDateCompte;
    }

    /**
     * @param mapMaxDateCompte the mapMaxDateCompte to set
     */
    public void setMapMaxDateCompte(Map<String, Date> mapMaxDateCompte) {
        this.mapMaxDateCompte = mapMaxDateCompte;
    }

    /**
     * Init encryptor
     *
     * @param motPasse mot de passe à utiliser
     */
    public void initEncryptor(String motPasse) {
        this.encryptor = new BasicTextEncryptor();
        this.encryptor.setPassword(motPasse);
    }

    @JsonIgnore
    public BasicTextEncryptor getEncryptor() {
        return this.encryptor;
    }

    public CompteBancaire getCompteCourant() {
        return compteCourant;
    }
}

