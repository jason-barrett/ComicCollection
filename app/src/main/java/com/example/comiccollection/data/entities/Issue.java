package com.example.comiccollection.data.entities;

import java.util.ArrayList;

public class Issue {
    /*
    This is an entity class for an issue of a comic title.
     */

    private String mTitle;

    /*
    Technically the issue number may be alphanumeric, for instance the first annual of a
    title may be denoted 'A1'.
     */
    private String mIssueNumber;

    /*
    Do I want this issue?  Note that there could be an issue I don't have and don't want, or
    an issue I have and still want (i.e., an upgrade).
     */
    private boolean mWanted;

    /*
    Information about copies I own.
     */
    private ArrayList<OwnedCopy> mOwnedCopies;

    /*
    Information about copies I know about and don't own (i.e., in the marketplace).
     */
    private ArrayList<UnownedCopy> mUnownedCopies;

    /*
    Information about copies I have sold.
    */
    private ArrayList<SoldCopy> mSoldCopies;


    private String mDocumentId;

    public String getTitle() { return mTitle; }

    public void setTitle(String title) { mTitle = title; }

    public boolean isWanted() { return mWanted; }

    public void setWanted(boolean wanted) { mWanted = wanted; }

    public String getIssueNumber() {
        return mIssueNumber;
    }

    public void setIssueNumber(String issueNumber) {
        this.mIssueNumber = issueNumber;
    }

    public ArrayList<OwnedCopy> getOwnedCopies() {
        return mOwnedCopies;
    }

    public void setOwnedCopies(ArrayList<OwnedCopy> copies) {
        this.mOwnedCopies = copies;
    }

    public ArrayList<UnownedCopy> getUnownedCopies() {
        return mUnownedCopies;
    }

    public void setUnownedCopies(ArrayList<UnownedCopy> copies) {
        this.mUnownedCopies = copies;
    }

    public ArrayList<SoldCopy> getSoldCopies() {
        return mSoldCopies;
    }

    public void setSoldCopies(ArrayList<SoldCopy> copies) {
        this.mSoldCopies = copies;
    }

    public String getDocumentId() { return mDocumentId; }

    public void setDocumentId(String documentId) { mDocumentId = documentId; }

    /*
    Answer the question, how many copies of this issue do I own?
     */
    public int numberOfCopiesOwned() {
        if( mOwnedCopies == null ) {
            return 0;
        }
        return mOwnedCopies.size();
    }
}
