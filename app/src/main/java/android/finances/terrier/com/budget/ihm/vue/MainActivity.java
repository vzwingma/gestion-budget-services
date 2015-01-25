package android.finances.terrier.com.budget.ihm.vue;

import android.content.Intent;
import android.finances.terrier.com.budget.R;
import android.finances.terrier.com.budget.abstrait.AbstractActivity;
import android.finances.terrier.com.budget.ihm.controleur.MainControleur;
import android.finances.terrier.com.budget.utils.Logger;
import android.view.Menu;


/**
 * Activité principale
 */
public class MainActivity extends AbstractActivity<MainControleur> {


    // Logger
    private final Logger LOG = new Logger(MainActivity.class);

    /**
     * Init IHM
     */
    @Override
    public void initIHM() {
        // Création du bouton de connexion
        (findViewById(R.id.buttonConnect)).setOnClickListener(getControleur());
        (findViewById(R.id.buttonConnectPattern)).setOnClickListener(getControleur());
        (findViewById(R.id.buttonSaveCompte)).setOnClickListener(getControleur());
    }


    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getControleur().lockPatternResultat(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /**
     * Création du controleur
     */
    @Override
    public MainControleur createControleur() {
        return new MainControleur();
    }


    /* (non-Javadoc)
 * @see com.steria.iv.proto.abstrait.AbstractActivity#getContentLayoutId()
 */
    @Override
    public int getContentLayoutId() {
        return R.layout.activity_main;
    }
}
