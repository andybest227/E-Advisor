package com.example.e_advisor;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_advisor.response_objects.CareerResponseObject;
import com.example.e_advisor.response_objects.CoursesApiResponse;
import com.example.e_advisor.response_objects.QualificationsResponse;
import com.example.e_advisor.resquest_objects.CareerRequestObject;
import com.example.e_advisor.utils.APIAddress;
import com.example.e_advisor.utils.AsyncGetTask;
import com.example.e_advisor.utils.AsyncPostTask;
import com.example.e_advisor.utils.CareerAdapter;
import com.example.e_advisor.utils.Token;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CareerGuide extends AppCompatActivity {
    private Spinner spinnerQualification, spinnerCourseOfStudy;
    private Dialog progress_dialog;
    private Button submitBtn;
    private  String courseOfStudy, qualification;
    private RecyclerView careerRecycleView;
    private final APIAddress apiAddress = new APIAddress();
    private final Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_career_guide);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerQualification = findViewById(R.id.qualifications);
        spinnerCourseOfStudy = findViewById(R.id.courseOfStudy);
        submitBtn = findViewById(R.id.btnCareer);
        careerRecycleView = findViewById(R.id.careerRecycleView);


        //Set content view to the dialog
        progress_dialog = new Dialog(this);
        progress_dialog.setContentView(R.layout.progress_dialog_view);
        progress_dialog.setCancelable(false);

        //Manage Career recycle view Layout
        careerRecycleView.setLayoutManager(new LinearLayoutManager(this));

        AsyncGetTask getTask = getAsyncGetTask();
        AsyncGetTask getQualifications = getAsyncQualifications();

        getQualifications.execute(apiAddress.api_address()+"/api/auth/career/qualifications");
        getTask.execute(apiAddress.api_address()+"/api/auth/career/course_of_study");

        submitBtn.setOnClickListener(view -> {
            //check if the user select a value from the spinner
            if (spinnerQualification.getSelectedItem().toString().equals("-select qualification-")){
                Toast.makeText(this, "Please select academic qualification", Toast.LENGTH_SHORT).show();
                TextView errorText = (TextView) spinnerQualification.getSelectedView();
                errorText.setError("");
                errorText.setTextColor(Color.RED);
                errorText.setText(R.string.select_qualification);
                return;
            }
            if (spinnerCourseOfStudy.getSelectedItem().toString().equals("-select course of study-")){
                Toast.makeText(this, "Please select course of study", Toast.LENGTH_SHORT).show();
                TextView errorText = (TextView) spinnerCourseOfStudy.getSelectedView();
                errorText.setError("");
                errorText.setTextColor(Color.RED);
                errorText.setText(R.string.courseOfStudy);
                return;
            }

            courseOfStudy = spinnerCourseOfStudy.getSelectedItem().toString().trim();
            qualification = spinnerQualification.getSelectedItem().toString().trim();
            AsyncPostTask<CareerRequestObject> asyncPostTask = asyncPostTask();
            asyncPostTask.execute(apiAddress.api_address()+"/api/auth/career/course");

        });

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
                        CoursesApiResponse apiResponse = gson.fromJson(response, CoursesApiResponse.class);

                        if (apiResponse != null && apiResponse.getCoursesResponse() != null && !apiResponse.getCoursesResponse().isEmpty()) {
                            List<String> uniqueCourses = apiResponse.getCoursesResponse().get(0).getUniquecourses();

                            if (!uniqueCourses.isEmpty()){
                                Collections.sort(uniqueCourses);
                                uniqueCourses.add(0, "-select course of study-");
                            }else{
                                uniqueCourses.add("-select course of study-");
                            }

                            // Convert List<String> to String[] if needed
                            String[] coursesResponseList = uniqueCourses.toArray(new String[0]);

                            // Use the coursesResponseList to populate a spinner
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(CareerGuide.this, android.R.layout.simple_spinner_item, coursesResponseList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCourseOfStudy.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onError(int code, String errorMessage) {
                        // handle error
                    }
                },
                progress_dialog,
                submitBtn
        );
    }

    private AsyncGetTask getAsyncQualifications() {
        String token = Token.getInstance().getToken();

        // handle response
        // handle error
        return new AsyncGetTask(
                token,
                new AsyncGetTask.TaskCallback() {
                    @Override
                    public void onSuccess(String response) {
                        QualificationsResponse apiResponse = gson.fromJson(response, QualificationsResponse.class);

                        if (apiResponse != null && apiResponse.getQualifications() != null && !apiResponse.getQualifications().isEmpty()) {
                            List<String> uniqueQualifications = apiResponse.getQualifications().get(0).getUniquequalifications();

                            if (!uniqueQualifications.isEmpty()){
                                Collections.sort(uniqueQualifications);
                                uniqueQualifications.add(0, "-select qualification-");
                            }else{
                                uniqueQualifications.add("-select qualification-");
                            }

                            // Convert List<String> to String[] if needed
                            String[] qualificationResponseList = uniqueQualifications.toArray(new String[0]);

                            // Use the coursesResponseList to populate a spinner
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(CareerGuide.this, android.R.layout.simple_spinner_item, qualificationResponseList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerQualification.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onError(int code, String errorMessage) {
                        // handle error
                    }
                },
                progress_dialog,
                submitBtn
        );
    }

    private AsyncPostTask<CareerRequestObject> asyncPostTask(){
        CareerRequestObject careerRequestObject = new CareerRequestObject(courseOfStudy, qualification);
        return new AsyncPostTask<>(
                careerRequestObject,
                Token.getInstance().getToken(),
                new AsyncPostTask.TaskCallback() {
                    @Override
                    public void onSuccess(String response) {
                        CareerResponseObject careerResponseObject = gson.fromJson(response, CareerResponseObject.class);
                        CareerAdapter careerAdapter = new CareerAdapter(careerResponseObject, career -> {
                            Intent careerDetails = new Intent(getApplicationContext(), CareerDetails.class);
                            careerDetails.putExtra("career", career);
                            startActivity(careerDetails);
                        });
                        careerRecycleView.setAdapter(careerAdapter);
                    }

                    @Override
                    public void onError(int code, String errorMessage) {
                        errorMessage("5000: Internal Server Error");
                        System.out.println(errorMessage);
                    }
                },
                progress_dialog,
                submitBtn
        );

    }

    //Error Alert
    public void errorMessage(String msg){
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Error").setContentText(msg)
                .show();
    }
}