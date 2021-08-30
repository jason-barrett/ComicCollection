package com.example.comiccollection.data.entities;

import java.util.Date;

public abstract class Copy {
    /*
    This is an entity class for 'Copy', that is, a copy of an issue that may be in my collection,
    or may not.
     */
    private String mTitle;
    private String mIssue;

    private String mGrade;
    private String mPageQuality;

    private String mNotes;

    private Date mDateOffered;
    private Date mDateSold;

    private String mDocumentId;

    public Copy() {}
    /*
    Create the simplest possible Copy with just the title and issue number.
     */
    public Copy(String title, String issue) {
        mTitle = title;
        mIssue = issue;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getIssue() {
        return mIssue;
    }

    public void setIssue(String issue) {
        this.mIssue = issue;
    }

    public String getPageQuality() {
        return mPageQuality;
    }

    public void setPageQuality(String pageQuality) {
        this.mPageQuality = pageQuality;
    }

    public String getGrade() {
        return mGrade;
    }

    public void setGrade(String grade) {
        this.mGrade = grade;
    }

    public String getNotes() {
        return mNotes;
    }

    public void setNotes(String notes) {
        this.mNotes = notes;
    }


    public Date getDateOffered() {
        return mDateOffered;
    }

    public void setDateOffered(Date dateOffered) {
        this.mDateOffered = dateOffered;
    }

    public Date getDateSold() {
        return mDateSold;
    }

    public void setDateSold(Date dateSold) {
        this.mDateSold = dateSold;
    }

    public String getDocumentId() { return mDocumentId; }

    public void setDocumentId(String documentId) { mDocumentId = documentId; }

}
