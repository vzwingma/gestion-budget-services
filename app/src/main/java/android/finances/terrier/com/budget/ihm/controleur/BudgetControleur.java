package android.finances.terrier.com.budget.ihm.controleur;

import android.finances.terrier.com.budget.R;
import android.finances.terrier.com.budget.abstrait.AbstractActivityControleur;
import android.finances.terrier.com.budget.ihm.vue.BudgetActivity;
import android.finances.terrier.com.budget.utils.Logger;
import android.view.MenuItem;

/**
 * Controleur de budget
 * Created by vzwingma on 26/12/2014.
 */
public class BudgetControleur extends AbstractActivityControleur<BudgetActivity> {


    // Logger
    private final Logger LOG = new Logger(BudgetControleur.class);


    /**
     * Démarrage du controleur
     */
    @Override
    public void startControleur() {

    }


    /**
     * Arrét du controleur
     */
    @Override
    public void stopControleur() {

    }

    /**
     * Sélection du menu
     *
     * @param item menu
     * @return résultat de la sélection
     */
    @Override
    public boolean onMenuItemSelected(MenuItem item) {
        LOG.info("onMenuItemSelected >>> " + item.getItemId() + "::" + R.id.action_budget_quitter);
        switch (item.getItemId()) {
            // S'il est égal a itemQuitter
            case R.id.action_budget_quitter:
                // On ferme l'activité
                stopGlobalApplication();
                return true;
            default:
                return false;
        }
    }
}
