package com.example.comiccollection.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.comiccollection.R;
import com.example.comiccollection.data.entities.Copy;
import com.example.comiccollection.data.entities.CopyType;
import com.example.comiccollection.ui.utilities.UiUtils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class RecordSaleDialogFragment extends DialogFragment {

    public interface RecordSaleDialogListener {
        public void onDialogClickRecordSale(RecordSaleDialogFragment fragment);
    }
    private EditText salePriceField;

    private EditText saleDateField;

    private TextView errorTextField;

    private Copy copy;

    private RecordSaleDialogListener listener;

    public RecordSaleDialogFragment(Copy copy, RecordSaleDialogListener listener) {
        this.copy = copy;
        this.listener = listener;
    }

    public Copy getCopy() {
        return copy;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.record_sale_confirm_layout, null);

        /*
        We'll want to show some fields as currency, so get a formatter up front.
         */
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        saleDateField = view.findViewById(R.id.record_sale_date);
        salePriceField = view.findViewById(R.id.record_sale_price);
        errorTextField = view.findViewById(R.id.record_sale_error_text);

        saleDateField.setOnClickListener( (v) -> {
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
                        saleDateField.setText((mm + 1) + "-" + dd + "-" + yy);
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        if( !copy.getOffers().isEmpty() ) {
            Copy.Offer lastOffer = copy.getOffers().get( copy.getOffers().size() - 1 );

            saleDateField.setText(new SimpleDateFormat("MM/dd/yyyy")
                    .format(new Date()));

            double salePrice = lastOffer.getOfferPrice();
            salePriceField.setText(currencyFormat.format(salePrice));
        }

        builder.setView(view)
                .setPositiveButton(R.string.positive_record_sale, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        copy.setSalePrice(Double.parseDouble(salePriceField.getText().toString()
                                .replaceAll("\\$", "")));

                        copy.setDateSold(UiUtils.dateFromDateField(saleDateField.getText().toString()));
                        copy.setCopyType(CopyType.SOLD);

                        listener.onDialogClickRecordSale(RecordSaleDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.negative_record_sale, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Objects.requireNonNull(RecordSaleDialogFragment.this.getDialog()).cancel();
                    }
                });

        return builder.create();
    }

}
