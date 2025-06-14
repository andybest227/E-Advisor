package com.example.e_advisor;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.e_advisor.response_objects.CareerResponseObject;

public class CareerDetails extends AppCompatActivity {
    private TextView titleTextView, descriptionTextView;
    private LinearLayout qualificationsContainer, relatedCoursesContainer, tipsContainer, tagsContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_career_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        qualificationsContainer = findViewById(R.id.qualificationsContainer);
        relatedCoursesContainer = findViewById(R.id.relatedCoursesContainer);
        tipsContainer = findViewById(R.id.tipsContainer);
        tagsContainer = findViewById(R.id.tagsContainer);


        CareerResponseObject.Careers career = getIntent().getParcelableExtra("career");

        if (career != null) {
            titleTextView.setText(career.getTitle());
            descriptionTextView.setText(career.getDescription());

            for (String q : career.getQualifications()) {
                TextView tv = new TextView(this);
                tv.setText("• " + q);
                qualificationsContainer.addView(tv);
            }

            for (String c : career.getRelatedCourses()) {
                TextView tv = new TextView(this);
                tv.setText("• " + c);
                relatedCoursesContainer.addView(tv);
            }

            for (String tip : career.getTips()) {
                TextView tv = new TextView(this);
                tv.setText("✓ " + tip);
                tipsContainer.addView(tv);
            }

            for (int i = 0; i < career.getTags().length; i++) {
                String tag = career.getTags()[i];
                TextView tagView = new TextView(this);
                tagView.setText(tag);
                tagView.setBackgroundResource(R.drawable.tag_background);
                tagView.setPadding(16, 8, 16, 8);
                tagView.setTextColor(Color.DKGRAY);

                // Set margin top only if it's not the first item
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

                if (i > 0) {
                    // Set top margin in dp
                    int marginInDp = 8;
                    params.topMargin = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            marginInDp,
                            getResources().getDisplayMetrics()
                    );
                }

                tagView.setLayoutParams(params);
                tagsContainer.addView(tagView);
            }

        }



    }
}