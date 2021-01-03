package com.example.comiccollection.data.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UnownedCopy extends Copy {
    /*
    This is an entity class for 'UnownedCopy', that is, a copy of an issue that is available
    in the market (or has sold) and provides some basis for value in grade.
*/
    private String mDealer;

    private double mSalePrice;
    private Date mDateSold;

    /*
    The price could change over time and different observations.
     */
    private List<Offer> mOffers;

    public UnownedCopy() {
        super();

        mOffers = new ArrayList<Offer>();
    }
    public String getDealer() {
        return mDealer;
    }

    public void setDealer(String dealer) {
        this.mDealer = dealer;
    }

    public List<Offer> getOffers() {
        return mOffers;
    }

    public void addOffer(double offerPrice, Date offerDate) {
        mOffers.add(new Offer(offerPrice, offerDate));
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


    public static class Offer {
        double mOfferPrice;
        Date mOfferDate;

        public Offer(double offerPrice, Date offerDate) {
            this.mOfferPrice = offerPrice;
            this.mOfferDate = offerDate;
        }

        double getOfferPrice() {
            return mOfferPrice;
        }

        Date getOfferDate() {
            return mOfferDate;
        }
    }
}
