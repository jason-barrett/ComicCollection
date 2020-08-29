package com.example.comiccollection;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.comiccollection.data.entities.Title;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddTitleDialogFragment extends DialogFragment {

    public interface AddTitleDialogListener {
        public void onDialogPositiveClick(AddTitleDialogFragment fragment);
    }

    AddTitleDialogListener listener;
    Title newTitle;

    EditText nameField;
    EditText firstIssueField;
    EditText lastIssueField;

    String TAG = AddTitleDialogFragment.class.getSimpleName();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            /*
            The context will be the activity this fragment instance is being attached to.
             */
            Log.d(TAG, "onAttach()");
            //listener = (AddTitleDialogListener) context;
            listener = (AddTitleDialogListener) getActivity();
        } catch( ClassCastException e) {
            /*
            The activity does not implement this interface.
             */
            throw new ClassCastException(getActivity().toString()
                    + " does not implement AddTitleDialogListener.");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.add_title_layout, null);

        nameField = view.findViewById(R.id.edit_title_name);
        firstIssueField = view.findViewById(R.id.edit_title_first_issue);
        lastIssueField = view.findViewById(R.id.edit_title_last_issue);

        builder.setView(view)
                .setTitle(R.string.add_title)
                .setPositiveButton(R.string.positive_add_title, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        newTitle = new Title();

                        newTitle.setName(nameField.getText().toString());
                        newTitle.setFirstIssue(firstIssueField.getText().toString());
                        newTitle.setLastIssue(lastIssueField.getText().toString());

                        Log.i(TAG, "Clicked to add title " + newTitle.toString());

                        listener.onDialogPositiveClick(AddTitleDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.negative_add_title, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AddTitleDialogFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    public Title getNewTitle() {
        return newTitle;
    }
}
