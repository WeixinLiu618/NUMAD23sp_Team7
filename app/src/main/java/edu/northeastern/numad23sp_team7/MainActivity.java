package edu.northeastern.numad23sp_team7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.northeastern.numad23sp_team7.FireBaseModel.LogInActivity;
import edu.northeastern.numad23sp_team7.huskymarket.activities.HuskyLoginActivity;
import edu.northeastern.numad23sp_team7.huskymarket.activities.HuskyMainActivity;
import edu.northeastern.numad23sp_team7.huskymarket.activities.SearchActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnA6 = findViewById(R.id.btn_a6);
        Button btnA7 = findViewById(R.id.btn_a7);
        Button btnAbout = findViewById(R.id.btn_about);
        Button btnGroup = findViewById(R.id.btn_group_project);

        btnA6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToA6Activity();
            }
        });

        btnA7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToA8Activity();
            }
        });

        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoAboutActivity();
            }
        });

        btnGroup.setOnClickListener(v -> goToGroupProject());

    }

    public void goToA6Activity() {
        Intent intent = new Intent(this, AtYourServiceActivity.class);
        startActivity(intent);
    }

    public void goToA8Activity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    public void gotoAboutActivity() {
        Intent intent = new Intent(this, AboutUsActivity.class);
        startActivity(intent);
    }

    public void goToGroupProject() {
        Intent intent = new Intent(this, HuskyLoginActivity.class);
        startActivity(intent);
//        Intent intent = new Intent(this, SearchActivity.class);
//        startActivity(intent);
    }
}