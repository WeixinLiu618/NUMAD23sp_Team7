package edu.northeastern.numad23sp_team7.huskymarket.database;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.northeastern.numad23sp_team7.huskymarket.model.ChatMessage;
import edu.northeastern.numad23sp_team7.huskymarket.model.Product;
import edu.northeastern.numad23sp_team7.huskymarket.utils.Constants;

public class ChatDao {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference chatRef = db.collection(Constants.KEY_COLLECTION_CHAT);

}
