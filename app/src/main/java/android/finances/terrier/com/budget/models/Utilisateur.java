/**
 *
 */
package android.finances.terrier.com.budget.models;

import java.io.Serializable;
import java.util.Map;

/**
 * Définition d'un utilisateur
 *
 * @author vzwingma
 */
public class Utilisateur implements Serializable {


    public static final String PREFERENCE_TABLE_ODD_STYLE = "PREFERENCE_TABLE_ODD_STYLE";
    /**
     *
     */
    private static final long serialVersionUID = 5912920498104708791L;
    private String id;
    // Login
    private String login;
    // Libellé
    private String libelle;
    private String hashMotDePasse;
    /**
     * Préférences
     */
    private Map<String, Object> preferences;

    /**
     * @return the login
     */
    public String getLogin() {
        return login;
    }

    /**
     * @param login the login to set
     */
    public void setLogin(String login) {
        this.login = login;
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
     * @return the preferences
     */
    public Map<String, Object> getPreferences() {
        return preferences;
    }

    /**
     * @param preferences the preferences to set
     */
    public void setPreferences(Map<String, Object> preferences) {
        this.preferences = preferences;
    }

    public String getHashMotDePasse() {
        return hashMotDePasse;
    }


    public void setHashMotDePasse(String hashMotDePasse) {
        this.hashMotDePasse = hashMotDePasse;
    }

    /**
     * @return the preferences
     */
    @SuppressWarnings("unchecked")
    public <T> T getPreference(String clePreference, Class<T> typeAttendu) {
        return (T) preferences.get(clePreference);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
    @Override
    public String toString() {
        return "Utilisateur [id=" + id + ", login=" + login + ", libelle="
                + libelle + "]";
    }
}
