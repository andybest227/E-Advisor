package com.example.e_advisor.ui.chat;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.auth0.android.jwt.JWT;
import com.example.e_advisor.R;
import com.example.e_advisor.databinding.FragmentChatBinding;
import com.example.e_advisor.response_objects.Message;
import com.example.e_advisor.response_objects.MessageObject;
import com.example.e_advisor.resquest_objects.MessageRequest;
import com.example.e_advisor.utils.APIAddress;
import com.example.e_advisor.utils.AdminConversationAdapter;
import com.example.e_advisor.utils.AsyncGetTask;
import com.example.e_advisor.utils.AsyncPostTask;
import com.example.e_advisor.utils.ConversationItem;
import com.example.e_advisor.utils.FetchMessageRequestObject;
import com.example.e_advisor.utils.MessageAdapter;
import com.example.e_advisor.utils.SocketManager;
import com.example.e_advisor.utils.Token;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.socket.client.Socket;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;

public class ChatFragment extends Fragment {

    private final APIAddress apiAddress = new APIAddress();
    private EditText chatTextBox;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private final List<Message> messageList = new ArrayList<>();
    private Dialog progress_dialog;
    private Socket socket;
    private String userId;
    private String user2;
    private final Gson gson = new Gson();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        com.example.e_advisor.databinding.FragmentChatBinding binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        root.findViewById(R.id.chat_input).setVisibility(View.GONE);


        String token = Token.getInstance().getToken();
        JWT jwt = new JWT(token);
        userId = jwt.getClaim("_id").asString();
        String role = jwt.getClaim("role").asString();

