package android.finances.terrier.com.budget.ihm.controleur;

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
     * Menu Item select
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        switch (item.getItemId()) {
            default:
                LOG.info("onMenuItemSelected : " + item.getItemId());
                return super.onMenuItemSelected(featureId, item);
        }
    }
}
