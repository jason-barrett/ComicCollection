package com.example.comiccollection.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.comiccollection.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.comiccollection.data.entities.Copy;
import com.example.comiccollection.data.entities.CopyType;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/*
This fragment pops up a UI dialog that allows the user to add a known copy of the current issue
to the datastore.

It may be a copy the user owns, or one he knows of in the market.
 */
public class AddCopyDialogFragment extends DialogFragment {

    public interface AddCopyDialogListener {
        public void onDialogClickAdd(AddCopyDialogFragment fragment);
    }

    /*
    By default, we're adding an owned copy.
     */
    private CopyType copyType = CopyType.OWNED;

    private AddCopyDialogListener listener;

    /*
    The copy created by this dialog is owned by the dialog and provided to the Activity
    that owns the dialog via the listener.
     */
    private Copy newCopy;
    private String thisTitle;
    private String thisIssue;

    private TextView addCopyText;

    private RadioButton addOwnedButton;
    private RadioButton addForSaleButton;
    private RadioButton addSoldButton;

    private EditText gradeField;
    private EditText valueField;
    private EditText dateField;
    private EditText priceField;
    private EditText otherPartyField;  // e.g., dealer

    private TextView errorTextView;
    private String errorText;

    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AddCopyDialogListener) getActivity();
        } catch( ClassCastException e ) {
            throw new ClassCastException(getActivity().toString()
                    + " does not implement AddCopyDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        /*
        We'll need the title and issue, so let's read them out of the Bundle.  If they're
        not there, we can't create a new Copy object.
         */
        if( savedInstanceState != null ) {
            thisTitle = (String) savedInstanceState.get("title");
            thisIssue = (String) savedInstanceState.get("issue");
        }

        assert getArguments() != null;
        thisTitle = getArguments().getString("title");
        thisIssue = getArguments().getString("issue");

        if( thisTitle == null || thisIssue == null ) {
            Log.e(TAG, "Title and issue not passed in to AddCopyDialogFragment");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.add_copy_layout, null);

        addCopyText = view.findViewById(R.id.add_copy_type);
        addCopyText.setText(getResources()
                .getString(R.string.add_copy, thisTitle + " " + thisIssue));

        addOwnedButton = view.findViewById(R.id.add_copy_radio_owned);
        addOwnedButton.setOnClickListener(this::onRadioButtonClicked);
        addForSaleButton = view.findViewById(R.id.add_copy_radio_forsale);
        addForSaleButton.setOnClickListener(this::onRadioButtonClicked);
        addSoldButton = view.findViewById(R.id.add_copy_radio_sold);
        addSoldButton.setOnClickListener(this::onRadioButtonClicked);

        gradeField = view.findViewById(R.id.edit_copy_grade);
        valueField = view.findViewById(R.id.edit_copy_value);
        priceField = view.findViewById(R.id.edit_copy_price);
        dateField = view.findViewById(R.id.edit_copy_date);
        otherPartyField = view.findViewById(R.id.edit_copy_dealer);

        errorTextView = view.findViewById(R.id.copy_box_error_text);
        if( errorText != null ) {
            errorTextView.setText(errorText);
        }

        /*
        Set the DatePickerDialog on the date field.
         */
        dateField.setOnClickListener( (v) -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getActivity(),
                    (dpview, yy, mm, dd) -> {
                        /*
                        Add 1 to the month because the picker uses 0-11.
                         */
                        dateField.setText((mm + 1) + "-" + dd + "-" + yy);
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        return builder.setView(view)
                .setNegativeButton(R.string.negative_add_copy,
                        (dialog, which) -> {
                          dialog.dismiss();
                       })
                .setPositiveButton(R.string.positive_add_copy,
                        (dialog, which) -> {
                            /*
                            Build the new Copy from the data that's been entered here.
                             */
                            if( thisTitle == null || thisTitle.isEmpty()
                                    || thisIssue == null || thisIssue.isEmpty() ) {
                                errorTextView.setText(R.string.copy_error_no_title_issue);
                                return;
                            }

                            newCopy = new Copy(thisTitle, thisIssue);
                            Log.i(TAG, "Creating a new " + copyType.toString() + " copy of "
                                + thisTitle + " " + thisIssue);

                            newCopy.setGrade(gradeField.getText().toString());
                            if( valueField.getText().length() > 0 ) {
                                newCopy.setValue(Double.parseDouble(valueField.getText().toString()));
                            }

                            if( copyType == CopyType.OWNED ) {
                                if( priceField.getText().length() > 0 ) {
                                    newCopy.setPurchasePrice(Double.parseDouble(priceField.getText().toString()));
                                }
                                if( dateField.getText().length() > 0 ) {
                                    newCopy.setDatePurchased(dateFromDateField(dateField.getText().toString()));
                                }
                                if( otherPartyField.getText().length() > 0 ) {
                                    newCopy.setDealer(otherPartyField.getText().toString());
                                }
                            } else if( copyType == CopyType.FORSALE ) {
                                if( dateField.getText().length() > 0 && priceField.getText().length() > 0 ) {
                                    newCopy.addOffer(Double.parseDouble(priceField.getText().toString()),
                                        dateFromDateField(dateField.getText().toString()));
                                }
                                if( otherPartyField.getText().length() > 0 ) {
                                    newCopy.setDealer(otherPartyField.getText().toString());
                                }
                            } else if( copyType == CopyType.SOLD ) {
                                if( priceField.getText().length() > 0 ) {
                                    newCopy.setSalePrice(Double.parseDouble(priceField.getText().toString()));
                                }
                                if( dateField.getText().length() > 0 ) {
                                    newCopy.setDateSold(dateFromDateField(dateField.getText().toString()));
                                }
                                if( otherPartyField.getText().length() > 0 ) {
                                    newCopy.setDealer(otherPartyField.getText().toString());
                                }
                            }

                            newCopy.setCopyType(copyType);
                            listener.onDialogClickAdd(this);
                })
                .create();
    }

    /*
    This is given as the radio button click handler in the XML that defines the radio
    buttons.
     */
    public void onRadioButtonClicked(View radioButton) {
        if( radioButton.getId() == R.id.add_copy_radio_owned ) {
            copyType = CopyType.OWNED;

            priceField.setHint(R.string.copy_purchase_price);
            dateField.setHint(R.string.copy_purchase_date);
            otherPartyField.setHint(R.string.copy_purchased_from);
        } else if ( radioButton.getId() == R.id.add_copy_radio_forsale ) {
            copyType = CopyType.FORSALE;

            priceField.setHint(R.string.copy_offer_price);
            dateField.setHint(R.string.copy_offer_date);
            otherPartyField.setHint(R.string.copy_seller);
        } else if( radioButton.getId() == R.id.add_copy_radio_sold ) {
            copyType = CopyType.SOLD;

            priceField.setHint(R.string.copy_sale_price);
            dateField.setHint(R.string.copy_sale_date);
            otherPartyField.setHint(R.string.copy_seller);
        }
    }

    /*
    Return to the calling class, the copy we've created based on the user's entries in this
    dialog.
     */
    public Copy getNewCopy() {
        return newCopy;
    }

    /*
    Allow the error text to be set from outside, for example, if the calling activity invalidates
    the Copy that was entered.
     */
    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    /*
    Translate a string from the date field in the format mm-dd-yyyy, into a Date object for
    the datastore.  I realize Date is sort-of deprecated, but I support Firestore for now
    and Firestore still uses it.
     */
    private Date dateFromDateField(String dateFieldText) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        Date returnDate;
        try {
            returnDate = dateFormat.parse(dateFieldText);
        } catch( ParseException e) {
            /*
            If the date field doesn't parse right (which shouldn't happen because we're using a
            date picker), return the current date.
             */
            returnDate = new Date();
        }

        return returnDate;
    }
}
