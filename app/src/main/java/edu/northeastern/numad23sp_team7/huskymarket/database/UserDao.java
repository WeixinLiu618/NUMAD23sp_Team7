package edu.northeastern.numad23sp_team7.huskymarket.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.function.Consumer;

import edu.northeastern.numad23sp_team7.huskymarket.model.User;
import edu.northeastern.numad23sp_team7.huskymarket.utils.Constants;

public class UserDao {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference usersRef = db.collection(Constants.KEY_COLLECTION_USERS);
    private final static String TAG = "userDao";

    public void getUserById(String userId, final Consumer<User> callback) {
        usersRef.document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                User user = document.toObject(User.class);
                                callback.accept(user);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }

    public void updateUserProfileImage(String userId, String encodedImage) {
        usersRef.document(userId)
                .update(Constants.KEY_PROFILE_IMAGE, encodedImage)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Profile image successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating profile image.", e);
                    }
                });
    }

    public void removeItemFromFavorites(String userId, String productId) {
        usersRef.document(userId)
                .update("favorites", FieldValue.arrayRemove(productId))
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Item removed from favorites successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error removing item from favorites", e);
                });
    }

    public void addItemToFavorites(String userId, String productId) {
        usersRef.document(userId)
                .update("favorites", FieldValue.arrayUnion(productId))
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Item added favorites successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding item to favorites", e);
                });
    }
}
