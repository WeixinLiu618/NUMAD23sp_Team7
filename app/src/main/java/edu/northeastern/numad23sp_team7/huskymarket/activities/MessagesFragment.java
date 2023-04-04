package edu.northeastern.numad23sp_team7.huskymarket.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.numad23sp_team7.R;
import edu.northeastern.numad23sp_team7.databinding.FragmentMessagesBinding;
import edu.northeastern.numad23sp_team7.databinding.FragmentProfileBinding;
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
        recentMessagesAdapter = new RecentMessagesAdapter(recentMessageCards,this);
        binding.recentMessagesRecyclerView.setAdapter(recentMessagesAdapter);




        // hard code
        binding.buttonChat.setOnClickListener(v -> {
            userDao.getUserById("D9gtlUubrMYR9UZyCQlc18uAr7r2", receiver -> {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(Constants.KEY_USER, receiver);
                startActivity(intent);
            });

        });

        return binding.getRoot();
    }



    @Override
    public void onDisplayedUserClick(User user) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }
}