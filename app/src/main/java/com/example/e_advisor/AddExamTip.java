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
import com.example.e_advisor.resquest_objects.CreateTipsRequest;
import com.example.e_advisor.utils.APIAddress;
import com.example.e_advisor.utils.AsyncPostTask;
import com.example.e_advisor.utils.Token;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddExamTip extends AppCompatActivity {
    private EditText title, description, tags;
    private final List<EditText> editTextList = new ArrayList<>();
    private Button submit_button;
    private String[] tags_list;
    private String txt_title, txt_description, txt_tags;
    private Dialog progress_dialog;
    private static final APIAddress apiAddress = new APIAddress();
    Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_exam_tip);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        title = findViewById(R.id.tipTitleEditText);
        description = findViewById(R.id.descriptionEditText);
        tags = findViewById(R.id.tagsEditText);
        submit_button = findViewById(R.id.submit_btn);

        editTextList.add(title);
        editTextList.add(description);

        //Set content view to the dialog
        progress_dialog = new Dialog(this);
        progress_dialog.setContentView(R.layout.progress_dialog_view);
        progress_dialog.setCancelable(false);


        submit_button.setOnClickListener(view -> {

            for (EditText editText : editTextList){
                if (editText.getText().toString().isEmpty()){
                    editText.setError("This field is required");
                    return;
                }
            }
            txt_title = title.getText().toString().trim();
            txt_description = description.getText().toString().trim();
            txt_tags = tags.getText().toString().trim();

            tags_list = txt_tags.split(",");

            AsyncPostTask<CreateTipsRequest>  postedTask = postTask();
            postedTask.execute(apiAddress.api_address()+"/api/auth/exams-tip/");
        });
    }

    @NonNull
    private AsyncPostTask<CreateTipsRequest> postTask (){
        String token = Token.getInstance().getToken();
        editTextList.add(tags);
        CreateTipsRequest request = new CreateTipsRequest(txt_title, txt_description, tags_list);
        return  new AsyncPostTask<>(
                request,
                token,
                new AsyncPostTask.TaskCallback() {
                    @Override
                    public void onSuccess(String response) {
                        ResetPasswordResponse backendResponse = gson.fromJson(response, ResetPasswordResponse.class);
                        successMessage(backendResponse.getMessage());
                        for (EditText editText : editTextList){
                            editText.setText("");
                        }
                    }
                    @Override
                    public void onError(int code, String errorMessage) {
                        errorMessage(code +": " +errorMessage);
                    }
                },
                progress_dialog,
                submit_button
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