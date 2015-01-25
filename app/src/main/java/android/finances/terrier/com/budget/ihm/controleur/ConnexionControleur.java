package android.finances.terrier.com.budget.ihm.controleur;

import android.finances.terrier.com.budget.R;
import android.finances.terrier.com.budget.abstrait.AbstractActivityControleur;
import android.finances.terrier.com.budget.ihm.vue.ConnexionActivity;
import android.finances.terrier.com.budget.ihm.vue.MainActivity;
import android.finances.terrier.com.budget.utils.IHMViewUtils;
import android.finances.terrier.com.budget.utils.Logger;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * Controleur de budget
 * Created by vzwingma on 26/12/2014.
 */
public class ConnexionControleur extends AbstractActivityControleur<ConnexionActivity> implements View.OnClickListener {


    // Logger
    private final Logger LOG = new Logger(ConnexionControleur.class);


    /**
     * Démarrage du controleur
     */
    @Override
    public void startControleur() {
        new AuthenticationHTTPAsyncTask(this).execute();
    }


    /**
     * Affichage du résultat de l'auth
     *
     * @param resultat résultat
     * @param message  message à affiché
     */
    public void getResultatAuth(String message, boolean resultat) {
        ((TextView) getActivity().findViewById(R.id.textViewResultat)).setText(message);
        (getActivity().findViewById(R.id.textViewResultat)).setVisibility(View.VISIBLE);
        ((TextView) getActivity().findViewById(R.id.textViewResultat)).setTextColor(!resultat ? IHMViewUtils.COLOR_VALEUR_NEGATIF : IHMViewUtils.COLOR_VALEUR_POSITIF);
        (getActivity().findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);
        if (!resultat) {
            (getActivity().findViewById(R.id.buttonReconnexion)).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.buttonReconnexion).setOnClickListener(this);
        }
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
    public boolean onMenuItemSelected(MenuItem item) {
        return false;
    }

    /**
     * Bouton reconnexion --> Retour au départ
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        getActivity().finish();
        startActivity(MainActivity.class);
    }
}
