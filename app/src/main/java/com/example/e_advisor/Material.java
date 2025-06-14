package com.example.e_advisor;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_advisor.response_objects.MaterialCoursesResponse;
import com.example.e_advisor.response_objects.MaterialResponse;
import com.example.e_advisor.resquest_objects.GetMaterialRequestObject;
import com.example.e_advisor.utils.APIAddress;
import com.example.e_advisor.utils.AsyncGetTask;
import com.example.e_advisor.utils.AsyncPostTask;
import com.example.e_advisor.utils.MaterialAdapter;
import com.example.e_advisor.utils.Token;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Material extends AppCompatActivity {
    private String course;
    private Dialog progress_dialog;
    private Button getMaterialButton;
    private RecyclerView materialRecycleView;
    private Spinner courseSpinner;
    private final APIAddress apiAddress = new APIAddress();
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_material);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        courseSpinner = findViewById(R.id.courseSpinner);
        materialRecycleView = findViewById(R.id.materialRecycleView);
        getMaterialButton = findViewById(R.id.getMaterialButton);


        //Set content view to the dialog
        progress_dialog = new Dialog(this);
        progress_dialog.setContentView(R.layout.progress_dialog_view);
        progress_dialog.setCancelable(false);

        //Manage material recycle view Layout
        materialRecycleView.setLayoutManager(new LinearLayoutManager(this));

        getMaterialButton.setOnClickListener(view -> {
            //get the selected item from the spinner
            course = courseSpinner.getSelectedItem().toString().trim();
            if (course.equals("-select course-")){
                errorMessage("Please select a course");
                return;
            }
            AsyncPostTask<GetMaterialRequestObject> asyncPostTask = getMaterialTask();
            asyncPostTask.execute(apiAddress.api_address()+"/api/auth/learning-material/material");
        });

        //Populate the courses spinner
        AsyncGetTask asyncGetTask = getAsyncGetTask();
        asyncGetTask.execute(apiAddress.api_address()+"/api/auth/learning-material/courses");
    }

    @NonNull
    private AsyncGetTask getAsyncGetTask() {
        String token = Token.getInstance().getToken();

        // handle response
        // handle error
        return new AsyncGetTask(
                token,
                new AsyncGetTask.TaskCallback() {
                    @Override
                    public void onSuccess(String response) {

                        MaterialCoursesResponse apiResponse = gson.fromJson(response, MaterialCoursesResponse.class);

                        if (apiResponse != null && apiResponse.getCourses() != null) {
                            List<String> uniqueCourses = new ArrayList<>(Arrays.asList(apiResponse.getCourses()));
                            if (!uniqueCourses.isEmpty()) {
                                Collections.sort(uniqueCourses);
                                uniqueCourses.add(0, "-select course-");
                            } else {
                                uniqueCourses.add("-select course-");
                            }

                            // Convert List<String> to String[] if needed
                            String[] coursesResponseList = uniqueCourses.toArray(new String[0]);

                            // Use the coursesResponseList to populate a spinner
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(Material.this, android.R.layout.simple_spinner_item, coursesResponseList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            courseSpinner.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onError(int code, String errorMessage) {
                        // handle error
                    }
                },
                progress_dialog,
                getMaterialButton
        );
    }

    private AsyncPostTask<GetMaterialRequestObject> getMaterialTask() {
        GetMaterialRequestObject getMaterialRequestObject = new GetMaterialRequestObject();
        getMaterialRequestObject.setCourse(course);
        return new AsyncPostTask<>(
                getMaterialRequestObject,
                Token.getInstance().getToken(),
                new AsyncPostTask.TaskCallback() {
                    @Override
                    public void onSuccess(String response) {
                        MaterialResponse materialResponse = gson.fromJson(response, MaterialResponse.class);
                        MaterialAdapter adapter = new MaterialAdapter(materialResponse.getMaterials(), material -> {
                            Intent materialDetailsIntent = new Intent(Material.this, MaterialDetails.class);
                            materialDetailsIntent.putExtra("material", material);
                            startActivity(materialDetailsIntent);
                        });
                        materialRecycleView.setAdapter(adapter);
                    }

                    @Override
                    public void onError(int code, String errorMessage) {
                    errorMessage(code + ": "+errorMessage);
                    }
                },
                progress_dialog,
                getMaterialButton
        );
    }

    public void errorMessage(String msg){
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Error").setContentText(msg)
                .show();
    }
}