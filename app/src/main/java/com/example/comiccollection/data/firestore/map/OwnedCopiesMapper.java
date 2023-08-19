package com.example.comiccollection.data.firestore.map;

import android.util.Log;

import com.example.comiccollection.data.ComicDbHelper;
import com.example.comiccollection.data.entities.Copy;
import com.example.comiccollection.data.entities.CopyType;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
This class is responsible for mapping a QuerySnapshot object containing a set of owned copies
of an Issue, to a List of Copy objects.
 */
public class OwnedCopiesMapper {

    static String TAG = OwnedCopiesMapper.class.getSimpleName();

    public static List<Copy> map(QuerySnapshot value) {
        List<Copy> ownedCopyList = new ArrayList<Copy>();

        for( DocumentSnapshot document : value ) {
            if( document.exists() ) {
                Copy ownedCopy = map(document);
                if (ownedCopy != null) {
                    ownedCopyList.add(ownedCopy);
                }
            }
        }
        return ownedCopyList;
    }

    public static Copy map(DocumentSnapshot document) {
        if (document == null || !document.exists()) {
            return null;
        }

        try {
            /*
             The happy path is in here.
             */
            Copy ownedCopy = new Copy();

            ownedCopy.setTitle(document.getString(ComicDbHelper.CC_COPY_TITLE));
            ownedCopy.setIssue(document.getString(ComicDbHelper.CC_COPY_ISSUE));

            if (document.contains(ComicDbHelper.CC_COPY_PAGE_QUALITY)) {
                ownedCopy.setPageQuality(document.getString(ComicDbHelper.CC_COPY_PAGE_QUALITY));
            }
            if( document.contains(ComicDbHelper.CC_COPY_GRADE) ) {
                ownedCopy.setGrade((document.getString(ComicDbHelper.CC_COPY_GRADE)));
            }
            if( document.contains(ComicDbHelper.CC_COPY_NOTES) ) {
                ownedCopy.setNotes((document.getString(ComicDbHelper.CC_COPY_NOTES)));
            }
            if( document.contains(ComicDbHelper.CC_COPY_DEALER) ) {
                ownedCopy.setDealer(document.getString(ComicDbHelper.CC_COPY_DEALER));
            }
            if( document.contains(ComicDbHelper.CC_COPY_PURCHASE_PRICE) ) {
                Double costAsDouble = document.getDouble(ComicDbHelper.CC_COPY_PURCHASE_PRICE);
                if( costAsDouble != null ) {
                    ownedCopy.setPurchasePrice(costAsDouble);
                }
            }
            if( document.contains(ComicDbHelper.CC_COPY_DATE_PURCHASED) ) {
                ownedCopy.setDatePurchased(document.getDate(ComicDbHelper.CC_COPY_DATE_PURCHASED));
            }
            if( document.contains(ComicDbHelper.CC_COPY_VALUE) ) {
                Double valueAsDouble = document.getDouble(ComicDbHelper.CC_COPY_VALUE);
                if( valueAsDouble != null ) {
                    ownedCopy.setValue(valueAsDouble);
                }
            }

            ownedCopy.setDocumentId(document.getId());
            ownedCopy.setCopyType(CopyType.OWNED);

            return ownedCopy;
        } catch( RuntimeException e ) {
            Log.e(TAG, "Cannot map OwnedCopy document " + document.getId()
                    + ", may be malformed.");
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }

        return null;
    }

}
