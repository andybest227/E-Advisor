package com.example.e_advisor;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.e_advisor.response_objects.ResetPasswordResponse;
import com.example.e_advisor.resquest_objects.CreateCourseRequestObject;
import com.example.e_advisor.utils.APIAddress;
import com.example.e_advisor.utils.AsyncPostTask;
import com.example.e_advisor.utils.Token;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddCourse extends AppCompatActivity {
    private EditText courseTitleEditText, descriptionEditText, tagsEditText;
    private Button submitBtn;
    private final List<EditText> editTextList = new ArrayList<>();

    private String title, description, tags;
    private Dialog progress_dialog;
    Gson gson = new Gson();
    private final APIAddress apiAddress = new APIAddress();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_course);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        courseTitleEditText = findViewById(R.id.courseTitleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        tagsEditText = findViewById(R.id.tagsEditText);
        submitBtn = findViewById(R.id.submit_btn);

        editTextList.add(courseTitleEditText);
        editTextList.add(descriptionEditText);

        progress_dialog = new Dialog(this);
        progress_dialog.setContentView(R.layout.progress_dialog_view);
        progress_dialog.setCancelable(false);


        submitBtn.setOnClickListener(view -> {
            for (EditText editText : editTextList){
                if (editText.getText().toString().trim().isEmpty()){
                    editText.setError("This field is required");
                    return;
                }
            }
            title = courseTitleEditText.getText().toString().trim();
            description = descriptionEditText.getText().toString().trim();
            tags =tagsEditText.getText().toString().trim();

            AsyncPostTask<CreateCourseRequestObject> postTask = asyncPostTask();
            postTask.execute(apiAddress.api_address()+"/api/auth/courses/");
        });
    }


    @NonNull
    private AsyncPostTask<CreateCourseRequestObject> asyncPostTask (){
        CreateCourseRequestObject body = new CreateCourseRequestObject(title, description, tags.split(","));
        editTextList.add(tagsEditText);
        String token = Token.getInstance().getToken();
        return new AsyncPostTask<>(
                body,
                token,
                new AsyncPostTask.TaskCallback() {
                    @Override
                    public void onSuccess(String response) {
                        ResetPasswordResponse resp = gson.fromJson(response, ResetPasswordResponse.class);
                        successMessage(resp.getMessage());
                        for (EditText editText : editTextList){
                            editText.setText("");
                        }
                    }

                    @Override
                    public void onError(int code, String errorMessage) {
                        errorMessage(code+": " + errorMessage);
                    }
                },
                progress_dialog,
                submitBtn
        );
    }

    //Error Alert
    public void successMessage(String msg){
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE).setTitleText("Success").setContentText(msg)
                .show();
    }

    //Error Alert
    public void errorMessage(String msg){
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Error").setContentText(msg)
                .show();
    }
}