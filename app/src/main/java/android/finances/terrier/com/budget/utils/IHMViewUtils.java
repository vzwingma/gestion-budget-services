package android.finances.terrier.com.budget.utils;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Classe utilitaire des views
 * Created by vzwingma on 18/01/2015.
 */
public class IHMViewUtils {


    public static final String VALEUR_POSITIF = "#2c9720";
    public static final String VALEUR_NEGATIF = "#FF0000";
    private static final NumberFormat formatter = new DecimalFormat("#0.00");

    private IHMViewUtils() {
    }

    /**
     * Mise à jour d'un textview avec une valeur en €
     *
     * @param id     id de l'élément
     * @param valeur valeur à afficher
     */
    public static void miseAJourTextViewValeurEuro(View rootView, int id, double valeur) {
        int color = Color.parseColor(valeur >= 0 ? IHMViewUtils.VALEUR_POSITIF : IHMViewUtils.VALEUR_NEGATIF);
        ((TextView) rootView.findViewById(id)).setText(formatter.format(valeur) + " €");
        ((TextView) rootView.findViewById(id)).setTextColor(color);
    }
}
