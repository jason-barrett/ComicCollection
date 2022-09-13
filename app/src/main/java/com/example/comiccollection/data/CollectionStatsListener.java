package com.example.comiccollection.data;

/*
Defines an interface for listening for a CollectionStats object from a repository.
 */
public interface CollectionStatsListener {
    public void onCollectionStatsReady(CollectionStats collectionStats);
    public void onCollectionStatsFailure(String message);
}
