package com.example.e_advisor.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_advisor.R;
import com.example.e_advisor.response_objects.CareerResponseObject;

import java.util.List;

public class CareerAdapter extends RecyclerView.Adapter<CareerAdapter.CareerViewHolder> {

    private final List<CareerResponseObject.Careers> careerList;
    private final OnCareerClickListener listener;

    public CareerAdapter(CareerResponseObject careerResponseObject, OnCareerClickListener listener) {
        this.careerList = careerResponseObject.getCareers();
        this.listener = listener;
    }

    @NonNull
    @Override
    public CareerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.career_list_item, parent, false);
        return new CareerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CareerAdapter.CareerViewHolder holder, int position) {
        CareerResponseObject.Careers career = careerList.get(position);
        holder.bind(career, listener);
    }

    @Override
    public int getItemCount() {
        return careerList != null ? careerList.size() : 0;
    }

    public static class CareerViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageButton careerButton;

        public CareerViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.careerContent);
            careerButton = itemView.findViewById(R.id.careerButton); // Add this to your XML
        }

        public void bind(CareerResponseObject.Careers career, OnCareerClickListener listener) {
            titleTextView.setText(career.getTitle());

            careerButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCareerClick(career);
                }
            });
        }
    }
}
