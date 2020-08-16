package com.example.comiccollection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.comiccollection.data.entities.Title;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TitlesAdapter extends RecyclerView.Adapter {

    ArrayList<Title> mTitles;

    //public TitlesAdapter(ArrayList<Title> titles) { mTitles = titles; }
    public TitlesAdapter() { mTitles = new ArrayList<Title>(); }


    public void updateTitles(ArrayList<Title> titles) {
        mTitles = titles;
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
    }

    @Override
    public int getItemCount() {
        return mTitles.size();
    }

    public class TitlesViewHolder extends RecyclerView.ViewHolder {

        TextView mTitleView;

        public TitlesViewHolder(View titlesItemView) {
            super(titlesItemView);

            mTitleView = (TextView) titlesItemView.findViewById(R.id.title_item_title);
        }

        public TextView getTitleView() {
            return mTitleView;
        }
    }
}
