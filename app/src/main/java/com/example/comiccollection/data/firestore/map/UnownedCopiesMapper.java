package com.example.comiccollection.data.firestore.map;

import android.util.Log;

import com.example.comiccollection.data.ComicDbHelper;
import com.example.comiccollection.data.entities.Copy;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/*
This class is responsible for mapping a QuerySnapshot object containing a set of unowned copies
of an Issue, to a List of UnownedCopy (extends Copy) objects.
 */
public class UnownedCopiesMapper {
    static String TAG = UnownedCopiesMapper.class.getSimpleName();

    public static List<Copy> map(QuerySnapshot value) {
        List<Copy> unownedCopyList = new ArrayList<Copy>();

        for( QueryDocumentSnapshot document : value ) {
            if( document.exists() ) {
                Copy unownedCopy = map(document);
                if (unownedCopy != null) {
                    unownedCopyList.add(unownedCopy);
                }
            }
        }
        return unownedCopyList;
    }

    public static Copy map(QueryDocumentSnapshot document) {
        if (document == null || !document.exists()) {
            return null;
        }

        try {
            /*
             The happy path is in here.
             */
            Copy unownedCopy = new Copy();

            unownedCopy.setTitle(document.getString(ComicDbHelper.CC_COPY_TITLE));
            unownedCopy.setIssue(document.getString(ComicDbHelper.CC_COPY_ISSUE));

            if (document.contains(ComicDbHelper.CC_COPY_PAGE_QUALITY)) {
                unownedCopy.setPageQuality(document.getString(ComicDbHelper.CC_COPY_PAGE_QUALITY));
            }
            if( document.contains(ComicDbHelper.CC_COPY_GRADE) ) {
                unownedCopy.setGrade((document.getString(ComicDbHelper.CC_COPY_GRADE)));
            }
            if( document.contains(ComicDbHelper.CC_COPY_NOTES) ) {
                unownedCopy.setNotes((document.getString(ComicDbHelper.CC_COPY_NOTES)));
            }
            if( document.contains(ComicDbHelper.CC_COPY_DATE_OFFERED) ) {
                unownedCopy.setDateOffered(document.getDate(ComicDbHelper.CC_COPY_DATE_OFFERED));
            }
            if( document.contains(ComicDbHelper.CC_COPY_DATE_SOLD) ) {
                unownedCopy.setDateSold(document.getDate(ComicDbHelper.CC_COPY_DATE_SOLD));
            }

            if( document.contains(ComicDbHelper.CC_COPY_DEALER) ) {
                unownedCopy.setDealer(document.getString(ComicDbHelper.CC_COPY_DEALER));
            }

            /*
            This copy may have multiple recorded offer prices (i.e., price changes).
             */
            if( document.contains(ComicDbHelper.CC_COPY_OFFERS) ) {
                List<Map<String, Object>> offers =
                        (List<Map<String, Object>>) document.get(ComicDbHelper.CC_COPY_OFFERS);
                for( Map<String, Object> offer : offers ) {
                    Double offerPrice = null;
                    Date offerDate = null;
                    for( String fieldName : offer.keySet() ) {
                        if( fieldName.equals(ComicDbHelper.CC_COPY_OFFER_PRICE) ) {
                            offerPrice = (Double)offer.get(fieldName);
                        } else if( fieldName.equals(ComicDbHelper.CC_COPY_DATE_OFFERED) ) {
                            offerDate = (Date)offer.get(fieldName);
                        }
                    }
                    if( offerPrice != null && offerDate != null ) {
                        unownedCopy.addOffer(offerPrice, offerDate);
                    } else {
                        Log.e(TAG, "Failed to load an unowned copy for "
                                + unownedCopy.getTitle() + " #" + unownedCopy.getIssue()
                                + ", data may be malformed.");
                    }
                }
            }

            if( document.contains(ComicDbHelper.CC_COPY_SALE_PRICE) ) {
                unownedCopy.setSalePrice(document.getDouble(ComicDbHelper.CC_COPY_SALE_PRICE));
                String priceAsString = document.getString(ComicDbHelper.CC_COPY_SALE_PRICE);
                unownedCopy.setSalePrice(FirestoreTypeUtils
                        .handleDoubleAsString(priceAsString, unownedCopy,
                                ComicDbHelper.CC_COPY_SALE_PRICE));
            }
            if( document.contains(ComicDbHelper.CC_COPY_DATE_SOLD) ) {
                unownedCopy.setDateSold(document.getDate(ComicDbHelper.CC_COPY_DATE_SOLD));
            }

            unownedCopy.setDocumentId(document.getId());

            return unownedCopy;
        } catch( RuntimeException e ) {
            Log.e(TAG, "Cannot map UnownedCopy document " + document.getId()
                    + ", may be malformed.");
        }

        return null;
    }
}
