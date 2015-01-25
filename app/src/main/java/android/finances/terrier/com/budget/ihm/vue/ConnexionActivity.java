package android.finances.terrier.com.budget.ihm.vue;

import android.finances.terrier.com.budget.R;
import android.finances.terrier.com.budget.abstrait.AbstractActivity;
import android.finances.terrier.com.budget.ihm.controleur.ConnexionControleur;

public class ConnexionActivity extends AbstractActivity<ConnexionControleur> {


    /**
     * @return l'id du Layout principal de l'activity
     */
    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_connexion;
    }


    /**
     * Initialisation de l'IHM
     */
    @Override
    protected void initIHM() {

    }

    /**
     * Création du controleur de l'activity
     *
     * @return le controleur de l'activity
     */
    @Override
    protected ConnexionControleur createControleur() {
        return new ConnexionControleur();
    }

}
