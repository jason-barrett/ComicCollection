package com.example.comiccollection.data.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Copy {
    /*
    This is an entity class for 'Copy', that is, a copy of an issue that may be in my collection,
    or may not.

    Start with some information about the physical comic itself.
     */
    private String mTitle;
    private String mIssue;

    private String mGrade;
    private String mPageQuality;

    private String mNotes;

    /*
    Some transactional information related to the history of the copy.  Could be sale price in
    the market, price paid by me, and so on.
     */
    private String mDealer;
    private double mPurchasePrice;  // i.e., by me.

    private String mPurchaser;
    private double mSalePrice;   // i.e., by someone I sold it to, or by someone else in the market.

    private Date mDatePurchased;
    private Date mDateOffered;
    private Date mDateSold;

    /*
    The price could change over time and different observations.
    */
    private ArrayList<Offer> mOffers;

    /*
    Current estimated value of the copy.  Might be filled in by the user or calculated from
    known offers and transactions for that issue.
     */
    private double mValue;

    private String mDocumentId;

    /*
    What kind of copy is this, or where is it in its lifecycle?  Owned, for sale, sold...
     */
    CopyType copyType;

    public Copy() {}
    /*
    Create the simplest possible Copy with just the title and issue number.
     */
    public Copy(String title, String issue) {
        /*
        The common use case for creating copies is probably going to be marking issues on the
        issue list as 'owned'.
         */
        this(title, issue, CopyType.OWNED);
    }

    public Copy(String title, String issue, CopyType copyType) {
        mTitle = title;
        mIssue = issue;
        this.copyType = copyType;

        mOffers = new ArrayList<Offer>();
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

    public String getDealer() {
        return mDealer;
    }

    public void setDealer(String mDealer) {
        this.mDealer = mDealer;
    }

    public double getPurchasePrice() {
        return mPurchasePrice;
    }

    public void setPurchasePrice(double mPurchasePrice) {
        this.mPurchasePrice = mPurchasePrice;
    }

    public String getPurchaser() {
        return mPurchaser;
    }

    public void setPurchaser(String mPurchaser) {
        this.mPurchaser = mPurchaser;
    }

    public double getSalePrice() {
        return mSalePrice;
    }

    public void setSalePrice(double mSalePrice) {
        this.mSalePrice = mSalePrice;
    }

    public Date getDatePurchased() {
        return mDatePurchased;
    }

    public void setDatePurchased(Date mDatePurchased) {
        this.mDatePurchased = mDatePurchased;
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

    public ArrayList<Offer> getOffers() {
        return mOffers;
    }

    public synchronized void addOffer(double offerPrice, Date offerDate) {
        mOffers.add(new Offer(offerPrice, offerDate));
    }

    public synchronized void addOffer(Offer offer) {
        mOffers.add(offer);
    }

    public double getValue() {
        return mValue;
    }

    public void setValue(double value) {
        this.mValue = value;
    }

    public CopyType getCopyType() {
        return copyType;
    }

    public void setCopyType(CopyType copyType) {
        this.copyType = copyType;
    }

    public String getDocumentId() { return mDocumentId; }

    public void setDocumentId(String documentId) { mDocumentId = documentId; }

    public static class Offer {
        private final double mOfferPrice;
        private final Date mOfferDate;

        private String mDocumentId;

        public Offer(double offerPrice, Date offerDate) {
            this.mOfferPrice = offerPrice;
            this.mOfferDate = offerDate;
        }

        public double getOfferPrice() {
            return mOfferPrice;
        }

        public Date getOfferDate() {
            return mOfferDate;
        }

        public String getDocumentId() {
            return mDocumentId;
        }

        public void setDocumentId( String documentId ) {
            mDocumentId = documentId;
        }
    }

}
