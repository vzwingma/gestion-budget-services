package android.finances.terrier.com.budget.ihm.vue;

import android.finances.terrier.com.budget.R;
import android.finances.terrier.com.budget.abstrait.AbstractActivity;
import android.finances.terrier.com.budget.ihm.controleur.MainControleur;
import android.finances.terrier.com.budget.utils.Logger;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;


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
        ((Button) findViewById(R.id.buttonOpenshift)).setOnClickListener(getControleur());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        LOG.info("onOptionsItemSelected : " + id + " == " + R.id.action_main_quitter);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_main_quitter) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
