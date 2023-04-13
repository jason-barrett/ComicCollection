package com.example.comiccollection.data.firestore.map;

import com.example.comiccollection.data.ComicDbHelper;
import com.example.comiccollection.data.entities.Copy;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OffersMapper {

    static String TAG = IssuesMapper.class.getSimpleName();

    /*
    Map a list of offer documents in a QuerySnapshot to a List of Offer objects.
     */
    public static List<Copy.Offer> map(QuerySnapshot querySnapshot) {
        List<Copy.Offer> offerList = new ArrayList<>();

        for(DocumentSnapshot documentSnapshot : querySnapshot.getDocuments() ) {
            double offerPrice = documentSnapshot.getDouble(ComicDbHelper.CC_COPY_OFFER_PRICE);
            Date offerDate = documentSnapshot.getDate(ComicDbHelper.CC_COPY_DATE_OFFERED);

            Copy.Offer offer = new Copy.Offer(offerPrice, offerDate);
            offer.setDocumentId(documentSnapshot.getId());

            offerList.add(offer);
        }

        return offerList;
    }
}
