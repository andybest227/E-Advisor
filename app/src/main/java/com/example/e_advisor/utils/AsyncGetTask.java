package com.example.e_advisor.utils;

import android.app.Dialog;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncGetTask {
    public interface TaskCallback {
        void onSuccess(String response);
        void onError(int code, String errorMessage);
    }

    private final String authToken;
    private final TaskCallback callback;

    private final WeakReference<Dialog> progressBarRef;
    private final WeakReference<Button> buttonRef;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public AsyncGetTask(
            String authToken,
            TaskCallback callback,
            Dialog progressBar,
            Button button
    ) {
        this.authToken = authToken;
        this.callback = callback;
        this.progressBarRef = new WeakReference<>(progressBar);
        this.buttonRef = new WeakReference<>(button);
    }

    public void execute(final String urlString) {
        if (progressBarRef.get() != null) progressBarRef.get().show();
        if (buttonRef.get() != null) buttonRef.get().setEnabled(false);

        executor.execute(() -> {
            int responseCode = 0;
            StringBuilder responseBuilder = new StringBuilder();

            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + authToken);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                responseCode = conn.getResponseCode();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        (responseCode == 200)
                                ? conn.getInputStream()
                                : conn.getErrorStream()
                ));

                String line;
                while ((line = reader.readLine()) != null) responseBuilder.append(line);
                reader.close();

            } catch (IOException e) {
                responseBuilder.append("Network error: ").append(e.getMessage());
            }

            int finalResponseCode = responseCode;
            String responseText = responseBuilder.toString();

            handler.post(() -> {
                if (progressBarRef.get() != null) progressBarRef.get().dismiss();
                if (buttonRef.get() != null) buttonRef.get().setEnabled(true);

                if (finalResponseCode == HttpURLConnection.HTTP_OK) {
                    callback.onSuccess(responseText);
                } else {
                    callback.onError(finalResponseCode, responseText);
                }
            });
        });
    }
}
