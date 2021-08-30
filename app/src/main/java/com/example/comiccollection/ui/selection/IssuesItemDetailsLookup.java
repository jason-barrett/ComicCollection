package com.example.comiccollection.ui.selection;

import android.view.MotionEvent;
import android.view.View;

import com.example.comiccollection.ui.IssuesAdapter;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

/*
This is a class that the selection library wants.  It uses an inner class
of a ViewHolder to get position information for the view that the user
pressed or tapped to make the selection.
 */
public class IssuesItemDetailsLookup extends ItemDetailsLookup<Long> {
    private final RecyclerView recyclerView;

    public IssuesItemDetailsLookup(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    /*
    This is pretty much boilerplate plumbing.
     */
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public ItemDetails<Long> getItemDetails(@NonNull MotionEvent e) {
        View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if( view != null ) {
            IssuesAdapter.IssuesViewHolder viewHolder =
                    (IssuesAdapter.IssuesViewHolder)recyclerView.findContainingViewHolder(view);
            if( viewHolder != null) {
                return viewHolder.getItemDetails();
            }
        }
        return null;
    }
}
