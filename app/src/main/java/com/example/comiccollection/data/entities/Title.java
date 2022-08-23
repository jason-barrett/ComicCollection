package com.example.comiccollection.data.entities;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class Title {
         /*
        This is an entity class for a comic title e.g., 'Spider-Man (Amazing)" or "Fantastic Four".
         */

    private String mName;
    private String mFirstIssue;
    private String mLastIssue;

    /*
    This is the document ID given by Firestore.  It's handy to have for deletes and
    modifications.
     */
    private String mDocumentId;

    public Title() {
    }

    /*
    Provide a copy constructor for testing and convenience.
     */
    public Title(Title title) {

        this.setDocumentId(title.getDocumentId());
        this.setName(title.getName());
        this.setFirstIssue(title.getFirstIssue());
        this.setLastIssue(title.getLastIssue());
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getFirstIssue() {
        return mFirstIssue;
    }

    public void setFirstIssue(String firstIssue) {
        this.mFirstIssue = firstIssue;
    }

    public String getLastIssue() {
        return mLastIssue;
    }

    public void setLastIssue(String lastIssue) {
        this.mLastIssue = lastIssue;
    }

    public String getDocumentId() {
        return mDocumentId;
    }

    public void setDocumentId(String documentId) {
        this.mDocumentId = documentId;
    }

    @NonNull
    @Override
    public String toString() {
        return getName() + " - " + getFirstIssue() + " (First) " + getLastIssue() + " (Last)";
    }
}
