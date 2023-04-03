package edu.northeastern.numad23sp_team7.huskymarket.database;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.northeastern.numad23sp_team7.huskymarket.model.ChatMessage;
import edu.northeastern.numad23sp_team7.huskymarket.utils.Constants;

public class ChatDao {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference chatRef = db.collection(Constants.KEY_COLLECTION_CHAT);
    private final static String TAG = "chatDao";

    public void addMessage(ChatMessage chatMessage) {
        chatRef.add(chatMessage)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Message added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding message", e));
    }

}
