package android.finances.terrier.com.budget.ihm.listener;

import android.finances.terrier.com.budget.ihm.controleur.BudgetFragmentControleur;
import android.finances.terrier.com.budget.models.CompteBancaire;
import android.finances.terrier.com.budget.utils.Logger;
import android.view.View;
import android.widget.AdapterView;

/**
 * Sélection d'un compte
 * Created by vzwingma on 24/01/2015.
 */
public class ListeCompteSelectedListener implements AdapterView.OnItemSelectedListener {


    // Logger
    private final Logger LOG = new Logger(ListeCompteSelectedListener.class);
    private BudgetFragmentControleur controleur;

    public ListeCompteSelectedListener(BudgetFragmentControleur controleur) {
        this.controleur = controleur;
    }


    /**
     * <p>Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.</p>
     * <p/>
     * Impelmenters can call getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        CompteBancaire compteBancaire = this.controleur.getBusinessService().getContexte().getComptes().get(position);
        if (this.controleur.getIdCompte() == null || !this.controleur.getIdCompte().equals(compteBancaire.getId())) {
            LOG.info("Selection COMPTE : " + id + "->" + compteBancaire.toFullString());
            this.controleur.setIdCompte(compteBancaire.getId());
            this.controleur.miseAJourVue();
        } else {
            LOG.debug("même COMPTE");
        }

    }

    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        LOG.info("onNothingSelected COMPTE : " + parent);
    }
}
