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

    @NonNull
    @Override
    public String toString() {
        return getName() + " - " + getFirstIssue() + " (First) " + getLastIssue() + " (Last)";
    }
}
