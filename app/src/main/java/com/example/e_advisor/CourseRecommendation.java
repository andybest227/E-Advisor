package com.example.e_advisor;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_advisor.response_objects.CourseApiResponse;
import com.example.e_advisor.response_objects.CoursesTagsObject;
import com.example.e_advisor.response_objects.TipsApiResponse;
import com.example.e_advisor.resquest_objects.CourseRecommendationRequestObject;
import com.example.e_advisor.utils.APIAddress;
import com.example.e_advisor.response_objects.ApiResponse;
import com.example.e_advisor.utils.AsyncGetTask;
import com.example.e_advisor.utils.AsyncPostTask;
import com.example.e_advisor.response_objects.ExamTipApiResponse;
import com.example.e_advisor.response_objects.TagsObject;
import com.example.e_advisor.utils.CourseAdapter;
import com.example.e_advisor.utils.TipAdapter;
import com.example.e_advisor.utils.Token;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CourseRecommendation extends AppCompatActivity {
    private final APIAddress apiAddress = new APIAddress();
    private ChipGroup chipGroup;
    private Button btnRecommendations;
    private Dialog progress_dialog;
    private final List<String> selectedTags = new ArrayList<>();
    private final CourseRecommendationRequestObject recommendationRequestObject = new CourseRecommendationRequestObject();
    private String [] skills;
    private List<CourseApiResponse.Course> courseList = new ArrayList<>();
    RecyclerView recyclerView;
    Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_course_recommendation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        chipGroup = findViewById(R.id.recommendation_chip_group);
        btnRecommendations = findViewById(R.id.getRecommendations);
        recyclerView = findViewById(R.id.recyclerViewRecommendations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        //Set content view to the dialog
        progress_dialog = new Dialog(this);
        progress_dialog.setContentView(R.layout.progress_dialog_view);
        progress_dialog.setCancelable(false);

        AsyncGetTask getTask = getAsyncGetTask();
        getTask.execute(apiAddress.api_address()+"/api/auth/courses/get/tags");

        btnRecommendations.setOnClickListener(view -> {
            courseList.clear();
            recyclerView.setAdapter(null);
            for (int i = 0; i < chipGroup.getChildCount(); i++){
                Chip chip = (Chip) chipGroup.getChildAt(i);
                if (chip.isChecked()){
                    selectedTags.add(chip.getText().toString());
                }
            }

            if (selectedTags.isEmpty()){
                errorMessage("Select at least one skill");
                return;
            }
            recommendationRequestObject.setSkills(null);
            skills = selectedTags.toArray(new String[0]);

            recommendationRequestObject.setSkills(skills);

            AsyncPostTask<CourseRecommendationRequestObject> asyncGetTipsTask = asyncPostTask();
            asyncGetTipsTask.execute(apiAddress.api_address()+"/api/auth/recommendations/recommend");
            selectedTags.clear();
        });

    }

    private AsyncGetTask getAsyncGetTask() {
        String token = Token.getInstance().getToken();
        // handle response
        // handle error
        return new AsyncGetTask(
                token,
                new AsyncGetTask.TaskCallback() {
                    @Override
                    public void onSuccess(String response) {
                        CoursesTagsObject apiResponse = gson.fromJson(response, CoursesTagsObject.class);
                        String [] tags = apiResponse.getData().getTags();
                        for (String tag : tags) {
                            Chip chip = new Chip(CourseRecommendation.this);
                            chip.setText(tag);
                            chip.setCheckable(true);
                            chip.setChipBackgroundColorResource(R.color.chip_background_selector);
                            chipGroup.addView(chip);
                        }
                    }

                    @Override
                    public void onError(int code, String errorMessage) {
                       errorMessage(errorMessage);
                    }
                },
                progress_dialog,
                btnRecommendations
        );
    }

    private AsyncPostTask<CourseRecommendationRequestObject> asyncPostTask(){
        String token = Token.getInstance().getToken();
        return new AsyncPostTask<>(
                recommendationRequestObject,
                token,
                new AsyncPostTask.TaskCallback() {
                    @Override
                    public void onSuccess(String responseMessage) {
                        try {
                            if (responseMessage != null && responseMessage.trim().startsWith("{")) {
                                CourseApiResponse courseApiResponse = gson.fromJson(responseMessage, CourseApiResponse.class);

                                courseList = courseApiResponse.getData();

                                if (courseList != null && !courseList.isEmpty()) {
                                    CourseAdapter adapter = new CourseAdapter(courseList);
                                    recyclerView.setAdapter(adapter);
                                } else {
                                    errorMessage("No courses found");
                                }

                            } else {
                                errorMessage("Invalid server response");
                            }

                        } catch (JsonSyntaxException e) {
                            Log.e("JSON_PARSE_ERROR", "Invalid JSON: " + e.getMessage());
                            errorMessage("Unable to parse response");
                        }
                    }
                    @Override
                    public void onError(int code, String errorMessage) {
                        ApiResponse response = gson.fromJson(errorMessage, ApiResponse.class);
                        // Handle error
                        errorMessage(response.getMessage());
                        System.out.println(code +": "+ response.getMessage());
                    }
                },
                progress_dialog,
                btnRecommendations
        );
    }

    //Error Alert
    public void errorMessage(String msg){
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Error").setContentText(msg)
                .show();
    }
}