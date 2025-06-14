package com.example.e_advisor.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_advisor.R;
import com.example.e_advisor.response_objects.ExamTipApiResponse;

import java.util.List;

public class TipAdapter extends RecyclerView.Adapter<TipAdapter.TipViewHolder> {

    private final List<ExamTipApiResponse> tipList;

    public TipAdapter(List<ExamTipApiResponse> tipList) {
        this.tipList = tipList;
    }

    @NonNull
    @Override
    public TipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exam_tip, parent, false);
        return new TipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TipViewHolder holder, int position) {
        ExamTipApiResponse tip = tipList.get(position);
        holder.tipContent.setText(tip.getContent());
    }

    @Override
    public int getItemCount() {
        return tipList.size();
    }

    static class TipViewHolder extends RecyclerView.ViewHolder {
        TextView tipContent;

        public TipViewHolder(@NonNull View itemView) {
            super(itemView);
            tipContent = itemView.findViewById(R.id.tipContent);
        }
    }
}

