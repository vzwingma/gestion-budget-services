package android.finances.terrier.com.budget.ihm.controleur;

import android.finances.terrier.com.budget.R;
import android.finances.terrier.com.budget.abstrait.AbstractActivityControleur;
import android.finances.terrier.com.budget.abstrait.AbstractRESTService;
import android.finances.terrier.com.budget.ihm.vue.MainActivity;
import android.finances.terrier.com.budget.services.FacadeServices;
import android.finances.terrier.com.budget.utils.Logger;
import android.view.MenuItem;
import android.view.View;


/**
 * Controleur de l'écran Principal
 *
 * @author vzwingma
 */
public class MainControleur extends AbstractActivityControleur<MainActivity> implements View.OnClickListener {


    // Logger
    private final Logger LOG = new Logger(MainControleur.class);

    /**
     * Initialisation du controleur
     */
    public void startControleur() {
        // Démarrage des services
        FacadeServices.initServices();
    }


    /**
     * Menu Item select
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        switch (item.getItemId()) {
            // S'il est égal a itemQuitter
            case R.id.action_main_quitter:
                // On ferme l'activité
                stopGlobalApplication();
                return true;
            default:
                LOG.info("onMenuItemSelected : " + item.getItemId());
                return super.onMenuItemSelected(featureId, item);
        }
    }


    /**
     * Called when buttonConnect has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.buttonLocalhost) {
            AbstractRESTService.IP_SERVEUR = AbstractRESTService.IP_SERVEUR_LOCALHOST;
        } else if (v.getId() == R.id.buttonOpenshift) {
            AbstractRESTService.IP_SERVEUR = AbstractRESTService.IP_SERVEUR_OPENSHIFT;
        }

        new AuthenticationHTTPAsyncTask(this).execute("", "");
    }


    /**
     * Arrét du controleur
     */
    @Override
    public void stopControleur() {
    }

}
