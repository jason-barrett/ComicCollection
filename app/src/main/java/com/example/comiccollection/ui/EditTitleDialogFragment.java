package com.example.comiccollection.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.comiccollection.R;
import com.example.comiccollection.data.entities.Title;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EditTitleDialogFragment extends DialogFragment {

    public interface EditTitleDialogListener {
        public void onDialogClickEdit(EditTitleDialogFragment fragment);
    }

    EditTitleDialogFragment.EditTitleDialogListener listener;
    Title currentTitle;

    EditText nameField;
    EditText firstIssueField;
    EditText lastIssueField;

    TextView errorTextView;
    String errorText;

    String TAG = EditTitleDialogFragment.class.getSimpleName();

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public EditTitleDialogFragment(Title currentTitle) {
        this.currentTitle = currentTitle;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            Log.d(TAG, "onAttach()");
            listener = (EditTitleDialogFragment.EditTitleDialogListener) getActivity();
        } catch( ClassCastException e) {
            /*
            The activity does not implement this interface.
             */
            throw new ClassCastException(getActivity().toString()
                    + " does not implement EditTitleDialogListener.");
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

        errorTextView = view.findViewById(R.id.title_box_error_text);

        /*
        Pre-fill the text fields with the values for the current title being edited.
         */
        nameField.setText(currentTitle.getName());
        firstIssueField.setText(currentTitle.getFirstIssue());
        lastIssueField.setText(currentTitle.getLastIssue());

        errorTextView.setText(errorText);

        builder.setView(view)
                .setTitle(R.string.edit_title)
                .setPositiveButton(R.string.positive_edit_title, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /*
                        Overwrite the fields in the currentTitle object rather than creating
                        a new one.  The repository will recognize this object by its document ID.
                         */
                        synchronized (currentTitle) {
                            /*
                            There is a getter for this title, and this block should be atomic
                            with respect to it.
                             */
                            currentTitle.setName(nameField.getText().toString());
                            currentTitle.setFirstIssue(firstIssueField.getText().toString());
                            currentTitle.setLastIssue(lastIssueField.getText().toString());
                        }

                        Log.i(TAG, "Clicked to edit title " + currentTitle.toString());
                        listener.onDialogClickEdit(EditTitleDialogFragment.this);
                    }  //onClick()
                })
                .setNegativeButton(R.string.negative_add_title, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditTitleDialogFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    public Title getTitle() {
        synchronized (currentTitle) {
            return currentTitle;
        }
    }


}
