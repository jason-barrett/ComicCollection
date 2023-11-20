package com.example.comiccollection.data.firestore.map;

import android.util.Log;

import com.example.comiccollection.data.ComicDbHelper;
import com.example.comiccollection.data.entities.Copy;
import com.example.comiccollection.data.entities.CopyType;
import com.example.comiccollection.data.entities.Grade;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/*
This class is responsible for mapping a QuerySnapshot object containing a set of sold copies
of an Issue, to a List of Copy objects.
 */
public class SoldCopiesMapper {
    static String TAG = SoldCopiesMapper.class.getSimpleName();

    public static List<Copy> map(QuerySnapshot value) {
        List<Copy> soldCopyList = new ArrayList<Copy>();

        for( QueryDocumentSnapshot document : value ) {
            if( document.exists() ) {
                Copy soldCopy = map(document);
                if (soldCopy != null) {
                    soldCopyList.add(soldCopy);
                }
            }
        }
        return soldCopyList;
    }

    public static Copy map(QueryDocumentSnapshot document) {
        if (document == null || !document.exists()) {
            return null;
        }

        try {
            /*
             The happy path is in here.
             */
            Copy soldCopy = new Copy();

            soldCopy.setTitle(document.getString(ComicDbHelper.CC_COPY_TITLE));
            soldCopy.setIssue(document.getString(ComicDbHelper.CC_COPY_ISSUE));

            if (document.contains(ComicDbHelper.CC_COPY_PAGE_QUALITY)) {
                soldCopy.setPageQuality(document.getString(ComicDbHelper.CC_COPY_PAGE_QUALITY));
            }
            if( document.contains(ComicDbHelper.CC_COPY_GRADE) ) {
                soldCopy.setGrade(Grade.fromString(document.getString(ComicDbHelper.CC_COPY_GRADE)));
            }
            if( document.contains(ComicDbHelper.CC_COPY_NOTES) ) {
                soldCopy.setNotes((document.getString(ComicDbHelper.CC_COPY_NOTES)));
            }
            if( document.contains(ComicDbHelper.CC_COPY_DATE_OFFERED) ) {
                soldCopy.setDateOffered(document.getDate(ComicDbHelper.CC_COPY_DATE_OFFERED));
            }
            if( document.contains(ComicDbHelper.CC_COPY_DATE_SOLD) ) {
                soldCopy.setDateSold(document.getDate(ComicDbHelper.CC_COPY_DATE_SOLD));
            }

            if( document.contains(ComicDbHelper.CC_COPY_PURCHASER) ) {
                soldCopy.setPurchaser(document.getString(ComicDbHelper.CC_COPY_PURCHASER));
            }
            if( document.contains(ComicDbHelper.CC_COPY_SALE_PRICE) ) {
                soldCopy.setSalePrice(document.getDouble(ComicDbHelper.CC_COPY_SALE_PRICE));
            }
            if( document.contains(ComicDbHelper.CC_COPY_DATE_SOLD) ) {
                soldCopy.setDateSold(document.getDate(ComicDbHelper.CC_COPY_DATE_SOLD));
            }

            soldCopy.setDocumentId(document.getId());
            soldCopy.setCopyType(CopyType.SOLDBYME);

            return soldCopy;
        } catch( RuntimeException e ) {
            Log.e(TAG, "Cannot map SoldCopy document " + document.getId()
                    + ", may be malformed.");
            Log.e(TAG, e.getMessage());
        }

        return null;
    }
}
