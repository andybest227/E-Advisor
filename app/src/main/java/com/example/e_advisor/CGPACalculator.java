package com.example.e_advisor;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CGPACalculator extends AppCompatActivity {
    private LinearLayout courseContainer;
    private Button addCourseBtn, calculateBtn;
    private TextView resultText;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cgpacalculator);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        courseContainer = findViewById(R.id.courseContainer);
        addCourseBtn = findViewById(R.id.addCourseBtn);
        calculateBtn = findViewById(R.id.calculateBtn);
        resultText = findViewById(R.id.resultText);
        inflater = LayoutInflater.from(this);

        // Add first input row by default
        addCourseRow();

        addCourseBtn.setOnClickListener(v -> addCourseRow());

        calculateBtn.setOnClickListener(v -> calculateCGPA());
    }

    private void addCourseRow() {
        View row = inflater.inflate(R.layout.item_course_input, null);
        Button removeBtn = row.findViewById(R.id.removeRowBtn);

        removeBtn.setOnClickListener(v -> courseContainer.removeView(row));

        courseContainer.addView(row);
    }


    private void calculateCGPA() {
        double totalPoints = 0;
        int totalUnits = 0;

        for (int i = 0; i < courseContainer.getChildCount(); i++) {
            View row = courseContainer.getChildAt(i);

            TextInputEditText gradeInput = row.findViewById(R.id.gradeInput);
            TextInputEditText unitInput = row.findViewById(R.id.unitInput);

            if (gradeInput == null || unitInput == null) {
                Log.w("CGPA", "Missing gradeInput or unitInput in row " + i);
                continue;
            }

            String[] grades = {"A", "B", "C", "D", "E", "F"};


            String grade = Objects.requireNonNull(gradeInput.getText()).toString().trim().toUpperCase();
            String unitStr = Objects.requireNonNull(unitInput.getText()).toString().trim();

            if (!Arrays.asList(grades).contains(grade)){
                errorMessage("Invalid grade detected");
                return;
            }

            if (Integer.parseInt(unitStr) < 1 || Integer.parseInt(unitStr) > 5){
                errorMessage("Invalid credit unit");
                return;
            }


            if (grade.isEmpty() || unitStr.isEmpty()) continue;

            try {
                int unit = Integer.parseInt(unitStr);
                double gradePoint = getPointFromGrade(grade);
                totalPoints += (gradePoint * unit);
                totalUnits += unit;
            } catch (NumberFormatException e) {
                Log.e("CGPA", "Invalid unit input at row " + i + ": " + unitStr);
            }
        }


        double cgpa = totalUnits > 0 ? totalPoints / totalUnits : 0;
        resultText.setText(String.format("Your CGPA: %.2f", cgpa));
    }

    private double getPointFromGrade(String grade) {
        switch (grade) {
            case "A": return 5.0;
            case "B": return 4.0;
            case "C": return 3.0;
            case "D": return 2.0;
            case "E": return 1.0;
            default: return 0.0;
        }
    }
    public void errorMessage(String msg){
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Error").setContentText(msg)
                .show();
    }
}
