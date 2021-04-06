package com.example.comiccollection.data;

public class ComicDbHelper {
    /*
    This class contains some constants and other useful references to the Firestore data
    storage.
     */

    /*
    The database name Google Realtime Database gave me.
     */
    //public static final String CC_DB_NAME = "comic-collection-rtd";

    /*
    The database name Cloud Firestore gave me.
     */
    public static final String CC_DB_NAME = "comic-collection-791f5";

    /*
    The data store is organized as a collection called 'titles' with subcollections
    'issues' (of a title) and 'copies' (of an issue).
     */
    public static final String CC_COLLECTION_TITLE = "titles";
    public static final String CC_COLLECTION_ISSUES = "issues";
    public static final String CC_COLLECTION_COPIES = "copies";

    /*
    The following names are the data elements in the 'titles' collection directly.
     */
    public static final String CC_TITLE_NAME = "name";
    public static final String CC_TITLE_FIRST_ISSUE = "firstIssue";
    public static final String CC_TITLE_LAST_ISSUE = "lastIssue";

    /*
    The following names are the data elements in the 'issues' collection.
     */
    public static final String CC_ISSUE_NUMBER = "issue";
    public static final String CC_ISSUE_TITLE = "title";
    public static final String CC_ISSUE_WANTED = "iwantit";
    public static final String CC_ISSUE_OWNED = "owned";
    public static final String CC_ISSUE_SOLD = "sold";
    public static final String CC_ISSUE_UNOWNED = "unowned";

    /*
    The following names are the data elements in the 'owned' or 'unowned' collections.  They
    represent individual copies of a certain issue.
     */
    public static final String CC_COPY_GRADE = "grade";
    public static final String CC_COPY_PAGE_QUALITY = "pageQuality";
    public static final String CC_COPY_NOTES = "notes";
    public static final String CC_COPY_COST = "cost";
    public static final String CC_COPY_DEALER = "dealer";
    public static final String CC_COPY_PURCHASER = "purchaser";

    public static final String CC_COPY_ISSUE = "issue";
    public static final String CC_COPY_TITLE = "title";

    // TODO: The same copy may have multiple cost / dateOffered pairs over time.
    public static final String CC_COPY_OFFERS = "offers";
    public static final String CC_COPY_DATE_OFFERED = "dateOffered";
    public static final String CC_COPY_OFFER_PRICE = "offerPrice";

    public static final String CC_COPY_SALE_PRICE = "salePrice";
    public static final String CC_COPY_DATE_SOLD = "dateSold";
    public static final String CC_COPY_DATE_PURCHASED = "datePurchased";

    public static final String CC_COPY_VALUE = "value";
}
