package com.example.comiccollection.data;

/*
This class represents a set of statistics about the collection.

If I can ever go to a later Java version, this might be a record.
 */
public class CollectionStats {

    /*
    The total number of individual issues owned in the collection (includes duplicate copies
    of an issue)
     */
    private final int totalIssues;

    /*
    The total value of the collection.
     */
    private final double totalValue;

    public CollectionStats(int totalIssues, double totalValue) {
        this.totalIssues = totalIssues;
        this.totalValue = totalValue;
    }

    public int getTotalIssues() {
        return totalIssues;
    }

    public double getTotalValue() {
        return totalValue;
    }
}
