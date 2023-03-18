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
    private ArrayList<Copy> mOwnedCopies;

    /*
    Information about copies I know about and don't own (i.e., in the marketplace).
     */
    private ArrayList<Copy> mUnownedCopies;

    /*
    Information about copies I have sold.
    */
    private ArrayList<Copy> mSoldCopies;


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

    public ArrayList<Copy> getOwnedCopies() {

        /*
        Return an empty list rather than null.
         */
        if( mOwnedCopies == null ) {
            return new ArrayList<>();
        }

        return mOwnedCopies;
    }

    public void setOwnedCopies(ArrayList<Copy> copies) {
        this.mOwnedCopies = copies;
    }

    public ArrayList<Copy> getUnownedCopies() {

        /*
        Return an empty list rather than null.
         */
        if( mUnownedCopies == null ) {
            return new ArrayList<>();
        }

        return mUnownedCopies;
    }

    public void setUnownedCopies(ArrayList<Copy> copies) {
        this.mUnownedCopies = copies;
    }

    public ArrayList<Copy> getSoldCopies() {

        /*
        Return an empty list rather than null.
         */
        if( mSoldCopies == null ) {
            return new ArrayList<>();
        }



        return mSoldCopies;
    }

    public void setSoldCopies(ArrayList<Copy> copies) {
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

    /*
    Do I own this issue but want an upgrade?
     */
    public boolean upgradeWanted() {
        if( numberOfCopiesOwned() > 0 && isWanted()) {
            return true;
        }

        return false;
    }

    /*
    A convenience method to return the issue together with its title.
     */
    public String getTitleAndIssueNumber() {
        return getTitle() + " " + getIssueNumber();
    }

    /*
    A convenience method to determine whether the issue number falls within a given range.
     */
    public boolean isInRange(int firstIssue, int lastIssue) {
        int thisIssue = Integer.parseInt(getIssueNumber());

        if( thisIssue >= firstIssue && thisIssue <= lastIssue) {
            return true;
        }
        return false;
    }
}