        assert role != null;
        if (role.equals("admin")) {
            showUserListView(root);
        } else {
            user2 = "681b7b0009547908cd9cb368";
            showChatView(root);
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SocketManager.disconnectSocket();
    }
    private String getCurrentFormattedTime() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalTime now = LocalTime.now();
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("h:mm:ss a");
            return now.format(outputFormatter);
        } else {
            return new SimpleDateFormat("h:mm:ss a", Locale.getDefault()).format(new Date());
        }
    }
    private String formatTimestamp(String isoTimestamp) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Instant instant = Instant.parse(isoTimestamp);
                ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy, hh:mm a");
                return zdt.format(formatter);
            } else {
                // Fallback for older Android versions
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = inputFormat.parse(isoTimestamp);

                SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM d, yyyy, hh:mm a", Locale.getDefault());
                assert date != null;
                return outputFormat.format(date);
            }
        } catch (Exception e) {
            return isoTimestamp; // Return original if parsing fails
        }
    }

    @NonNull
    private AsyncPostTask<FetchMessageRequestObject> fetchMessageTask() {
        FetchMessageRequestObject fetchMessageRequestObject = new FetchMessageRequestObject(userId, user2);
        return new AsyncPostTask<>(
                fetchMessageRequestObject,
                Token.getInstance().getToken(),
                new AsyncPostTask.TaskCallback() {
                    @Override
                    public void onSuccess(String response) {
                        MessageObject messageObject = gson.fromJson(response, MessageObject.class);
                        if (messageObject != null && messageObject.isSuccess() && messageObject.getMessages() != null) {
                            for (Message message : messageObject.getMessages()) {
                                String formattedTime = formatTimestamp(message.getCreatedAt());

                                Message formattedMessage = new Message(
                                        message.getSenderId(),
                                        message.getContent(),
                                        formattedTime
                                );

                                messageList.add(formattedMessage);
                                messageAdapter.notifyItemInserted(messageList.size() - 1);
                            }

                            recyclerView.scrollToPosition(messageList.size() - 1);
                        } else {
                            Log.e("MessageParse", "Invalid or empty message response");
                        }
                    }

                    @Override
                    public void onError(int code, String errorMessage) {
                        errorMessage(errorMessage);
                    }
                },
                progress_dialog,
                null);
    }
    public void errorMessage(String msg){
        new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE).setTitleText("Error").setContentText(msg)
                .show();
    }


    private void showUserListView(View root) {
        RecyclerView userListRecycler = root.findViewById(R.id.recyclerView);
        userListRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        AsyncGetTask userFetchTask = new AsyncGetTask(
                Token.getInstance().getToken(),
                new AsyncGetTask.TaskCallback() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getBoolean("success")) {
                                JSONArray convoys = json.getJSONArray("conversations");
                                List<ConversationItem> conversationList = new ArrayList<>();

                                for (int i = 0; i < convoys.length(); i++) {
                                    JSONObject obj = convoys.getJSONObject(i);
                                    String userId = obj.getString("userId");
                                    String name = obj.getString("name");
                                    String lastMessage = obj.getString("lastMessage");
                                    int unread = obj.getInt("unreadCount");

                                    ConversationItem item = new ConversationItem(userId, name, lastMessage, unread);
                                    conversationList.add(item);
                                }

                                AdminConversationAdapter adapter = new AdminConversationAdapter(getContext(), conversationList, clickedUserId -> {
                                    user2 = clickedUserId;
                                    showChatView(root);
                                    markMessagesAsRead(clickedUserId, userId);
                                });
                                userListRecycler.setAdapter(adapter);
                            } else {
                                errorMessage("No conversations found");
                            }
                        } catch (JSONException e) {
                            errorMessage("Parse error: " + e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onError(int code, String errorMessage) {
                        errorMessage("Failed to load conversations: " + errorMessage);
                        System.out.println(errorMessage);
                    }
                },
                progress_dialog,
                null
        );
        userFetchTask.execute(apiAddress.api_address()+"/api/chat/admin/conversations");
    }

    private void showChatView(View root) {
        BottomNavigationView navigationView = requireActivity().findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setVisibility(View.GONE);
        }
        chatTextBox = root.findViewById(R.id.chat_input);
        ImageButton btn_send = root.findViewById(R.id.btn_send);
        recyclerView = root.findViewById(R.id.recyclerView);

        chatTextBox.setVisibility(View.VISIBLE);

        progress_dialog = new Dialog(requireActivity());
        progress_dialog.setContentView(R.layout.progress_dialog_view);
        progress_dialog.setCancelable(false);

        chatTextBox.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                recyclerView.postDelayed(() -> recyclerView.scrollToPosition(messageList.size() -1), 100);
            }
        });

        messageAdapter = new MessageAdapter(getContext(), messageList, userId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(messageAdapter);

        socket = SocketManager.getSocket();
        if (socket != null) {
            socket.connect();
            socket.emit("register", userId);
        } else {
            Log.e("ChatFragment", "Socket is null. Connection failed.");
            errorMessage("Failed to connect to Web Socket");
        }

        // Fetch messages
        AsyncPostTask<FetchMessageRequestObject> fetchMessages = fetchMessageTask();
        fetchMessages.execute(apiAddress.api_address() + "/api/chat/conversation");

        // Send message
        btn_send.setOnClickListener(view -> {
            String messageText = chatTextBox.getText().toString().trim();
            if (messageText.isEmpty()) return;

            MessageRequest messageRequest = new MessageRequest();
            messageRequest.setSenderId(userId);
            messageRequest.setReceiverId(user2); // dynamic
            messageRequest.setContent(messageText);

            try {
                String json = gson.toJson(messageRequest);
                JSONObject messageData = new JSONObject(json);
                socket.emit("send_message", messageData);
            } catch (JSONException e) {
                errorMessage("Unable to send message: " + e.getLocalizedMessage());
            }

            String formattedTime = getCurrentFormattedTime();
            Message message = new Message(userId, messageText, formattedTime);
            messageList.add(message);
            messageAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerView.scrollToPosition(messageList.size() - 1);

            chatTextBox.setText("");
        });

        socket.on("receive_message", args -> requireActivity().runOnUiThread(() -> {
            try {
                JSONObject data = (JSONObject) args[0];
                String senderId = data.getString("senderId");
                String content = data.getString("content");
                String timestamp = data.getString("timestamp");

                String formattedTime = formatTimestamp(timestamp);

                Message message = new Message(senderId, content, formattedTime);
                messageList.add(message);
                messageAdapter.notifyItemInserted(messageList.size() - 1);
                recyclerView.scrollToPosition(messageList.size() - 1);

            } catch (JSONException e) {
                errorMessage("Unable to receive message: " + e.getLocalizedMessage());
            }
        }));
    }

    private void markMessagesAsRead(String senderId, String receiverId) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("senderId", senderId);
            requestBody.put("receiverId", receiverId);
        } catch (JSONException e) {
            Log.e("MarkRead", "JSON error: " + e.getMessage());
            return;
        }

        new AsyncPostTask<>(
                requestBody,
                Token.getInstance().getToken(),
                new AsyncPostTask.TaskCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Log.d("MarkRead", "Messages marked as read successfully");
                    }

                    @Override
                    public void onError(int code, String errorMessage) {
                        Log.e("MarkRead", "Failed to mark messages as read: " + errorMessage);
                    }
                },
                null,
                null
        ).execute(apiAddress.api_address() + "/api/chat/markAsRead");
    }

}
