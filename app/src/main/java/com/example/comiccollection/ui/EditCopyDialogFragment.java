package com.example.comiccollection.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.comiccollection.R;
import com.example.comiccollection.data.entities.Copy;
import com.example.comiccollection.data.entities.CopyType;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class EditCopyDialogFragment extends AbstractCopyDialogFragment {

    private Copy copyToEdit;

    private TextView errorTextView;
    private String errorText;

    /*
    Registered listener to receive the results of the edit.
     */
    EditCopyDialogListener listener;

    private final String TAG = this.getClass().getSimpleName();

    public interface EditCopyDialogListener {
        public void onDialogClickEdit(EditCopyDialogFragment fragment);
    }

    public EditCopyDialogFragment(@NonNull Copy copyToEdit, @NonNull EditCopyDialogListener listener) {
        this.copyToEdit = copyToEdit;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        /*
        Set up the view from the XML layout.
         */
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.base_copy_layout, null);

        /*
        We'll want to show some fields as currency, so get a formatter up front.
         */
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        /*
        Fill in the default values for each field with the current values in the Copy
        object.  It'll depend a little bit on what type of copy it is.
         */
        gradeField = view.findViewById(R.id.edit_copy_grade);
        if( copyToEdit.getGrade() != null && !copyToEdit.getGrade().isEmpty()) {
            gradeField.setText(copyToEdit.getGrade());
        }

        valueField = view.findViewById(R.id.edit_copy_value);
        valueField.setText(currencyFormat.format(copyToEdit.getValue()));

        dateField = view.findViewById(R.id.edit_copy_date);
        /*
        Set the DatePickerDialog on the date field.
         */
        dateField.setOnClickListener( (v) -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(
                    Objects.requireNonNull(getActivity()),
                    (dpview, yy, mm, dd) -> {
                        /*
                        Add 1 to the month because the picker uses 0-11.
                         */
                        dateField.setText((mm + 1) + "-" + dd + "-" + yy);
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        priceField = view.findViewById(R.id.edit_copy_price);
        otherPartyField = view.findViewById(R.id.edit_copy_dealer);

        if( copyToEdit.getCopyType() == CopyType.OWNED ) {
            if( copyToEdit.getDatePurchased() != null ) {
                dateField.setText(new SimpleDateFormat("MM/dd/yyyy")
                        .format(copyToEdit.getDatePurchased()));
            }

            priceField.setText(currencyFormat.format(copyToEdit.getPurchasePrice()));

            if( copyToEdit.getDealer() != null && !copyToEdit.getDealer().isEmpty() ) {
                otherPartyField.setText(copyToEdit.getDealer());
            }
        } else if( copyToEdit.getCopyType() == CopyType.FORSALE ) {
            /*
            For now, just deal with the last offer.  The screen doesn't support showing the
            full offer history for a FORSALE copy yet.
             */
            int numOffers = copyToEdit.getOffers().size();
            if( numOffers > 0 ) {
                Copy.Offer lastOffer = copyToEdit.getOffers().get( numOffers - 1 );

                if( lastOffer.getOfferDate() != null ){
                    dateField.setText(new SimpleDateFormat("MM/dd/yyyy")
                            .format(lastOffer.getOfferDate()));
                }

                priceField.setText(currencyFormat.format(lastOffer.getOfferPrice()));
            }

            if( copyToEdit.getDealer() != null && !copyToEdit.getDealer().isEmpty() ) {
                otherPartyField.setText(copyToEdit.getDealer());
            }
        } else if( copyToEdit.getCopyType() == CopyType.SOLD ) {
            if( copyToEdit.getDateSold() != null ) {
                dateField.setText(new SimpleDateFormat("MM/dd/yyyy")
                        .format(copyToEdit.getDateSold()));
            }

            priceField.setText(currencyFormat.format(copyToEdit.getSalePrice()));

            if( copyToEdit.getDealer() != null && !copyToEdit.getDealer().isEmpty() ) {
                otherPartyField.setText(copyToEdit.getDealer());
            }
        }

        notesField = view.findViewById(R.id.edit_copy_notes);
        if( copyToEdit.getNotes() != null && !copyToEdit.getNotes().isEmpty() ) {
            notesField.setText(copyToEdit.getNotes());
        }

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton(R.string.positive_edit_copy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /*
                        Save the edits to the current copy and pass the copy to the listener.
                         */
                        setCopyFromTextFields(copyToEdit);
                        listener.onDialogClickEdit(EditCopyDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.negative_edit_copy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .create();
    }

    public Copy getEditCopy() {
        return copyToEdit;
    }
}
