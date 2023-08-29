package com.example.comiccollection.data.entities;

public enum CopyType {
    OWNED,
    FORSALE,
    SOLD,
    SOLDBYME;

    /*
    In some parts of the application, the framework maps copy types to ints.  For instance,
    in identifying expandable view groups that correspond to copy types.  For convenience
    and 'switchability', define this mapping in one place.
     */
    public static final int OWNED_COPIES = 0;
    public static final int FORSALE_COPIES = 1;
    public static final int SOLD_COPIES = 2;
    public static final int SOLDBYME_COPIES = 3;

}
