package com.example.comiccollection.ui;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.comiccollection.data.entities.Copy;
import com.example.comiccollection.data.entities.CopyType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
This is a special DialogFragment type that may be extended to support dialogs that add or modify
Copy objects.
 */
public abstract class AbstractCopyDialogFragment extends DialogFragment {

    protected EditText gradeField;
    protected EditText valueField;
    protected EditText dateField;
    protected EditText priceField;
    protected EditText otherPartyField;  // e.g., dealer
    protected EditText notesField;

    protected TextView errorTextView;
    protected String errorText;

    private final String TAG = AbstractCopyDialogFragment.class.getSimpleName();

    public void setCopyFromTextFields(Copy newCopy) {

        if( newCopy == null ) {
            Log.e(TAG, "Cannot set null copy to be edited");
            return;
        }

        newCopy.setGrade(gradeField.getText().toString());

        /*
        Strip the leading '$' from the monetary string before loading to the new Copy.
         */
        if (valueField.getText().length() > 0) {
            newCopy.setValue(Double.parseDouble(valueField.getText().toString()
                    .replaceAll("\\$", "")));
        }

        newCopy.setNotes(notesField.getText().toString());

        if (newCopy.getCopyType() == CopyType.OWNED) {
            /*
             Strip the leading '$' from the monetary string before loading to the new Copy.
             */
            if (priceField.getText().length() > 0) {
                newCopy.setPurchasePrice(Double.parseDouble(priceField.getText().toString()
                        .replaceAll("\\$", "")));
            }
            if (dateField.getText().length() > 0) {
                newCopy.setDatePurchased(dateFromDateField(dateField.getText().toString()));
            }
            if (otherPartyField.getText().length() > 0) {
                newCopy.setDealer(otherPartyField.getText().toString());
            }
        } else if (newCopy.getCopyType() == CopyType.FORSALE) {
            /*
             Strip the leading '$' from the monetary string before loading to the new Copy.
             */
            if (dateField.getText().length() > 0 && priceField.getText().length() > 0) {
                newCopy.addOffer(Double.parseDouble(priceField.getText().toString()
                                .replaceAll("\\$", "")),
                        dateFromDateField(dateField.getText().toString()));
            }
            if (otherPartyField.getText().length() > 0) {
                newCopy.setDealer(otherPartyField.getText().toString());
            }
        } else if (newCopy.getCopyType() == CopyType.SOLD) {
            /*
             Strip the leading '$' from the monetary string before loading to the new Copy.
             */
            if (priceField.getText().length() > 0) {
                newCopy.setSalePrice(Double.parseDouble(priceField.getText().toString()
                        .replaceAll("\\$", "")));
            }
            if (dateField.getText().length() > 0) {
                newCopy.setDateSold(dateFromDateField(dateField.getText().toString()));
            }
            if (otherPartyField.getText().length() > 0) {
                newCopy.setDealer(otherPartyField.getText().toString());
            }
        }
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
