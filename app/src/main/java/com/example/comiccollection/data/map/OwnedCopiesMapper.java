package com.example.comiccollection.data.map;

import android.util.Log;

import com.example.comiccollection.data.ComicDbHelper;
import com.example.comiccollection.data.entities.OwnedCopy;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/*
This class is responsible for mapping a QuerySnapshot object containing a set of owned copies
of an Issue, to a List of OwnedCopy (extends Copy) objects.
 */
public class OwnedCopiesMapper {

    static String TAG = OwnedCopiesMapper.class.getSimpleName();

    public static List<OwnedCopy> map(QuerySnapshot value) {
        List<OwnedCopy> ownedCopyList = new ArrayList<OwnedCopy>();

        for( QueryDocumentSnapshot document : value ) {
            if( document.exists() ) {
                OwnedCopy ownedCopy = map(document);
                if (ownedCopy != null) {
                    ownedCopyList.add(ownedCopy);
                }
            }
        }
        return ownedCopyList;
    }

    public static OwnedCopy map(QueryDocumentSnapshot document) {
        if (document == null || !document.exists()) {
            return null;
        }

        try {
            /*
             The happy path is in here.
             */
            OwnedCopy ownedCopy = new OwnedCopy();

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
            if( document.contains(ComicDbHelper.CC_COPY_DATE_OFFERED) ) {
                ownedCopy.setDateOffered(document.getDate(ComicDbHelper.CC_COPY_DATE_OFFERED));
            }
            if( document.contains(ComicDbHelper.CC_COPY_DATE_SOLD) ) {
                ownedCopy.setDateSold(document.getDate(ComicDbHelper.CC_COPY_DATE_SOLD));
            }

            if( document.contains(ComicDbHelper.CC_COPY_DEALER) ) {
                ownedCopy.setDealer(document.getString(ComicDbHelper.CC_COPY_DEALER));
            }
            if( document.contains(ComicDbHelper.CC_COPY_COST) ) {
                String costAsString = document.getString(ComicDbHelper.CC_COPY_COST);
                ownedCopy.setCost(FirestoreTypeUtils.handleDoubleAsString(costAsString, ownedCopy,
                        ComicDbHelper.CC_COPY_COST));
            }
            if( document.contains(ComicDbHelper.CC_COPY_DATE_PURCHASED) ) {
                ownedCopy.setDatePurchased(document.getDate(ComicDbHelper.CC_COPY_DATE_PURCHASED));
            }
            if( document.contains(ComicDbHelper.CC_COPY_VALUE) ) {
                String valueAsString = document.getString(ComicDbHelper.CC_COPY_VALUE);
                ownedCopy.setValue(FirestoreTypeUtils.handleDoubleAsString(valueAsString, ownedCopy,
                        ComicDbHelper.CC_COPY_VALUE));
            }

            ownedCopy.setDocumentId(document.getId());

            return ownedCopy;
        } catch( RuntimeException e ) {
            Log.e(TAG, "Cannot map OwnedCopy document " + document.getId()
                    + ", may be malformed.");
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

}
