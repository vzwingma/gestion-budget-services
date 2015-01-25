package android.finances.terrier.com.budget.ihm.vue;

import android.finances.terrier.com.budget.R;
import android.finances.terrier.com.budget.abstrait.AbstractFragmentActivity;
import android.finances.terrier.com.budget.ihm.controleur.BudgetControleur;
import android.finances.terrier.com.budget.ihm.vue.budget.BudgetSectionsPagerAdapter;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import org.joda.time.DateMidnight;
import org.joda.time.DateTimeZone;
import org.joda.time.Months;

import java.util.Calendar;
import java.util.Date;

public class BudgetActivity extends AbstractFragmentActivity<BudgetControleur> {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private BudgetSectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    /**
     * @return l'id du Layout principal de l'activity
     */
    @Override
    public int getContentLayoutId() {
        return R.layout.activity_budget;
    }

    /**
     * Initialisation de l'IHM
     */
    @Override
    public void initIHM() {
        // Create the adapter that will return a fragment for each of the
        // primary sections of the activity.
        mSectionsPagerAdapter = new BudgetSectionsPagerAdapter(this, getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Affichage de la page initiale : la courante
        Date minDate = getControleur().getBusinessService().getContexte().getMapMinDateCompte().get(getControleur().getBusinessService().getContexte().getCompteCourant().getId());
        Date maxDate = Calendar.getInstance().getTime();
        DateMidnight minDateJoda = new DateMidnight(minDate, DateTimeZone.UTC);
        DateMidnight currentDateJoda = new DateMidnight(maxDate, DateTimeZone.UTC);
        mViewPager.setCurrentItem(Months.monthsBetween(minDateJoda, currentDateJoda).getMonths() + 1);
    }

    /**
     * Création du controleur de l'activity
     *
     * @return le controleur de l'activity
     */
    @Override
    public BudgetControleur createControleur() {
        return new BudgetControleur();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_budget, menu);
        return true;
    }
}
