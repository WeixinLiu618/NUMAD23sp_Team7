package edu.northeastern.numad23sp_team7.huskymarket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.northeastern.numad23sp_team7.databinding.FragmentMessagesBinding;
import edu.northeastern.numad23sp_team7.huskymarket.adapter.RecentMessagesAdapter;
import edu.northeastern.numad23sp_team7.huskymarket.database.UserDao;
import edu.northeastern.numad23sp_team7.huskymarket.listeners.RecentMessageCardClickListener;
import edu.northeastern.numad23sp_team7.huskymarket.model.RecentMessage;
import edu.northeastern.numad23sp_team7.huskymarket.model.RecentMessageCard;
import edu.northeastern.numad23sp_team7.huskymarket.model.User;
import edu.northeastern.numad23sp_team7.huskymarket.utils.Constants;
import edu.northeastern.numad23sp_team7.huskymarket.utils.PreferenceManager;

public class MessagesFragment extends Fragment implements RecentMessageCardClickListener {

    private FragmentMessagesBinding binding;
    private PreferenceManager preferenceManager;

    private List<RecentMessageCard> recentMessageCards;
    private RecentMessagesAdapter recentMessagesAdapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore database;

    private static final UserDao userDao = new UserDao();

    private static final String TAG = "message-fragment";

    public MessagesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(requireContext());
        // Inflate the layout for this fragment
        binding = FragmentMessagesBinding.inflate(getLayoutInflater());
        recentMessageCards = new ArrayList<>();
        recentMessagesAdapter = new RecentMessagesAdapter(recentMessageCards, this);
        binding.recentMessagesRecyclerView.setAdapter(recentMessagesAdapter);


        database.collection(Constants.KEY_COLLECTION_RECENT_MESSAGE)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_RECENT_MESSAGE)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);


        // hard code
//        binding.buttonChat.setOnClickListener(v -> {
//            userDao.getUserById("D9gtlUubrMYR9UZyCQlc18uAr7r2", receiver -> {
//                Intent intent = new Intent(getActivity(), ChatActivity.class);
//                intent.putExtra(Constants.KEY_USER, receiver);
//                startActivity(intent);
//            });
//
//        });

        return binding.getRoot();
    }


    @Override
    public void onItemClick(User user) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            Log.e(TAG, "error: ", error);
            return;
        }
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {

                    RecentMessage recentMessage = documentChange.getDocument().toObject(RecentMessage.class);

                    RecentMessageCard recentMessageCard = new RecentMessageCard();
                    recentMessageCard.setLastMessage(recentMessage.getLastMessage());
                    recentMessageCard.setTimestamp(recentMessage.getTimestamp());

                    if (preferenceManager.getString(Constants.KEY_USER_ID).equals(recentMessage.getSenderId())) {
                        recentMessageCard.setDisplayedUserId(recentMessage.getReceiverId());
                        recentMessageCard.setDisplayedUsername(recentMessage.getReceiverName());
                        recentMessageCard.setDisplayedUserImage(recentMessage.getReceiverImage());
                    } else {
                        recentMessageCard.setDisplayedUserId(recentMessage.getSenderId());
                        recentMessageCard.setDisplayedUsername(recentMessage.getSenderName());
                        recentMessageCard.setDisplayedUserImage(recentMessage.getSenderImage());
                    }
                    recentMessageCards.add(recentMessageCard);

                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for (RecentMessageCard recentMessageCard : recentMessageCards) {
                        RecentMessage recentMessage = documentChange.getDocument().toObject(RecentMessage.class);
                        if (recentMessageCard.getDisplayedUserId().equals(recentMessage.getSenderId())
                                || recentMessageCard.getDisplayedUserId().equals(recentMessage.getReceiverId())) {
                            recentMessageCard.setLastMessage(recentMessage.getLastMessage());
                            recentMessageCard.setTimestamp(recentMessage.getTimestamp());
                            break;
                        }
                    }
                }
            }
            Collections.sort(recentMessageCards, (o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));
            recentMessagesAdapter.notifyDataSetChanged();

            if (recentMessageCards.size() == 0) {
                binding.noMessagePrompt.setVisibility(View.VISIBLE);
                binding.recentMessagesRecyclerView.setVisibility(View.GONE);
            } else {
                binding.recentMessagesRecyclerView.smoothScrollToPosition(0);
                binding.recentMessagesRecyclerView.setVisibility(View.VISIBLE);
                binding.noMessagePrompt.setVisibility(View.GONE);
            }
            binding.progressBar.setVisibility(View.GONE);
        }

    };
}