package android.finances.terrier.com.budget.ihm.vue;

import android.finances.terrier.com.budget.R;
import android.finances.terrier.com.budget.abstrait.AbstractActivity;
import android.finances.terrier.com.budget.ihm.controleur.ConnexionControleur;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ConnexionActivity extends AbstractActivity<ConnexionControleur> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
    }

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_connexion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
