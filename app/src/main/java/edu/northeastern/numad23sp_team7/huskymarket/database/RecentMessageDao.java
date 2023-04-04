package edu.northeastern.numad23sp_team7.huskymarket.database;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.northeastern.numad23sp_team7.huskymarket.model.RecentMessage;
import edu.northeastern.numad23sp_team7.huskymarket.utils.Constants;

public class RecentMessageDao {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference recentMessageRef = db.collection(Constants.KEY_COLLECTION_RECENT_MESSAGE);
    private final static String TAG = "recentMessageDAO";


}
