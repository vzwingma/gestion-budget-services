package android.finances.terrier.com.budget.abstrait;

import android.finances.terrier.com.budget.utils.Logger;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Classe abstraite d'une activité Android
 *
 * @param <BudgetActivityControleur> controleur associé é l'activité
 * @author vzwingma
 */
public abstract class AbstractActivity<BudgetActivityControleur extends AbstractActivityControleur> extends ActionBarActivity {

    // Logger
    private final Logger LOG = new Logger(AbstractActivity.class);

    /**
     * Controleur
     */
    private BudgetActivityControleur controleur;

    /**
     * Création de l'activity
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LOG.trace("Création de l'activity : " + this.getClass().getName());
        super.onCreate(savedInstanceState);
        setContentView(getContentLayoutId());
        this.controleur = createControleur();
        LOG.trace("Création du controleur de l'activity : " + this.controleur.getClass().getName());
        this.controleur.setActivity(this);
        this.controleur.startParentControleur();
    }


    /**
     * @return l'id du Layout principal de l'activity
     */
    protected abstract int getContentLayoutId();


    /* (non-Javadoc)
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
        LOG.trace("Démarrage de l'activity : " + this.getClass().getName());
        super.onStart();
        initIHM();
    }


    /**
     * Arrét de l'activity
     *
     * @see android.app.Activity#onStop()
     */
    @Override
    protected void onStop() {
        LOG.trace("Arrêt de l'activity : " + this.getClass().getName());
        getControleur().stopParentControleur();
        super.onStop();
    }


    /**
     * Initialisation de l'IHM
     */
    protected abstract void initIHM();

    /**
     * Création du controleur de l'activity
     *
     * @return le controleur de l'activity
     */
    protected abstract BudgetActivityControleur createControleur();

    /**
     * @return controleur de l'activity
     */
    public BudgetActivityControleur getControleur() {
        return this.controleur;
    }
}
