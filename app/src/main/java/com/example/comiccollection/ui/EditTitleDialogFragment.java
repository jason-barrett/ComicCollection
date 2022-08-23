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

    private EditTitleDialogFragment.EditTitleDialogListener listener;

    /*
    The title object reference being given, which holds the state of the title before any
    editing.  Do not mutate the object.
     */
    private final Title currentTitle;

    /*
    The new title object we are creating here, which reflects the edits.
     */
    private Title newTitle;

    private EditText nameField;
    private EditText firstIssueField;
    private EditText lastIssueField;

    private TextView errorTextView;
    private String errorText;

    /*
    A small state variable to allow the error text field to show a warning when the user
    chooses to confirm an edit - kind of an 'Are you sure?'
     */
    private boolean warned;

    private String TAG = EditTitleDialogFragment.class.getSimpleName();

    public void setErrorText(String errorText) {

        this.errorText = errorText;
        errorTextView.setText(errorText);
    }

    public EditTitleDialogFragment(Title currentTitle) {
        this.currentTitle = currentTitle;

        this.warned = false;
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

        AlertDialog alertDialog = builder.setView(view)
                .setTitle(R.string.edit_title)
                .setPositiveButton(R.string.positive_edit_title, null)
                .setNegativeButton(R.string.negative_add_title, null)
                .show();

        /*
        Add listeners to both buttons.  We don't use the AlertDialog's 'built-in' listeners
        because we want a little more control of when the dialog goes away.
         */

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener( (v) -> {
            /*
            Error checking.  Make sure that no fields were blanked out.
             */
            if( nameField.getText() == null || nameField.getText().toString().isEmpty() )
            {
                setErrorText(getString(R.string.title_name_is_required));
                return;
            }

            if( firstIssueField.getText() == null || firstIssueField.getText().toString().isEmpty() )
            {
                setErrorText(getString(R.string.title_first_issue_is_required));
                return;
            }

            if( lastIssueField.getText() == null || lastIssueField.getText().toString().isEmpty() )
            {
                setErrorText(getString(R.string.title_last_issue_is_required));
                return;
            }

            /*
             The user has chosen to perform the edit.  Show a warning before actually
             doing it.
             */
            if( !warned ) {
                setErrorText(getString(R.string.title_warning_will_delete));
                warned = true;
            } else {
                /*
                 Create a new Title object.  The repository will recognize the
                 object by its document ID, so copy it to the new object.
                 */
                newTitle = new Title();
                newTitle.setDocumentId(currentTitle.getDocumentId());

                newTitle.setName(nameField.getText().toString());
                newTitle.setFirstIssue(firstIssueField.getText().toString());
                newTitle.setLastIssue(lastIssueField.getText().toString());

                Log.i(TAG, "Clicked to edit title " + currentTitle.toString());
                listener.onDialogClickEdit(EditTitleDialogFragment.this);

                warned = false;
            }
        });

        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener( (v) -> {
            /*
             Clear any warning that may be present, if the user responds to the warning
             by canceling the dialog.
             */
            warned = false;

            EditTitleDialogFragment.this.getDialog().cancel();
        });

        return alertDialog;
    }

    public Title getCurrentTitle() {
        return currentTitle;
    }

    public Title getNewTitle() {
        return newTitle;
    }

}
