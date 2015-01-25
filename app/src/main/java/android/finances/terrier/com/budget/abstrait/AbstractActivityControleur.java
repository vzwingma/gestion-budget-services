package android.finances.terrier.com.budget.abstrait;

import android.app.Activity;
import android.content.Intent;
import android.finances.terrier.com.budget.services.BusinessService;
import android.finances.terrier.com.budget.services.FacadeServices;
import android.finances.terrier.com.budget.services.PersistanceService;
import android.finances.terrier.com.budget.utils.Logger;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Classe abstraite d'un controleur d'une activité
 *
 * @param <BudgetActivity> activité associée au controleur
 * @author vzwingma
 */
public abstract class AbstractActivityControleur<BudgetActivity extends Activity> {

    // Logger
    private final Logger LOG = new Logger(AbstractActivityControleur.class);

    /**
     * Activity liée au controleur
     */
    private BudgetActivity activity;


    /**
     * Démarrage du controleur parent puis le controleur spécifique
     */
    protected void startParentControleur() {
        LOG.trace("[IHM] Démarrage du controleur " + this.getClass().getName());
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
        LOG.trace("[IHM] Arrét du controleur " + this.getClass().getName());
        stopControleur();
    }

    /**
     * Arrét du controleur
     */
    protected abstract void stopControleur();


    /**
     * Arrét de l'application
     * Appelé sur Destroy du Main
     */
    protected void stopGlobalApplication() {
        LOG.info("[IHM] ********* Arrét de l'application *********");
        FacadeServices.stopAndroidServices();
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    /**
     * @return businessService
     */
    public BusinessService getBusinessService() {
        return FacadeServices.getInstance().getBusinessService();
    }


    /**
     * @return getPersistanceService
     */
    public PersistanceService getPersistanceService() {
        return FacadeServices.getInstance().getPersistanceService();
    }

    /**
     * Ajout d'une notification
     */
    public void showPopupNotification(final String message, final int delai) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getActivity(), message, delai).show();
            }
        });
    }


    /**
     * Démarrage d'une activite
     *
     * @param activityClass classe de l'activité
     */
    @SuppressWarnings("rawtypes")
    public void startActivity(Class<? extends Activity> activityClass) {
        LOG.debug("[IHM] Start activity " + activityClass.getName() + " depuis " + getActivity().getClass().getName());
        Intent activiyIntent = new Intent(getActivity(), activityClass);
        getActivity().startActivity(activiyIntent);
    }


    /**
     * Sélection du menu
     *
     * @param item menu
     * @return résultat de la sélection
     */
    public abstract boolean onMenuItemSelected(MenuItem item);

    /**
     * Recherche d'un élément à partir de son id
     *
     * @param idElement idElement
     * @return view correspondante
     */
    public TextView getElementById(int idElement) {
        return (TextView) getActivity().findViewById(idElement);
    }
    /**
     * @return facade des services

    public FacadeServices getFacadeServices(){
    return FacadeServices.getInstance();
    }*/

    /**
     * @return l'activité
     */
    public BudgetActivity getActivity() {
        return activity;
    }

    public void setActivity(BudgetActivity activity) {
        this.activity = activity;
    }
}
