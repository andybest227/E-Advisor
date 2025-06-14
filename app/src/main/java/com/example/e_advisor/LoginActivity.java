package com.example.e_advisor;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.e_advisor.response_objects.LoginApiResponse;
import com.example.e_advisor.utils.APIAddress;
import com.example.e_advisor.response_objects.ApiResponse;
import com.example.e_advisor.utils.AsyncPostTask;
import com.example.e_advisor.resquest_objects.LoginUserObject;
import com.example.e_advisor.utils.Token;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText txt_email, txt_password;
    String email, password;
    private Dialog progress_dialog;
    private final APIAddress apiAddress = new APIAddress();
    List<EditText> inputFields = new ArrayList<>();
    Button login_btn;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "e_advisor";
    private static final String TOKEN_KEY = "token";
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        TextView register = findViewById(R.id.btnRegister);
        login_btn = findViewById(R.id.login_btn);
        txt_email = findViewById(R.id.username_field);
        txt_password = findViewById(R.id.password_field);

        //Set content view to the dialog
        progress_dialog = new Dialog(this);
        progress_dialog.setContentView(R.layout.progress_dialog_view);
        progress_dialog.setCancelable(false);

        inputFields.add(txt_email);
        inputFields.add(txt_password);

        sharedPreferences = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        //Handle registration
        login_btn.setOnClickListener(view -> {

            //Check for empty fields
            for (EditText field : inputFields) {
                if (field.getText().toString().trim().isEmpty()) {
                    field.setError("This field is required");
                    return;
                }
            }

            email = txt_email.getText().toString().trim();
            password = txt_password.getText().toString().trim();

            AsyncPostTask<LoginUserObject> task = getUserLoginDataAsyncPostTask();
            task.execute(apiAddress.api_address()+"/api/auth/login");
        });

        //Navigate to register activity
        register.setOnClickListener(v -> {
            Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
        });

        String token = getToken(this);
        if (token != null) {
            progress_dialog.show();
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(apiAddress.api_address()+"/api/auth/user")
                    .addHeader("Authorization", "Bearer " + token)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    progress_dialog.dismiss();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> {
                            Token.getInstance().setToken(token);
                            progress_dialog.dismiss();
                            // Token is valid - go to dashboard
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> progress_dialog.dismiss());
                    }
                }
            });
        }

    }

    @NonNull
    private AsyncPostTask<LoginUserObject> getUserLoginDataAsyncPostTask() {
        LoginUserObject loginUserObject = new LoginUserObject(email, password);
        return new AsyncPostTask<>(
                loginUserObject,
                null,
                new AsyncPostTask.TaskCallback(){
            @Override
            public void onSuccess(String response) {
                try {
                    if (response != null && response.trim().startsWith("{")) {
                        
                        LoginApiResponse apiResponse = gson.fromJson(response, LoginApiResponse.class);
                        Token.getInstance().setToken(apiResponse.getToken());

                        //Save token to share preference
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(TOKEN_KEY, apiResponse.getToken());
                        editor.apply();

                        Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(homeIntent);
                    } else {
                        errorMessage("Unable to connect to server");
                    }

                } catch (JsonSyntaxException e) {
                    Log.e("JSON_PARSE_ERROR", "Invalid JSON: " + e.getMessage());
                    errorMessage("Unable to connect to server");
                }
            }

                    @Override
                    public void onError(int code, String errorMessage) {
                        try {
                            // Try to parse it as JSON
                            if (errorMessage != null && errorMessage.trim().startsWith("{")) {
                                ApiResponse response = gson.fromJson(errorMessage, ApiResponse.class);
                                errorMessage(response.getMessage());
                                System.out.println(code + ": " + response.getMessage());
                            } else {
                                // Not a JSON string
                                errorMessage(errorMessage != null ? errorMessage : "Unknown error");
                                System.out.println(code + ": " + errorMessage);
                            }
                        } catch (JsonSyntaxException e) {
                            errorMessage("Server not reachable"+ e.getLocalizedMessage());
                        }
                    }


                },
                progress_dialog,
                login_btn
                );}
    public void errorMessage(String msg){
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Error").setContentText(msg)
                .show();
    }

    //Get username from shared preference
    private String getToken(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(TOKEN_KEY, "");
    }
}