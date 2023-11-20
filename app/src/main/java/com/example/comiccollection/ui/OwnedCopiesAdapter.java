package com.example.comiccollection.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comiccollection.R;
import com.example.comiccollection.data.entities.Grade;
import com.example.comiccollection.data.entities.OwnedCopy;

import java.util.ArrayList;

public class OwnedCopiesAdapter extends RecyclerView.Adapter<OwnedCopiesAdapter.OwnedCopiesViewHolder> {

    private ArrayList<OwnedCopy> ownedCopiesList;

    public OwnedCopiesAdapter(ArrayList<OwnedCopy> ownedCopiesList) {
        this.ownedCopiesList = ownedCopiesList;
    }

    public void updateOwnedCopiesList(ArrayList<OwnedCopy> ownedCopiesList) {
        this.ownedCopiesList = ownedCopiesList;
    }

    @NonNull
    @Override
    public OwnedCopiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.owned_copy_item_layout, parent, false);

        return new OwnedCopiesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OwnedCopiesViewHolder holder, int position) {

        Grade grade = ownedCopiesList.get(position).getGrade();
        holder.getGradeView().setText(grade == null  ? "NG" : grade.toString());

        String value = String.valueOf(ownedCopiesList.get(position).getValue());
        holder.getValueView().setText(value);
    }

    @Override
    public int getItemCount() {
        return ownedCopiesList.size();
    }

    public static class OwnedCopiesViewHolder extends RecyclerView.ViewHolder {

        private final TextView mGradeView;
        private final TextView mValueView;

        public OwnedCopiesViewHolder(@NonNull View itemView) {
            super(itemView);

            mGradeView = itemView.findViewById(R.id.owned_copy_grade);
            mValueView = itemView.findViewById(R.id.owned_copy_value);
        }

        public TextView getGradeView() {
            return mGradeView;
        }

        public TextView getValueView() {
            return mValueView;
        }
    }
}
