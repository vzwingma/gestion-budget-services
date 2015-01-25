package android.finances.terrier.com.budget.ihm.vue.budget;

import android.finances.terrier.com.budget.ihm.vue.BudgetActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.joda.time.DateMidnight;
import org.joda.time.DateTimeZone;
import org.joda.time.Months;

import java.util.Calendar;
import java.util.Date;

/**
 * Adapter des pages
 * Created by vzwingma on 26/12/2014.
 */
public class BudgetSectionsPagerAdapter extends FragmentPagerAdapter {

    // Activité associée
    private final BudgetActivity activity;


    public BudgetSectionsPagerAdapter(BudgetActivity activity, FragmentManager fm) {
        super(fm);
        this.activity = activity;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Calendar c = Calendar.getInstance();
        Date minDate = this.activity.getControleur().getBusinessService().getContexte().getMapMinDateCompte().get(this.activity.getControleur().getBusinessService().getContexte().getCompteCourant().getId());
        c.setTime(minDate);
        c.add(Calendar.MONTH, position);

        return BudgetMoisFragment.newInstance(c.get(Calendar.MONTH), c.get(Calendar.YEAR));
    }

    @Override
    public int getCount() {
        Date minDate = this.activity.getControleur().getBusinessService().getContexte().getMapMinDateCompte().get(this.activity.getControleur().getBusinessService().getContexte().getCompteCourant().getId());
        Date maxDate = this.activity.getControleur().getBusinessService().getContexte().getMapMaxDateCompte().get(this.activity.getControleur().getBusinessService().getContexte().getCompteCourant().getId());
        DateMidnight minDateJoda = new DateMidnight(minDate, DateTimeZone.UTC);
        DateMidnight maxDateJoda = new DateMidnight(maxDate, DateTimeZone.UTC);
        // Show nombre de pages disponibles
        return Months.monthsBetween(minDateJoda, maxDateJoda).getMonths() + 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Position  : " + position;
    }
}