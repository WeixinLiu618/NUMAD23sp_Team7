package edu.northeastern.numad23sp_team7.huskymarket.activities;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import edu.northeastern.numad23sp_team7.MainActivity;
import edu.northeastern.numad23sp_team7.R;
import edu.northeastern.numad23sp_team7.databinding.ActivityHuskyMainBinding;
import edu.northeastern.numad23sp_team7.huskymarket.utils.PreferenceManager;

public class HuskyMainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_NOTIFICATION = 123;
    private ActivityHuskyMainBinding binding;
    private PreferenceManager preferenceManager;
    private AlertDialog notificationHintDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHuskyMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check notification permissions, if not, prompt user to turn on
        if (!isNotificationPermissionGranted(this)) {
            showNotificationPermissionDialog();
        }

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

    private boolean isNotificationPermissionGranted(Context context) {
        // Check whether the notification permission has been authorized
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        return notificationManager.areNotificationsEnabled();
    }

    private void showNotificationPermissionDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(HuskyMainActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_hint_notification_permission, null);
        builder.setView(dialogView);
        TextView buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        TextView buttonSettings = dialogView.findViewById(R.id.buttonSettings);

        notificationHintDialog = builder.create();

        buttonCancel.setOnClickListener(v -> {
            notificationHintDialog.dismiss();
        });

        buttonSettings.setOnClickListener(v -> {
            // Jump to the user's device notification settings page
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            startActivityForResult(intent, MY_PERMISSIONS_REQUEST_NOTIFICATION);
        });
        notificationHintDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // No matter user turned on notification, if come back, the app will close dialog
        if (requestCode == MY_PERMISSIONS_REQUEST_NOTIFICATION) {
            notificationHintDialog.dismiss();
        }
    }
    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(HuskyMainActivity.this, MainActivity.class);
        startActivity(intent);
    }
}