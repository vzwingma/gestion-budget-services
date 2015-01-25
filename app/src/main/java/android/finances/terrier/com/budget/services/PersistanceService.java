package android.finances.terrier.com.budget.services;

import android.app.Application;
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


    /**
     * Création du service
     */
    @Override
    public void onCreate() {
    }


    /**
     * @param application application
     * @param key      clé
     * @return préférence
     */
    public String getPreference(Application application, AuthenticationPreferencesEnums key) {

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = application.getSharedPreferences(PREFS_NAME, 0);
        String valeur = settings.getString(key.name(), null);
        LOG.info("[PREF] Chargement de [" + key + "] = [" + valeur + "]");
        return valeur;
    }


    /**
     * Sauvegarde préférence
     *
     * @param application application
     * @param key      clé
     * @param valeur   valeur
     */
    public boolean savePreference(Application application, AuthenticationPreferencesEnums key, String valeur) {

        if (application == null) {
            return false;
        }
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = application.getSharedPreferences(PREFS_NAME, 0);
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
