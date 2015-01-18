package android.finances.terrier.com.budget.ihm.vue.budget;

import android.app.Activity;
import android.finances.terrier.com.budget.R;
import android.finances.terrier.com.budget.models.BudgetMensuel;
import android.finances.terrier.com.budget.utils.Logger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

/**
 * Adapteur de la liste de résumé des totaux
 * Created by vzwingma on 03/01/2015.
 */
public class ResumeTotauxExpandableAdapter extends BaseExpandableListAdapter {

    private static final NumberFormat formatter = new DecimalFormat("#0.00");
    // Logger
    private final Logger LOG = new Logger(ResumeTotauxExpandableAdapter.class);
    private Activity activity;
    private LayoutInflater inflater;
    private BudgetMensuel budgetMensuel;

    /**
     * Constructeur
     *
     * @param budget
     * @param activity
     * @param inflater
     */
    public ResumeTotauxExpandableAdapter(BudgetMensuel budget, Activity activity, LayoutInflater inflater) {
        this.budgetMensuel = budget;
        this.activity = activity;
        this.inflater = inflater;
    }

    /**
     * Gets the number of groups.
     *
     * @return the number of groups
     */
    @Override
    public int getGroupCount() {
        return this.budgetMensuel.getTotalParCategories().size();
    }

    /**
     * Gets the number of children in a specified group.
     *
     * @param groupPosition the position of the group for which the children
     *                      count should be returned
     * @return the children count in the specified group
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return this.budgetMensuel.getTotalParSSCategories().size();
    }

    /**
     * Gets the data associated with the given group.
     *
     * @param groupPosition the position of the group
     * @return the data child for the specified group
     */
    @Override
    public Object getGroup(int groupPosition) {
        LOG.info("Affichage catégorie : " + groupPosition);
        return getElementFromPosition(this.budgetMensuel.getTotalParCategories(), groupPosition);
    }


    /**
     * @param mapData
     * @param position
     * @return
     */
    private Double[] getElementFromPosition(Map<String, Double[]> mapData, int position) {
        int i = 0;
        for (String key : mapData.keySet()) {
            if (i == position) {
                LOG.info("Affichage des valeurs de la clé : " + key);
                return mapData.get(key);
            }
            i++;
        }
        return null;
    }

    /**
     * Gets the data associated with the given child within the given group.
     *
     * @param groupPosition the position of the group that the child resides in
     * @param childPosition the position of the child with respect to other
     *                      children in the group
     * @return the data of the child
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    /**
     * Gets the ID for the group at the given position. This group ID must be
     * unique across groups. The combined ID (see
     * {@link #getCombinedGroupId(long)}) must be unique across ALL items
     * (groups and all children).
     *
     * @param groupPosition the position of the group for which the ID is wanted
     * @return the ID associated with the group
     */
    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    /**
     * Gets the ID for the given child within the given group. This ID must be
     * unique across all children within the group. The combined ID (see
     * {@link #getCombinedChildId(long, long)}) must be unique across ALL items
     * (groups and all children).
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child within the group for which
     *                      the ID is wanted
     * @return the ID associated with the child
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    /**
     * Indicates whether the child and group IDs are stable across changes to the
     * underlying data.
     *
     * @return whether or not the same ID always refers to the same object
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Gets a View that displays the given group. This View is only for the
     * group--the Views for the group's children will be fetched using
     * {@link #getChildView(int, int, boolean, android.view.View, android.view.ViewGroup)}.
     *
     * @param groupPosition the position of the group for which the View is
     *                      returned
     * @param isExpanded    whether the group is expanded or collapsed
     * @param convertView   the old view to reuse, if possible. You should check
     *                      that this view is non-null and of an appropriate type before
     *                      using. If it is not possible to convert this view to display
     *                      the correct data, this method can create a new view. It is not
     *                      guaranteed that the convertView will have been previously
     *                      created by
     *                      {@link #getGroupView(int, boolean, android.view.View, android.view.ViewGroup)}.
     * @param parent        the parent that this view will eventually be attached to
     * @return the View corresponding to the group at the specified position
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.resumecategorierow, null);
        }
        Double[] donnees = (Double[]) getGroup(groupPosition);
        ((CheckedTextView) convertView).setText(formatter.format(donnees[0]) + "€ ::" + formatter.format(donnees[1])+" €");
        ((CheckedTextView) convertView).setChecked(isExpanded);
        return convertView;
    }

    /**
     * Gets a View that displays the data for the given child within the given
     * group.
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child (for which the View is
     *                      returned) within the group
     * @param isLastChild   Whether the child is the last child within the group
     * @param convertView   the old view to reuse, if possible. You should check
     *                      that this view is non-null and of an appropriate type before
     *                      using. If it is not possible to convert this view to display
     *                      the correct data, this method can create a new view. It is not
     *                      guaranteed that the convertView will have been previously
     *                      created by
     *                      {@link #getChildView(int, int, boolean, android.view.View, android.view.ViewGroup)}.
     * @param parent        the parent that this view will eventually be attached to
     * @return the View corresponding to the child at the specified position
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        TextView textView = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.resumecategoriesgroup, null);
        }
        textView = (TextView) convertView.findViewById(R.id.textView1);
        LOG.info("Affichage sscatégorie : " + childPosition + " de " + groupPosition);
        Double[] donnees = getElementFromPosition(this.budgetMensuel.getTotalParSSCategories(), childPosition);
        textView.setText(formatter.format(donnees[0]) + "€ //" + formatter.format(donnees[1])+" €");
        return convertView;

    }

    /**
     * Whether the child at the specified position is selectable.
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child within the group
     * @return whether the child is selectable.
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
