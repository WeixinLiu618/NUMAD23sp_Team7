package edu.northeastern.numad23sp_team7.huskymarket.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import edu.northeastern.numad23sp_team7.R;
import edu.northeastern.numad23sp_team7.databinding.ActivityHuskyMainBinding;
import edu.northeastern.numad23sp_team7.huskymarket.utils.PreferenceManager;

public class HuskyMainActivity extends AppCompatActivity {

    private ActivityHuskyMainBinding binding;
    private PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHuskyMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            changeFragment(new HomeFragment());
        }


        binding.bottomMenu.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navHome:
                    changeFragment(new HomeFragment());
                    break;
                case R.id.navSellings:
                    changeFragment(new SellingsFragment());
                    break;
                case R.id.navPost:
                    // not on fragment
                    Intent intent = new Intent(HuskyMainActivity.this, CreatePostActivity.class);
                    startActivity(intent);
                    break;
                case R.id.navMessages:
                    changeFragment(new MessagesFragment());
                break;
                case R.id.navProfile:
                    changeFragment(new ProfileFragment());
                    break;


            }

            return true;
        });
    }

    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

}