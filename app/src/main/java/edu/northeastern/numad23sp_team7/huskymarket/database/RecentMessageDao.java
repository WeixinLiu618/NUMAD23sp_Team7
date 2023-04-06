package edu.northeastern.numad23sp_team7.huskymarket.database;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import edu.northeastern.numad23sp_team7.huskymarket.model.RecentMessage;
import edu.northeastern.numad23sp_team7.huskymarket.utils.Constants;

public class RecentMessageDao {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference recentMessageRef = db.collection(Constants.KEY_COLLECTION_RECENT_MESSAGE);
    private final static String TAG = "recentMessageDAO";

    public void updateProfileImage(String userId, String encodedImage) {
        Query q1 = recentMessageRef.whereEqualTo(Constants.KEY_USER1_ID, userId);
        q1.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for(QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                    recentMessageRef.document(documentSnapshot.getId())
                            .update(Constants.KEY_USER1_IMAGE, encodedImage)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "User1 Image successfully updated!"))
                            .addOnFailureListener(e -> Log.w(TAG, "updateProfileImage: ", e));
                }
            }else {
                Log.d(TAG, "updateProfileImage: ", task.getException());
            }
        });

        Query q2 = recentMessageRef.whereEqualTo(Constants.KEY_USER2_ID, userId);
        q2.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for(QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                    recentMessageRef.document(documentSnapshot.getId())
                            .update(Constants.KEY_USER2_IMAGE, encodedImage)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "User2 Image successfully updated!"))
                            .addOnFailureListener(e -> Log.w(TAG, "updateProfileImage: ", e));
                }
            }else {
                Log.d(TAG, "updateProfileImage: ", task.getException());
            }
        });
    }


}