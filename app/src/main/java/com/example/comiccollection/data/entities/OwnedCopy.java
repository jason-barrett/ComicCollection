package com.example.comiccollection.data.entities;

import java.util.Date;

public class OwnedCopy extends Copy {
    /*
    This is an entity class for 'OwnedCopy', that is, an individual copy of an issue that is in my
    collection.
     */
    private String mDealer;
    private double mCost;

    private Date mDatePurchased;

    private double mValue;

    public OwnedCopy() {
        super();
    }
    public String getDealer() {
        return mDealer;
    }

    public void setDealer(String dealer) {
        this.mDealer = dealer;
    }

    public double getCost() {
        return mCost;
    }

    public void setCost(double cost) {
        this.mCost = cost;
    }

    public Date getDatePurchased() {
        return mDatePurchased;
    }

    public void setDatePurchased(Date datePurchased) {
        this.mDatePurchased = datePurchased;
    }

    public double getValue() {
        return mValue;
    }

    public void setValue(double value) {
        this.mValue = value;
    }



}
