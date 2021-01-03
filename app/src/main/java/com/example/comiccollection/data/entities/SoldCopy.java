package com.example.comiccollection.data.entities;

import java.util.Date;

public class SoldCopy extends Copy {
    /*
    This is an entity class for 'SoldCopy', that is, an individual copy of an issue that I have
    sold out of my collection.
     */
    private String mPurchaser;
    private double mSalePrice;

    private Date mDateSold;

    public SoldCopy() {
        super();
    }
    public String getPurchaser() {
        return mPurchaser;
    }

    public void setPurchaser(String purchaser) {
        this.mPurchaser = purchaser;
    }

    public double getSalePrice() {
        return mSalePrice;
    }

    public void setSalePrice(double salePrice) {
        this.mSalePrice = salePrice;
    }

    public Date getDateSold() {
        return mDateSold;
    }

    public void setDateSold(Date dateSold) {
        this.mDateSold = dateSold;
    }


}
