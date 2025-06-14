package com.example.e_advisor.utils;

import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_advisor.R;
import com.example.e_advisor.response_objects.MaterialResponse;

import java.util.List;

public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder> {

    private final List<MaterialResponse.Material> materialList;
    private final OnMaterialClickListener listener;
    public MaterialAdapter(List<MaterialResponse.Material> materialList, OnMaterialClickListener listener) {
        this.materialList = materialList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from((parent.getContext()))
                .inflate(R.layout.career_list_item, parent, false);
        return new MaterialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {
        MaterialResponse.Material material = materialList.get(position);
        holder.bind(material, listener);
    }

    @Override
    public int getItemCount() {
        return materialList != null ? materialList.size() : 0;
    }

    public static class MaterialViewHolder extends RecyclerView.ViewHolder {
        TextView materialTitle;
        ImageButton materialDetails;

        public MaterialViewHolder(@NonNull View itemView) {
            super(itemView);
            materialTitle = itemView.findViewById(R.id.careerContent);
            materialDetails = itemView.findViewById(R.id.careerButton);
        }

        public void bind(MaterialResponse.Material material, OnMaterialClickListener listener){
            materialTitle.setText(material.getTitle());
            materialDetails.setOnClickListener(view -> {
                if (listener != null){
                    listener.onMaterialClick(material);
                }
            });
        }
    }
}
