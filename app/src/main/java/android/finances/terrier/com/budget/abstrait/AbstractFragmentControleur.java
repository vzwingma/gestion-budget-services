package android.finances.terrier.com.budget.abstrait;

import android.finances.terrier.com.budget.ihm.vue.budget.BudgetMoisFragment;
import android.finances.terrier.com.budget.services.BusinessService;
import android.finances.terrier.com.budget.services.FacadeServices;
import android.finances.terrier.com.budget.utils.Logger;
import android.view.MenuItem;
import android.view.View;


/**
 * Classe abstraite d'un controleur d'une activité
 *
 * @param <BudgetFragment> fragment associée au controleur
 * @author vzwingma
 */
public abstract class AbstractFragmentControleur<BudgetFragment extends BudgetMoisFragment> {

    // Logger
    private final Logger LOG = new Logger(AbstractFragmentControleur.class);

    /**
     * Fragment liée au controleur
     */
    private BudgetFragment fragment;


    /**
     * Démarrage du controleur parent puis le controleur spécifique
     */
    protected void startParentControleur() {
        LOG.trace("Démarrage du controleur " + this.getClass().getName());
        startControleur();
    }

    /**
     * Démarrage du controleur
     */
    protected abstract void startControleur();

    /**
     * Arrét du controleur
     */
    protected void stopParentControleur() {
        LOG.trace("Arrét du controleur " + this.getClass().getName());
        stopControleur();
    }

    /**
     * Arrét du controleur
     */
    protected abstract void stopControleur();

    /**
     * @return service métier
     */
    public BusinessService getBusinessService() {
        return FacadeServices.getInstance().getBusinessService();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
    protected boolean onMenuItemSelected(int featureId, MenuItem item) {
        LOG.warn("On MenuSelected : A Surcharger");
        return true;
    }

    public BudgetFragment getFragment() {
        return fragment;
    }

    public void setFragment(BudgetFragment fragment) {
        this.fragment = fragment;
    }

    /**
     * Recherche d'un élément à partir de son id
     *
     * @param idElement idElement
     * @return view correspondante
     */
    public View getElementFromView(int idElement) {
        return getFragment().getRootView().findViewById(idElement);
    }
}
