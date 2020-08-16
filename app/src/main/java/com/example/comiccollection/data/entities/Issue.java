package com.example.comiccollection.data.entities;

import java.util.ArrayList;

public class Issue {
    /*
    This is an entity class for an issue of a comic title.
     */

    private String mIssueNumber;  // Technically could be alphanumeric.
    private ArrayList<Copy> mCopies;

    public String getIssueNumber() {
        return mIssueNumber;
    }

    public void setIssueNumber(String issueNumber) {
        this.mIssueNumber = issueNumber;
    }

    public ArrayList<Copy> getCopies() {
        return mCopies;
    }

    public void setCopies(ArrayList<Copy> copies) {
        this.mCopies = copies;
    }
}
