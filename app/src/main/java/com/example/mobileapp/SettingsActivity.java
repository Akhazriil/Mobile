package com.example.mobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void saveSettings(View view) {
        EditText maxScoreInput = findViewById(R.id.max_score_input);
        int maxScore = Integer.parseInt(maxScoreInput.getText().toString());

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("max_score", maxScore);
        startActivity(intent);
    }
}
