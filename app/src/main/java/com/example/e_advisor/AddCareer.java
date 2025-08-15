package com.example.e_advisor;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.e_advisor.utils.APIAddress;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddCareer extends AppCompatActivity {

    private EditText TitleEditText, descriptionEditText, careerTagsEditText, tipsEditText;
    private Spinner qualifications;
    private ChipGroup courseChipGroup;
    private Button submitBtn;
    private Dialog progress_dialog;
    List<EditText> editTextList = new ArrayList<>();
    Gson gson = new Gson();
    APIAddress apiAddress = new APIAddress();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_career);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TitleEditText = findViewById(R.id.TitleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        careerTagsEditText = findViewById(R.id.careerTagsEditText);
        tipsEditText = findViewById(R.id.tipsEditText);

        qualifications = findViewById(R.id.qualifications);
        courseChipGroup = findViewById(R.id.courseChipGroup);

        submitBtn = findViewById(R.id.submit_btn);

        editTextList.add(tipsEditText);
        editTextList.add(descriptionEditText);

        //Initialize progress dialog
        progress_dialog = new Dialog(this);
        progress_dialog.setContentView(R.layout.progress_dialog_view);
        progress_dialog.setCancelable(false);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.admin_spinner_item,
                getResources().getStringArray(R.array.qualifications)
        );

        adapter.setDropDownViewResource(R.layout.admin_spinner_item);
        qualifications.setAdapter(adapter);
    }
}