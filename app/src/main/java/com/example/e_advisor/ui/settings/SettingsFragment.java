package com.example.e_advisor.ui.settings;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.auth0.android.jwt.JWT;
import com.example.e_advisor.R;
import com.example.e_advisor.databinding.FragmentSettingsBinding;
import com.example.e_advisor.response_objects.ResetPasswordResponse;
import com.example.e_advisor.resquest_objects.PasswordResetRequestObject;
import com.example.e_advisor.utils.APIAddress;
import com.example.e_advisor.utils.AsyncPostTask;
import com.example.e_advisor.utils.Token;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private EditText oldPassword, new_password;
    List<EditText> editTextList = new ArrayList<>();
    private Button submit;
    private Dialog progress_dialog;
    private String old_Password, newPassword, userId;
    Gson gson = new Gson();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        oldPassword = root.findViewById(R.id.oldPassword);
        new_password = root.findViewById(R.id.new_password);
        submit = root.findViewById(R.id.submit);

        editTextList.add(oldPassword);
        editTextList.add(new_password);

        progress_dialog = new Dialog(requireActivity());
        progress_dialog.setContentView(R.layout.progress_dialog_view);
        progress_dialog.setCancelable(false);


        APIAddress apiAddress = new APIAddress();
        String url = apiAddress.api_address();
        String token = Token.getInstance().getToken();

        JWT jwt = new JWT(token);

        userId = jwt.getClaim("_id").asString();

        submit.setOnClickListener(view -> {
            for (EditText editText : editTextList){
                if (editText.getText().toString().isEmpty()){
                    editText.setError("Field required");
                    return;
                }
            }
            old_Password = oldPassword.getText().toString().trim();
            newPassword = new_password.getText().toString().trim();
            PasswordResetRequestObject pwdObject = new PasswordResetRequestObject(userId, old_Password, newPassword);
            new AsyncPostTask<>(
                    pwdObject,
                    Token.getInstance().getToken(),
                    new AsyncPostTask.TaskCallback(){

                        @Override
                        public void onSuccess(String response) {
                            ResetPasswordResponse resetPasswordResponse = gson.fromJson(response, ResetPasswordResponse.class);
                            successMessage(resetPasswordResponse.getMessage());
                            oldPassword.setText("");
                            new_password.setText("");
                        }

                        @Override
                        public void onError(int code, String errorMessage) {
                            try {
                                // Try to parse it as JSON
                                if (errorMessage != null && errorMessage.trim().startsWith("{")) {
                                    ResetPasswordResponse response = gson.fromJson(errorMessage, ResetPasswordResponse.class);
                                    errorMessage(response.getMessage());
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
                    submit
            ).execute(url+"/api/auth/reset");
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void errorMessage(String msg){
        new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE).setTitleText("Error").setContentText(msg)
                .show();
    }
    public void successMessage(String msg){
        new SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE).setTitleText("Success").setContentText(msg)
                .show();
    }
}