package com.example.comiccollection.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import android.util.Log;

import com.example.comiccollection.data.entities.Copy;
import com.example.comiccollection.R;
import com.example.comiccollection.data.entities.CopyType;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CopiesAdapter extends BaseExpandableListAdapter {

    /*
    We need a Context for the view layout inflater.
     */
    private final Context context;

    /*
    This is the set of names for each category (group).
     */
    private final List<String> copyCategoryNamesList;

    /*
    This is the set of Copy objects, for each group, that carry the data for each copy (child).
     */
    private final Map<String, List<Copy>> copiesMap;

    private final String TAG = CopiesAdapter.class.getSimpleName();

    public CopiesAdapter(Context context, List<String> copyCategoryNamesList,
                         Map<String, List<Copy>> copiesMap) {
        this.context = context;
        this.copyCategoryNamesList = copyCategoryNamesList;
        this.copiesMap = copiesMap;
    }

    @Override
    public int getGroupCount() {
        return copyCategoryNamesList.size();
    }

    /*
    Returns the count of children (copies) given a group (category) descriptor.
     */
    @Override
    public int getChildrenCount(int i) {
        try {
            String copyCategory = copyCategoryNamesList.get(i);
            return Objects.requireNonNull(copiesMap.get(copyCategory)).size();

        } catch( IndexOutOfBoundsException e) {
            return 0;
        }
    }

    @Override
    public Object getGroup(int i) {
        return copyCategoryNamesList.get(i);
    }

    /*
    'i' chooses a category, 'i1' chooses a Copy within that category.
     */
    @Override
    public Object getChild(int i, int i1) {
        try {
            String copyCategory = copyCategoryNamesList.get(i);
            List<Copy> copyList =  Objects.requireNonNull(copiesMap.get(copyCategory));

            return copyList.get(i1);

        } catch( IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        /*
        This view will always be the view we get from inflating copy_group_layout.
         */
        View groupView = convertView;

        if( convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            groupView = inflater.inflate(R.layout.copy_group_layout, null);
        }

        /*
        Provide the data to the View, in this case it is just a string for the category.
         */
        TextView copyCategoryView = groupView.findViewById(R.id.tvCopyCategory);
        copyCategoryView.setText((String)getGroup(listPosition));

        return groupView;
    }

    @Override
    public View getChildView(int groupListPosition, int childListPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        /*
        The view here will depend on which group the child is in.  If this is rendering an owned
        copy, the view will be different than for an unowned (for sale) or a sold copy.  The
        latter categories show more information.
         */
        View childView = convertView;

        /*
        We'll want to show some fields as currency, so get a formatter up front.
         */
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        /*
        The convertView parameter is a built View that Android may give me to recycle.  Do I
        *have* to use it?  In a case like this where the views are heterogeneous, is it possible
        Android may give me the wrong type of View?
         */
        if( convertView == null ) {
            LayoutInflater inflater = LayoutInflater.from(context);
            switch( groupListPosition ) {
                case CopyType.OWNED_COPIES:
                    childView = inflater.inflate(R.layout.owned_copy_item_layout, null);
                    break;

                case CopyType.FORSALE_COPIES:
                case CopyType.SOLD_COPIES:
                    childView = inflater.inflate(R.layout.unowned_copy_item_layout, null);
                    break;

                default:
                    Log.e(TAG, "Invalid group list position " + groupListPosition);
                    return null;
            }
        }

        /*
        Provide the Copy data to the view.  The data will be different depending on whether the
        copy is owned or not.
         */
        switch( groupListPosition ) {
            case CopyType.OWNED_COPIES:
                /*
                Get the copy object at the group and child list positions.
                 */
                List<Copy> ownedCopiesList =
                        (List<Copy>) copiesMap.get(copyCategoryNamesList.get(CopyType.OWNED_COPIES));
                assert ownedCopiesList != null;
                Copy ownedCopy = ownedCopiesList.get(childListPosition);

                TextView gradeView = childView.findViewById(R.id.owned_copy_grade);
                if( ownedCopy.getGrade() != null ) {
                    gradeView.setText(ownedCopy.getGrade().toString());
                }

                TextView valueView = childView.findViewById(R.id.owned_copy_value);
                valueView.setText(currencyFormat.format(ownedCopy.getValue()));

                /*
                If there happen to be any notes on the copy, show the field, otherwise save the
                space.
                 */
                TextView ownedNotesView = childView.findViewById(R.id.owned_copy_notes);
                if( ownedCopy.getNotes() == null || ownedCopy.getNotes().isEmpty() ) {
                    ownedNotesView.setVisibility(View.INVISIBLE);
                } else {
                    ownedNotesView.setText(ownedCopy.getNotes());
                }

                break;

            case CopyType.FORSALE_COPIES:
                /*
                Get the copy object at the group and child list positions.
                 */
                List<Copy> forSaleCopiesList =
                        (List<Copy>) copiesMap.get(copyCategoryNamesList.get(CopyType.FORSALE_COPIES));
                assert forSaleCopiesList != null;
                Copy forSaleCopy = forSaleCopiesList.get(childListPosition);

                TextView forSaleGradeView = childView.findViewById(R.id.unowned_copy_grade);
                forSaleGradeView.setText(forSaleCopy.getGrade().toString());

                TextView forSaleDealerView = childView.findViewById(R.id.unowned_copy_dealer);
                forSaleDealerView.setText(forSaleCopy.getDealer());

                TextView forSalePriceView = childView.findViewById(R.id.unowned_copy_price);

                /*
                A copy that's for sale somewhere may have had changes to the price, all of which
                will be recorded in the data.  On this screen, show the latest (last added)
                price and date.
                 */
                ArrayList<Copy.Offer> offerList = forSaleCopy.getOffers();
                if( offerList == null || offerList.size() == 0 ) {
                    Log.e(TAG, "Unowned copy record for " + forSaleCopy.getTitle()
                            + " " + forSaleCopy.getIssue() + " has no offer price.");
                } else {
                    Copy.Offer thisOffer = offerList.get(offerList.size() - 1);
                    forSalePriceView.setText(currencyFormat.format(thisOffer.getOfferPrice()));

                    TextView forSaleDateView = childView.findViewById(R.id.unowned_copy_date);
                    forSaleDateView.setText(new SimpleDateFormat("MM/dd/yyyy")
                            .format(thisOffer.getOfferDate()));
                }

                /*
                If there happen to be any notes on the copy, show the field, otherwise save the
                space.
                 */
                TextView forSaleNotesView = childView.findViewById(R.id.unowned_copy_notes);
                if( forSaleCopy.getNotes() == null || forSaleCopy.getNotes().isEmpty() ) {
                    forSaleNotesView.setVisibility(View.GONE);
                } else {
                    forSaleNotesView.setText(forSaleCopy.getNotes());
                }

                break;

            case CopyType.SOLD_COPIES:
                /*
                Get the copy object at the group and child list positions.
                 */
                List<Copy> soldCopiesList =
                        (List<Copy>) copiesMap.get(copyCategoryNamesList.get(CopyType.SOLD_COPIES));
                assert soldCopiesList != null;
                Copy soldCopy = soldCopiesList.get(childListPosition);

                TextView soldGradeView = childView.findViewById(R.id.unowned_copy_grade);
                soldGradeView.setText(soldCopy.getGrade().toString());

                TextView soldDealerView = childView.findViewById(R.id.unowned_copy_dealer);
                soldDealerView.setText(soldCopy.getDealer());

                TextView soldPriceView = childView.findViewById(R.id.unowned_copy_price);
                soldPriceView.setText(currencyFormat.format(soldCopy.getSalePrice()));

                TextView soldDateView = childView.findViewById(R.id.unowned_copy_date);
                //soldDateView.setText(soldCopy.getDateSold().toString());
                soldDateView.setText(new SimpleDateFormat("MM/dd/yyyy")
                        .format(soldCopy.getDateSold()));

                /*
                If there happen to be any notes on the copy, show the field, otherwise save the
                space.
                 */
                TextView soldNotesView = childView.findViewById(R.id.unowned_copy_notes);
                if( soldCopy.getNotes() == null || soldCopy.getNotes().isEmpty() ) {
                    soldNotesView.setVisibility(View.INVISIBLE);
                } else {
                    soldNotesView.setText(soldCopy.getNotes());
                }

                break;

            default:
                Log.e(TAG, "Invalid category: " + groupListPosition);
                //return null;
        }

        return childView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
