package edu.northeastern.numad23sp_team7.huskymarket.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import edu.northeastern.numad23sp_team7.databinding.ActivityChatBinding;
import edu.northeastern.numad23sp_team7.huskymarket.adapter.ChatAdapter;
import edu.northeastern.numad23sp_team7.huskymarket.model.ChatMessage;
import edu.northeastern.numad23sp_team7.huskymarket.model.RecentMessage;
import edu.northeastern.numad23sp_team7.huskymarket.model.User;
import edu.northeastern.numad23sp_team7.huskymarket.utils.Constants;
import edu.northeastern.numad23sp_team7.huskymarket.utils.FCMApiClient;
import edu.northeastern.numad23sp_team7.huskymarket.utils.FCMApiService;
import edu.northeastern.numad23sp_team7.huskymarket.utils.ImageCodec;
import edu.northeastern.numad23sp_team7.huskymarket.utils.PreferenceManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private User receiver;
    private String senderId;
    private String senderName;
    private String senderProfileImage;

    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;

    private FirebaseFirestore database;


    private String recentMessageId = null;

    private static final String TAG = "chat-activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // TODO connect with other fragments here:
        // get receiver from former activity: message fragment, product detail, notification
        receiver = (User) getIntent().getSerializableExtra("user");
        binding.textReceiverName.setText(receiver.getUsername());

        // init
        database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();

        senderId = preferenceManager.getString(Constants.KEY_USER_ID);
        senderName = preferenceManager.getString(Constants.KEY_USERNAME);
        senderProfileImage = preferenceManager.getString(Constants.KEY_PROFILE_IMAGE);
        chatAdapter = new ChatAdapter(
                chatMessages,
                ImageCodec.getDecodedImage(senderProfileImage),
                ImageCodec.getDecodedImage(receiver.getProfileImage()),
                senderId);
        binding.chatRecyclerView.setAdapter(chatAdapter);


        // click back and go back to message fragment
        binding.imageBack.setOnClickListener(v -> onBackPressed());

        // click send and write new message to database
        binding.layoutSend.setOnClickListener(v -> sendMessage());

        // listen to chat collection change -> view
        // current user as sender
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiver.getId())
                .addSnapshotListener(eventListener);
        // current user as receiver
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, receiver.getId())
                .whereEqualTo(Constants.KEY_RECEIVER_ID, senderId)
                .addSnapshotListener(eventListener);
    }

    private void sendMessage() {
        ChatMessage chatMessage = new ChatMessage(senderId, receiver.getId(), binding.inputMessage.getText().toString(), new Date());
        database.collection(Constants.KEY_COLLECTION_CHAT).add(chatMessage);
        RecentMessage recentMessage = new RecentMessage(
                senderId, senderName, senderProfileImage,
                receiver.getId(), receiver.getUsername(), receiver.getProfileImage(),
                binding.inputMessage.getText().toString(), new Date());

        if (recentMessageId == null) {
            database.collection(Constants.KEY_COLLECTION_RECENT_MESSAGE)
                    .add(recentMessage)
                    .addOnSuccessListener(documentReference -> recentMessageId = documentReference.getId())
                    .addOnFailureListener(e -> Log.w("Fail to add recent message.", e));
        } else {
            DocumentReference documentReference =
                    database.collection(Constants.KEY_COLLECTION_RECENT_MESSAGE).document(recentMessageId);
            documentReference.update(
                    Constants.KEY_LAST_MESSAGE, binding.inputMessage.getText().toString(),
                    Constants.KEY_TIMESTAMP, new Date()
            );
        }
        if (receiver.getFcmToken() != null) {
            try {
                JSONArray tokens = new JSONArray();
                tokens.put(receiver.getFcmToken());

                JSONObject data = new JSONObject();
                data.put(Constants.KEY_USER_ID, senderId);
                data.put(Constants.KEY_USERNAME, senderName);
                data.put(Constants.KEY_FCM_TOKEN, receiver.getFcmToken());
                data.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());

                JSONObject messageBody = new JSONObject();
                messageBody.put(Constants.KEY_DATA, data);
                messageBody.put(Constants.KEY_REGISTRATION_IDS, tokens);
                sendNotification(messageBody.toString());

            } catch (Exception e) {
                Log.d(TAG, "sendMessage: " + e.getMessage());
            }
        }

        binding.inputMessage.setText(null);


    }


    private void sendNotification(String messageBody) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(Constants.KEY_AUTHORIZATION, Constants.VALUE_AUTHORIZATION);
        headers.put(Constants.KEY_CONTENT_TYPE, Constants.VALUE_CONTENT_TYPE);

        FCMApiClient.getClient()
                .create(FCMApiService.class)
                .sendMessage(headers, messageBody)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "onResponse: " + "Notification sent successful");
                        } else {
                            Log.d(TAG, "onResponse: " + "Error: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getMessage());
                    }
                });
    }


    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            int count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setSenderId(documentChange.getDocument().getString(Constants.KEY_SENDER_ID));
                    chatMessage.setReceiverId(documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID));
                    chatMessage.setMessage(documentChange.getDocument().getString(Constants.KEY_MESSAGE));
                    chatMessage.setTimestamp(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages, (c1, c2) -> c1.getTimestamp().compareTo(c2.getTimestamp()));
            if (count == 0) {
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }

            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);
        if (recentMessageId == null) {
            checkRecentMessage(senderId, receiver.getId());
            checkRecentMessage(receiver.getId(), senderId);
        }
    };


    // check if recentMessage collection has had a recent conversation
    private void checkRecentMessage(String senderId, String receiverId) {
        database.collection(Constants.KEY_COLLECTION_RECENT_MESSAGE)
                .whereEqualTo(Constants.KEY_USER1_ID, senderId)
                .whereEqualTo(Constants.KEY_USER2_ID, receiverId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                            recentMessageId = task.getResult().getDocuments().get(0).getId();
                        }
                    } else {
                        Log.d(TAG, "cannot find a recent message");
                    }
                });
    }


}