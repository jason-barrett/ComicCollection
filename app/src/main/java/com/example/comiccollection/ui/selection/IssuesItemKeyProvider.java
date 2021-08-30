package com.example.comiccollection.ui.selection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.widget.RecyclerView;

/*
This class will provide an item key for an element in a RecyclerView list.
 */
public class IssuesItemKeyProvider extends ItemKeyProvider<Long> {

    private final RecyclerView recyclerView;

    public IssuesItemKeyProvider(RecyclerView recyclerView) {
        super(SCOPE_MAPPED);
        this.recyclerView = recyclerView;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public Long getKey(int position) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();

        if( adapter != null ) {
            return adapter.getItemId(position);
        } else {
            return null;
        }
    }

    @Override
    public int getPosition(@NonNull @org.jetbrains.annotations.NotNull Long key) {
        int position = RecyclerView.NO_POSITION;
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForItemId(key);
        if( viewHolder != null ) {
            position = viewHolder.getLayoutPosition();
        }
        return position;
    }
}
