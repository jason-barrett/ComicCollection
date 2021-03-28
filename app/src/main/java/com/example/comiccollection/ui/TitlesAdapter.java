package com.example.comiccollection.ui;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.comiccollection.R;
import com.example.comiccollection.data.entities.Title;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TitlesAdapter extends RecyclerView.Adapter {

    /*
    This is the canonical dataset that will be shown in the Adapter.
     */
    ArrayList<Title> mTitles;

    /*
    Specify a listener that will receive click events from the view holders, along with
    the knowledge of which View was clicked and which Title that represents in the data.
     */
    public interface TitleClickListener{
        public void onClick(View view, String titleName);
    }
    TitleClickListener mListener;

    String TAG = TitlesAdapter.class.getSimpleName();

    public TitlesAdapter(TitlesAdapter.TitleClickListener listener) {
        mTitles = new ArrayList<Title>();

        mListener = listener;
    }

    public void updateTitles(ArrayList<Title> titles) {
        mTitles = titles;
    }

    public Title getTitleAt(int position) {
        return mTitles.get(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.title_item_layout, parent, false);

        TitlesViewHolder titlesViewHolder = new TitlesViewHolder(view);
        return titlesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        /*
        Set the value of the title for this holder to the value in the Title object at position
        'position'
         */
        Title title = mTitles.get(position);
        TitlesViewHolder titlesViewHolder = (TitlesViewHolder) holder;
        titlesViewHolder.getTitleView().setText(title.getName());

        /*
        A long press on the view holder will provide a context menu for edits and deletes.
         */
        holder.itemView.setLongClickable(true);

    } // onBindViewHolder()

    @Override
    public int getItemCount() {
        return mTitles.size();
    }

    public class TitlesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener {

        TextView mTitleView;
        View mTitlesItemView;

        public TitlesViewHolder(View titlesItemView) {
            super(titlesItemView);

            mTitleView = (TextView) titlesItemView.findViewById(R.id.title_item_title);
            mTitlesItemView = titlesItemView;

            /*
            Set the OnCreateContextMenu listener for the main view.
             */
            titlesItemView.setOnCreateContextMenuListener(this);
            titlesItemView.setOnClickListener(this);
        }

        public TextView getTitleView() {
            return mTitleView;
        }

        /*
        implements View.OnClickListener
         */
        @Override
        public void onClick(View view) {
            mListener.onClick(view, (String)mTitleView.getText());
        }

        /*
        implements View.OnCreateContextMenuListener
         */
        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

            /*
            Credit to the guy who made this video (https://www.youtube.com/watch?v=fl5BB3I3MvQ) for the trick of
            sneaking the adapter position into the first parameter (the group number) as a way of getting the
            position (and therefore the Title) back to the Activity.
             */
            contextMenu.add(this.getAdapterPosition(), R.id.title_menu_option_edit, 0, "EDIT");
            contextMenu.add(this.getAdapterPosition(), R.id.title_menu_option_delete, 1, "DELETE");
        }

    }  // class TitlesViewHolder
}
