package android.finances.terrier.com.budget.services;

import android.app.Activity;
import android.content.SharedPreferences;
import android.finances.terrier.com.budget.abstrait.AbstractService;
import android.finances.terrier.com.budget.utils.AuthenticationPreferencesEnums;
import android.finances.terrier.com.budget.utils.Logger;

/**
 * Services métier
 * Created by vzwingma on 02/01/2015.
 */
public class PersistanceService extends AbstractService {


    // Logger
    private static final Logger LOG = new Logger(PersistanceService.class);

    private static final String PREFS_NAME = "BudgetPrefsFile";
    private static final String REST_BASIC_AUTH_LOGIN = "android";
    private static final String REST_BASIC_AUTH_PWD = "budgetAndroid";

    /**
     * Création du service
     */
    @Override
    public void onCreate() {
    }


    /**
     * @param activite
     * @param key      clé
     * @return préférence
     */
    public String getPreference(Activity activite, AuthenticationPreferencesEnums key) {

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = activite.getSharedPreferences(PREFS_NAME, 0);
        String valeur = settings.getString(key.name(), null);
        LOG.info("[PREF] Chargement de [" + key + "] = [" + valeur + "]");
        if (valeur == null) {
            if (key.equals(AuthenticationPreferencesEnums.REST_BASIC_AUTH_LOGIN)) {
                savePreference(activite, AuthenticationPreferencesEnums.REST_BASIC_AUTH_LOGIN, REST_BASIC_AUTH_LOGIN);
                return REST_BASIC_AUTH_LOGIN;
            }
            if (key.equals(AuthenticationPreferencesEnums.REST_BASIC_AUTH_PWD)) {
                savePreference(activite, AuthenticationPreferencesEnums.REST_BASIC_AUTH_PWD, REST_BASIC_AUTH_PWD);
                return REST_BASIC_AUTH_PWD;
            }
        }
        return valeur;
    }


    /**
     * Sauvegarde préférence
     *
     * @param activite activité
     * @param key      clé
     * @param valeur   valeur
     */
    public boolean savePreference(Activity activite, AuthenticationPreferencesEnums key, String valeur) {

        if (activite == null) {
            return false;
        }
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = activite.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        LOG.info("[PREF] Sauvegarde de [" + key + "] = [" + valeur + "]");
        editor.putString(key.name(), valeur);
        // Commit the edits!
        return editor.commit();
    }

    /**
     * Arrêt du service
     */
    @Override
    public boolean onDestroy() {
        return true;
    }
}
