package edu.northeastern.numad23sp_team7.huskymarket.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.northeastern.numad23sp_team7.huskymarket.model.Product;
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
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    recentMessageRef.document(documentSnapshot.getId())
                            .update(Constants.KEY_USER1_IMAGE, encodedImage)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "User1 Image successfully updated!"))
                            .addOnFailureListener(e -> Log.w(TAG, "updateProfileImage: ", e));
                }
            } else {
                Log.d(TAG, "updateProfileImage: ", task.getException());
            }
        });

        Query q2 = recentMessageRef.whereEqualTo(Constants.KEY_USER2_ID, userId);
        q2.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    recentMessageRef.document(documentSnapshot.getId())
                            .update(Constants.KEY_USER2_IMAGE, encodedImage)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "User2 Image successfully updated!"))
                            .addOnFailureListener(e -> Log.w(TAG, "updateProfileImage: ", e));
                }
            } else {
                Log.d(TAG, "updateProfileImage: ", task.getException());
            }
        });
    }

    public void updateDeletedStatus(String messageId, String userId, boolean isDeleted) {
        Query q1 = recentMessageRef
                .whereEqualTo(Constants.KEY_USER1_ID, userId)
                .whereEqualTo(FieldPath.documentId(), messageId);
        q1.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    recentMessageRef.document(documentSnapshot.getId())
                            .update(Constants.KEY_DELETED_BY_USER1, isDeleted)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "User1 successfully deleted recent message!"))
                            .addOnFailureListener(e -> Log.w(TAG, "User1 failed to delete recent message: ", e));
                }
            }
        });

        Query q2 = recentMessageRef
                .whereEqualTo(Constants.KEY_USER2_ID, userId)
                .whereEqualTo(FieldPath.documentId(), messageId);
        q2.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    recentMessageRef.document(documentSnapshot.getId())
                            .update(Constants.KEY_DELETED_BY_USER2, isDeleted)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "User2 successfully deleted recent message!"))
                            .addOnFailureListener(e -> Log.w(TAG, "User2 failed to delete recent message: ", e));
                }
            }
        });
    }

    public void getRecentMessageBySearch(String userId, String username, String searchTerm, final Consumer<ArrayList<RecentMessage>> callback) {
        ArrayList<RecentMessage> searchedRecentMessages = new ArrayList<>();
        Query q1 = recentMessageRef.whereEqualTo(Constants.KEY_USER1_ID, userId);

        Query q2 = recentMessageRef.whereEqualTo(Constants.KEY_USER2_ID, userId);
        Task<QuerySnapshot> task1 = q1.get();
        Task<QuerySnapshot> task2 = q2.get();

        Tasks.whenAllComplete(task1, task2)
                .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Task<?>>> combinedTask) {
                        List<DocumentSnapshot> documents = new ArrayList<>();

                        for (Task<?> task : combinedTask.getResult()) {
                            if (task.isSuccessful()) {
                                QuerySnapshot snapshot = (QuerySnapshot) task.getResult();
                                documents.addAll(snapshot.getDocuments());
                            } else {
                                Log.e(TAG, "Error getting documents: " + task.getException());
                            }
                        }

                        // Do something with the combined documents
                        for (DocumentSnapshot documentSnapshot : documents) {
                            RecentMessage recentMessage = documentSnapshot.toObject(RecentMessage.class);
                            if (searchTerm != null && recentMessage != null
                                    && (recentMessage.getUser1Name().toLowerCase().contains(searchTerm.toLowerCase())
                                    || recentMessage.getUser2Name().toLowerCase().contains(searchTerm.toLowerCase())
                                    || recentMessage.getLastMessage().toLowerCase().contains(searchTerm.toLowerCase()))) {
                                searchedRecentMessages.add(recentMessage);
                            }

                        }

                        // Do something with the searched recent messages
                        callback.accept(searchedRecentMessages);
                    }
                });
    }


}
