package com.example.e_advisor;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.e_advisor.utils.APIAddress;
import com.example.e_advisor.response_objects.ApiResponse;
import com.example.e_advisor.utils.AsyncPostTask;
import com.example.e_advisor.resquest_objects.UserRegData;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterActivity extends AppCompatActivity {
    private Spinner spinnerAcademicLevel;
    private ChipGroup chipGroup;
    private final List<String> selectedInterests = new ArrayList<>();
    private EditText txt_full_name, txt_email_address, txt_password;
    private String name, email, password, academicLevel;
    private Dialog progress_dialog;
    private final APIAddress apiAddress = new APIAddress();
    Button submit_reg;
    Gson gson = new Gson();
    List<EditText> inputFields = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //get view components
        chipGroup = findViewById(R.id.chip_group);
        txt_full_name = findViewById(R.id.editFullName);
        txt_email_address = findViewById(R.id.editEmail);
        txt_password = findViewById(R.id.editPassword);
        submit_reg= findViewById(R.id.btnRegister);

        //Set content view to the dialog
        progress_dialog = new Dialog(this);
        progress_dialog.setContentView(R.layout.progress_dialog_view);
        progress_dialog.setCancelable(false);


        //add input fields into inputFields array
        inputFields.add(txt_full_name);
        inputFields.add(txt_email_address);
        inputFields.add(txt_password);

        String[] interests = {"Maths", "ICT", "Electronic", "Business", "Accounting", "Engineering", "Management", "Health"};

        for (String interest : interests) {
            Chip chip = new Chip(this);
            chip.setText(interest);
            chip.setCheckable(true);
            chip.setChipBackgroundColorResource(R.color.chip_background_selector);
            chipGroup.addView(chip);
        }

        spinnerAcademicLevel = findViewById(R.id.spinnerAcademicLevel);
        // Populate Academic Level Spinner
        ArrayAdapter<CharSequence> levelAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.academic_levels_array,
                android.R.layout.simple_spinner_item
        );
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAcademicLevel.setAdapter(levelAdapter);

        submit_reg.setOnClickListener(v -> {
            //Check for empty fields
            for (EditText field : inputFields){
                if(field.getText().toString().trim().isEmpty()){
                    field.setError("This field is required");
                    return;
                }
            }
            //check if the user select a value from the spinner
            if (spinnerAcademicLevel.getSelectedItem().toString().equals("-select academic level-")){
                Toast.makeText(this, "Please select academic level", Toast.LENGTH_SHORT).show();
                TextView errorText = (TextView) spinnerAcademicLevel.getSelectedView();
                errorText.setError("");
                errorText.setTextColor(Color.RED);
                errorText.setText(R.string.select_academic);
                return;
            }

            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) chipGroup.getChildAt(i);
                if (chip.isChecked()) {
                    selectedInterests.add(chip.getText().toString());
                }
            }

            //collect user inputs
            name = txt_full_name.getText().toString().trim();
            email = txt_email_address.getText().toString().trim();
            password = txt_password.getText().toString().trim();
            academicLevel = spinnerAcademicLevel.getSelectedItem().toString().trim();

            String[] selectedInterestList = selectedInterests.toArray(new String[0]);

            if (selectedInterestList.length == 0){
                Toast.makeText(this, "Please select an interest", Toast.LENGTH_SHORT).show();
                return;
            }

            //Check valid email address
            if (!validateEmailAddress(email)){
                Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
                txt_email_address.setError("Invalid email address");
                return;
            }

            //check password integrity
            if (!password_integrity(password)){
                Toast.makeText(this, "Password must contain a letter, digit, special character and length 8", Toast.LENGTH_SHORT).show();
                txt_password.setError("Password is weak");
                return;
            }

            AsyncPostTask<UserRegData> task = getUserRegDataAsyncPostTask(selectedInterestList);
            task.execute(apiAddress.api_address()+"/api/auth/register");
        });
    }

    @NonNull
    private AsyncPostTask<UserRegData> getUserRegDataAsyncPostTask(String[] selectedInterestList) {
        UserRegData userRegData = new UserRegData(name, email, password, academicLevel, selectedInterestList);
        return new AsyncPostTask<>(
                userRegData,
                null,
                new AsyncPostTask.TaskCallback() {
                    @Override
                    public void onSuccess(String responseMessage) {
                        // Handle success
                        ApiResponse response = gson.fromJson(responseMessage, ApiResponse.class);
                        // Handle error
                        successMessage(response.getMessage());
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
                submit_reg
        );
    }

    //test password integrity LETTERS, SPECIAL CHARACTERS, NUMBER and length 8
    public static boolean password_integrity(String password){
        if (password.length()>=8){
            Pattern letter = Pattern.compile("[a-zA-z]");
            Pattern digits = Pattern.compile("[0-9]");
            Pattern special = Pattern.compile("[!@#$%^&*()_+=|<>?{}\\[\\]`~]");

            Matcher hasLetter = letter.matcher(password);
            Matcher hasDigits = digits.matcher(password);
            Matcher hasSpecial = special.matcher(password);
            return hasLetter.find() && hasDigits.find() && hasSpecial.find();
        }else{
            return false;
        }
    }

    //Check username integrity
    public static boolean username_integrity(String username){
        if (username.length()>=5){
            Pattern special = Pattern.compile("[!.@#$%^&*()+=|<>{}\\[\\]`~]");
            Matcher hasSpecial = special.matcher(username);
            return hasSpecial.find();
        }else{
            return false;
        }
    }

    //validate email address
    public boolean validateEmailAddress(String userEmail){
        return Patterns.EMAIL_ADDRESS.matcher(userEmail).matches();
    }

    //Success Alert
    public void successMessage(String msg){
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE).setTitleText("Success").setContentText(msg)
                .setConfirmButton("OK", sweetAlertDialog -> finish())
                .show();
    }

    //Error Alert
    public void errorMessage(String msg){
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Error").setContentText(msg)
                .show();
    }
}