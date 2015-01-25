package android.finances.terrier.com.budget.ihm.controleur;

import android.content.Intent;
import android.finances.terrier.com.budget.R;
import android.finances.terrier.com.budget.abstrait.AbstractActivityControleur;
import android.finances.terrier.com.budget.ihm.vue.MainActivity;
import android.finances.terrier.com.budget.lockpattern.LockPatternActivity;
import android.finances.terrier.com.budget.services.FacadeServices;
import android.finances.terrier.com.budget.utils.AuthenticationConstants;
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
     * Called when buttonConnect has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        // Login direct
        if (v.getId() == R.id.buttonConnect) {
            String login = getElementById(R.id.LoginForm).getText().toString();
            String motPasse = getElementById(R.id.PwdForm).getText().toString();
            getService().setServeurCredential(login, motPasse);
            connectUserToServeur();
        }
        // Connexion à partir du pattern enregistré
        else if (v.getId() == R.id.buttonConnectPattern) {
            LOG.info("Connection via un lock pattern");
            Intent intent = new Intent(LockPatternActivity.ACTION_COMPARE_PATTERN, null,
                    getActivity().getApplicationContext(), LockPatternActivity.class);
            intent.putExtra(LockPatternActivity.EXTRA_PATTERN, AuthenticationConstants.pattern.toCharArray());
            getActivity().startActivityForResult(intent, AuthenticationConstants.REQ_ENTER_PATTERN);
        }
        // Enregistrement d'un pattern
        else if (v.getId() == R.id.buttonSaveCompte) {
            LOG.info("Création d'une identité Android");
            Intent intent = new Intent(LockPatternActivity.ACTION_CREATE_PATTERN, null,
                    getActivity().getApplicationContext(), LockPatternActivity.class);
            getActivity().startActivityForResult(intent, AuthenticationConstants.REQ_CREATE_PATTERN);
        }
    }


    /**
     * Résultat du lockpattern (création ou validation)
     *
     * @param requestCode code mode lockpattern (création/modification)
     * @param resultCode  résultat
     * @param data data du lockpattern
     */
    public void lockPatternResultat(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AuthenticationConstants.REQ_CREATE_PATTERN: {
                if (resultCode == LockPatternActivity.RESULT_OK) {
                    char[] pattern = data.getCharArrayExtra(
                            LockPatternActivity.EXTRA_PATTERN);
                    String login = getElementById(R.id.LoginForm).getText().toString();
                    String motPasse = getElementById(R.id.PwdForm).getText().toString();
                    FacadeServices.getInstance().getBusinessService().createAndroidId(login, motPasse, pattern);
                    connectUserToServeur();
                }
                break;
            }// REQ_CREATE_PATTERN
            case AuthenticationConstants.REQ_ENTER_PATTERN: {
                /*
                 * NOTE that there are 4 possible result codes!!!
                 */
                switch (resultCode) {
                    case LockPatternActivity.RESULT_OK:
                        LOG.info("** Résultat LockPattern : OK");
                        char[] pattern = data.getCharArrayExtra(
                                LockPatternActivity.EXTRA_PATTERN);
                        getService().authenticateToMobile(pattern);
                        connectUserToServeur();
                        break;
                    case LockPatternActivity.RESULT_CANCELED:
                        LOG.info("** Résultat LockPattern : Cancel");
                        break;
                    case LockPatternActivity.RESULT_FAILED:
                        LOG.info("** Résultat LockPattern : Echec");
                        break;
                    case LockPatternActivity.RESULT_FORGOT_PATTERN:
                        LOG.info("** Résultat LockPattern : Forgot");
                        break;
                }
                /*
                 * In any case, there's always a key EXTRA_RETRY_COUNT, which holds
                 * the number of tries that the user did.
                 * int retryCount = data.getIntExtra(
                        LockPatternActivity.EXTRA_RETRY_COUNT, 0);
                 */
                break;
            }// REQ_ENTER_PATTERN
        }
    }


    /**
     * Connection d'un utilisateur
     */
    private void connectUserToServeur() {
        getActivity().findViewById(R.id.progressBarConnexion).setVisibility(View.VISIBLE);
        new AuthenticationHTTPAsyncTask(this).execute();
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
     * Arrét du controleur
     */
    @Override
    public void stopControleur() {
    }
}
