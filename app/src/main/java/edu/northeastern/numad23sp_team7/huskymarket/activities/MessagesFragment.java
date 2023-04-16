package edu.northeastern.numad23sp_team7.huskymarket.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.northeastern.numad23sp_team7.databinding.FragmentMessagesBinding;
import edu.northeastern.numad23sp_team7.huskymarket.adapter.RecentMessagesAdapter;
import edu.northeastern.numad23sp_team7.huskymarket.database.RecentMessageDao;
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
    private FirebaseFirestore database;
    private String searchTerm = "";

    private static final UserDao userDao = new UserDao();
    private static final RecentMessageDao recentMessageDao = new RecentMessageDao();

    private static final String TAG = "message-fragment";

    public MessagesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(requireContext());
        // Inflate the layout for this fragment
        binding = FragmentMessagesBinding.inflate(getLayoutInflater());
        recentMessageCards = new ArrayList<>();
        recentMessagesAdapter = new RecentMessagesAdapter(recentMessageCards, this);
        binding.recentMessagesRecyclerView.setAdapter(recentMessagesAdapter);


        database.collection(Constants.KEY_COLLECTION_RECENT_MESSAGE)
                .whereEqualTo(Constants.KEY_USER1_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_RECENT_MESSAGE)
                .whereEqualTo(Constants.KEY_USER2_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);


        // TODO, hard code, need to delete later
        binding.buttonChat.setOnClickListener(v -> {
            userDao.getUserById("D9gtlUubrMYR9UZyCQlc18uAr7r2", receiver -> {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(Constants.KEY_USER, receiver);
                startActivity(intent);
            });

        });

        // search
        binding.searchPlate.requestFocus();
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(binding.searchPlate, InputMethodManager.SHOW_IMPLICIT);
        binding.searchPlate.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // perform search
                performSearch();
                return true;
            }
            return false;
        });

        // when delete all text, perform search automatically
        binding.searchPlate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: " + s.length());
                if(s.length() == 0) {
                    performSearch();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                // do nothing
            }
        });

        return binding.getRoot();
    }

    private void performSearch() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.searchPlate.getWindowToken(), 0);
        binding.searchPlate.clearFocus();
        searchTerm = binding.searchPlate.getText().toString().trim();
        Log.d(TAG, "search term:" + searchTerm);
        searchRecentMessage();
    }

    private void searchRecentMessage() {
        loading(true);
        recentMessageDao.getRecentMessageBySearch(preferenceManager.getString(Constants.KEY_USER_ID),
                preferenceManager.getString(Constants.KEY_USERNAME), searchTerm, recentMessages -> {
                    recentMessageCards.clear();
                    for(RecentMessage recentMessage: recentMessages) {
                        RecentMessageCard recentMessageCard = new RecentMessageCard();
                        recentMessageCard.setLastMessage(recentMessage.getLastMessage());
                        recentMessageCard.setTimestamp(recentMessage.getTimestamp());

                        if (preferenceManager.getString(Constants.KEY_USER_ID).equals(recentMessage.getUser1Id())) {
                            recentMessageCard.setDisplayedUserId(recentMessage.getUser2Id());
                            recentMessageCard.setDisplayedUsername(recentMessage.getUser2Name());
                            recentMessageCard.setDisplayedUserImage(recentMessage.getUser2Image());
                        } else {
                            recentMessageCard.setDisplayedUserId(recentMessage.getUser1Id());
                            recentMessageCard.setDisplayedUsername(recentMessage.getUser1Name());
                            recentMessageCard.setDisplayedUserImage(recentMessage.getUser1Image());
                        }
                        recentMessageCards.add(recentMessageCard);
                    }
                    Collections.sort(recentMessageCards, (o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));
                    recentMessagesAdapter.notifyDataSetChanged();
                    loading(false);
                });


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

                    if (preferenceManager.getString(Constants.KEY_USER_ID).equals(recentMessage.getUser1Id())) {
                        recentMessageCard.setDisplayedUserId(recentMessage.getUser2Id());
                        recentMessageCard.setDisplayedUsername(recentMessage.getUser2Name());
                        recentMessageCard.setDisplayedUserImage(recentMessage.getUser2Image());
                    } else {
                        recentMessageCard.setDisplayedUserId(recentMessage.getUser1Id());
                        recentMessageCard.setDisplayedUsername(recentMessage.getUser1Name());
                        recentMessageCard.setDisplayedUserImage(recentMessage.getUser1Image());
                    }
                    recentMessageCards.add(recentMessageCard);

                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for (RecentMessageCard recentMessageCard : recentMessageCards) {
                        RecentMessage recentMessage = documentChange.getDocument().toObject(RecentMessage.class);
                        if (recentMessageCard.getDisplayedUserId().equals(recentMessage.getUser1Id())
                                || recentMessageCard.getDisplayedUserId().equals(recentMessage.getUser2Id())) {
                            if (preferenceManager.getString(Constants.KEY_USER_ID).equals(recentMessage.getUser1Id())) {
//                                recentMessageCard.setDisplayedUsername(recentMessage.getUser2Name()); // can delete if not change username
                                recentMessageCard.setDisplayedUserImage(recentMessage.getUser2Image());
                            } else {
//                                recentMessageCard.setDisplayedUsername(recentMessage.getUser1Name()); // can delete if not change username
                                recentMessageCard.setDisplayedUserImage(recentMessage.getUser1Image());
                            }
                            recentMessageCard.setLastMessage(recentMessage.getLastMessage());
                            recentMessageCard.setTimestamp(recentMessage.getTimestamp());
                            break;
                        }
                    }
                }
            }
            Collections.sort(recentMessageCards, (o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));
            recentMessagesAdapter.notifyDataSetChanged();
            loading(false);
        }

    };


    private void loading(boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.recentMessagesRecyclerView.setVisibility(View.GONE);
            binding.noMessagePrompt.setVisibility(View.GONE);
        } else {
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
    }
}