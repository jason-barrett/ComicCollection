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

/*
This fragment pops up a UI dialog that allows the user to add a title.  The dialog will ask the user
for:

 - the name of the title (e.g., "Justice League")
 - the first issue of interest (often #1 but not always)
 - the last issue of interest
 */
public class AddTitleDialogFragment extends DialogFragment {

    public interface AddTitleDialogListener {
        public void onDialogClickAdd(AddTitleDialogFragment fragment);
    }

    private AddTitleDialogListener listener;
    private Title newTitle;

    private EditText nameField;
    private EditText firstIssueField;
    private EditText lastIssueField;

    private TextView errorTextView;
    private String errorText;

    private final String TAG = AddTitleDialogFragment.class.getSimpleName();

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            Log.d(TAG, "onAttach()");
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

        errorTextView = view.findViewById(R.id.title_box_error_text);
        if( errorText != null ) {
            errorTextView.setText(errorText);
        }

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
                        listener.onDialogClickAdd(AddTitleDialogFragment.this);
                    }  //onClick()
                })
                .setNegativeButton(R.string.negative_add_title, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AddTitleDialogFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    /*
    Return a Title object for the title newly created by this dialog.
     */
    public Title getNewTitle() {
        return newTitle;
    }
}
