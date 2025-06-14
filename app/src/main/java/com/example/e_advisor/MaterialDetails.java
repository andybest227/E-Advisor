package com.example.e_advisor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.e_advisor.response_objects.MaterialResponse;

public class MaterialDetails extends AppCompatActivity {
    TextView materialTitle, materialDescription, materialType, materialCourse;
    Button openLinkButton;
    String materialUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_material_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        materialTitle = findViewById(R.id.materialTitle);
        materialDescription = findViewById(R.id.materialDescription);
        materialType = findViewById(R.id.materialType);
        materialCourse = findViewById(R.id.materialCourse);
        openLinkButton = findViewById(R.id.openLinkButton);

        MaterialResponse.Material material = getIntent().getParcelableExtra("material");

        assert material != null;
        String title = material.getTitle();
        String description = material.getDescription();
        String type = material.getType();
        String course = material.getCourse();
        materialUrl = material.getUrl();
        materialTitle.setText(title);
        materialDescription.setText(description);
        materialType.setText("Type: " + type);
        materialCourse.setText("Course: " + course);

        openLinkButton.setOnClickListener(v -> {
            if (materialUrl != null && !materialUrl.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(materialUrl));
                startActivity(browserIntent);
            }
        });
    }
}