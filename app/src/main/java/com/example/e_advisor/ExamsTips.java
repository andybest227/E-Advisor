package com.example.e_advisor;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_advisor.response_objects.TipsApiResponse;
import com.example.e_advisor.utils.APIAddress;
import com.example.e_advisor.response_objects.ApiResponse;
import com.example.e_advisor.utils.AsyncGetTask;
import com.example.e_advisor.utils.AsyncPostTask;
import com.example.e_advisor.response_objects.ExamTipApiResponse;
import com.example.e_advisor.response_objects.TagsObject;
import com.example.e_advisor.utils.TipAdapter;
import com.example.e_advisor.utils.Token;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ExamsTips extends AppCompatActivity {
    private final APIAddress apiAddress = new APIAddress();
    private ChipGroup chipGroup;
    private Button btnFetchTags;
    private Dialog progress_dialog;
    private final List<String> selectedTags = new ArrayList<>();
    private String [] selectedTagsList;
    RecyclerView recyclerView;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exams_tips);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        chipGroup = findViewById(R.id.tags_chip_group);
        btnFetchTags = findViewById(R.id.getTags);
        recyclerView = findViewById(R.id.recyclerViewTips);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        //Set content view to the dialog
        progress_dialog = new Dialog(this);
        progress_dialog.setContentView(R.layout.progress_dialog_view);
        progress_dialog.setCancelable(false);
        AsyncGetTask getTask = getAsyncGetTask();
        getTask.execute(apiAddress.api_address()+"/api/auth/exams-tip/tags");

        btnFetchTags.setOnClickListener(view -> {
            recyclerView.setAdapter(null);
            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) chipGroup.getChildAt(i);
                if (chip.isChecked()) {
                    selectedTags.add(chip.getText().toString());
                }
            }

            selectedTagsList = selectedTags.toArray(new String[0]);

            if (selectedTagsList.length == 0){
                errorMessage("Please select an interest");
                return;
            }
            AsyncPostTask<String[]> asyncGetTipsTask = asyncPostTask();
            asyncGetTipsTask.execute(apiAddress.api_address()+"/api/auth/exams-tip/tips");
            selectedTags.clear();
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
                        TagsObject apiResponse = gson.fromJson(response, TagsObject.class);
                        String [] tags = apiResponse.getTags();
                        for (String tag : tags) {
                            Chip chip = new Chip(ExamsTips.this);
                            chip.setText(tag);
                            chip.setCheckable(true);
                            chip.setChipBackgroundColorResource(R.color.chip_background_selector);
                            chipGroup.addView(chip);
                        }
                    }

                    @Override
                    public void onError(int code, String errorMessage) {
                        // handle error
                    }
                },
                progress_dialog,
                btnFetchTags
        );
    }

    @NonNull
    private AsyncPostTask<String []> asyncPostTask(){
        String token = Token.getInstance().getToken();
        return new AsyncPostTask<>(
                selectedTagsList,
                token,
                new AsyncPostTask.TaskCallback() {
                    @Override
                    public void onSuccess(String responseMessage) {
                        // Handle success
                        try {
                            if (responseMessage != null && responseMessage.trim().startsWith("{")) {
                                TipsApiResponse tipsResponse = gson.fromJson(responseMessage, TipsApiResponse.class);
                                String[] contents = tipsResponse.getResult().getContents();
                                List<ExamTipApiResponse> examTips = new ArrayList<>();
                                for (String content : contents) {
                                    examTips.add(new ExamTipApiResponse(content));
                                }
                                TipAdapter adapter = new TipAdapter(examTips);
                                recyclerView.setAdapter(adapter);
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
                        ApiResponse response = gson.fromJson(errorMessage, ApiResponse.class);
                        // Handle error
                        errorMessage(response.getMessage());
                        System.out.println(code +": "+ response.getMessage());
                        selectedTagsList = null;
                    }
                },
                progress_dialog,
                btnFetchTags
        );
    }

    //Error Alert
    public void errorMessage(String msg){
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Error").setContentText(msg)
                .show();
    }
}