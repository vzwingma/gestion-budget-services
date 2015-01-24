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


    public static final int ROW_COLOR_ODD = Color.parseColor("#f5f5f5");
    public static final int ROW_COLOR = Color.parseColor("#FFFFFF");
    private static final int COLOR_VALEUR_POSITIF = Color.parseColor("#2c9720");
    private static final int COLOR_VALEUR_NEGATIF = Color.parseColor("#FF0000");
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
        int color = valeur >= 0 ? IHMViewUtils.COLOR_VALEUR_POSITIF : IHMViewUtils.COLOR_VALEUR_NEGATIF;
        ((TextView) rootView.findViewById(id)).setText(formatter.format(valeur) + " €");
        ((TextView) rootView.findViewById(id)).setTextColor(color);
    }
}
