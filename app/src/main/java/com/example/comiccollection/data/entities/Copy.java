package com.example.comiccollection.data.entities;

import java.util.Date;

public class Copy {
    /*
    This is an entity class for 'Copy', that is, a copy of an issue that may be in my collection,
    or may not.
     */
    private String mGrade;
    private String mDealer;
    private double mCost;
    private Date mDateOffered;
    private Date mDateSold;
    private Date mDatePurchased;
    private boolean mOwned;

    public String getGrade() {
        return mGrade;
    }

    public void setGrade(String grade) {
        this.mGrade = mGrade;
    }

    public String getDealer() {
        return mDealer;
    }

    public void setDealer(String dealer) {
        this.mDealer = mDealer;
    }

    public double getCost() {
        return mCost;
    }

    public void setCost(double cost) {
        this.mCost = mCost;
    }

    public Date getDateOffered() {
        return mDateOffered;
    }

    public void setDateOffered(Date dateOffered) {
        this.mDateOffered = mDateOffered;
    }

    public Date getDateSold() {
        return mDateSold;
    }

    public void setDateSold(Date dateSold) {
        this.mDateSold = mDateSold;
    }

    public Date getDatePurchased() {
        return mDatePurchased;
    }

    public void setDatePurchased(Date datePurchased) {
        this.mDatePurchased = mDatePurchased;
    }

    public boolean isOwned() {
        return mOwned;
    }

    public void setOwned(boolean owned) {
        this.mOwned = mOwned;
    }
}
