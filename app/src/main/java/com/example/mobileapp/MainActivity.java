package com.example.mobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    int human_score = 0;
    int computer_score = 0;
    int max_score = 5; // По умолчанию максимальное количество очков

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Получаем лимит очков из настроек, если он был изменен
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("max_score")) {
            max_score = intent.getIntExtra("max_score", 5);
        }
    }

    public void buttonScissors(View view) {
        resultHandler("Scissors");
    }

    public void buttonRock(View view) {
        resultHandler("Rock");
    }

    public void buttonPaper(View view) {
        resultHandler("Paper");
    }

    private void resultHandler(String object_value) {
        int int_result = actionHandler(object_value);
        TextView textView = findViewById(R.id.score);
        String score;

        if (int_result == 1) {
            human_score += 1;
            Toast.makeText(this, "Вы выиграли раунд!", Toast.LENGTH_SHORT).show();
        } else if (int_result == -1) {
            computer_score += 1;
            Toast.makeText(this, "Вы проиграли раунд!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ничья!", Toast.LENGTH_SHORT).show();
        }

        score = "Счет: " + human_score + " : " + computer_score;
        textView.setText(score);

        checkGameOver();
    }

    private void checkGameOver() {
        if (human_score >= max_score) {
            Toast.makeText(this, "Вы выиграли игру!", Toast.LENGTH_LONG).show();
            resetGame();
        } else if (computer_score >= max_score) {
            Toast.makeText(this, "Компьютер выиграл игру!", Toast.LENGTH_LONG).show();
            resetGame();
        }
    }

    private void resetGame() {
        human_score = 0;
        computer_score = 0;
        TextView textView = findViewById(R.id.score);
        textView.setText("Счет: 0 : 0");
    }

    private static int actionHandler(String human_action) {
        String computer_action = computerAction();
        if (Objects.equals(computer_action, human_action)) {
            return 0; // ничья
        }

        switch (human_action) {
            case "Rock":
                return (Objects.equals(computer_action, "Scissors") ? 1 : -1);
            case "Paper":
                return (Objects.equals(computer_action, "Rock") ? 1 : -1);
            case "Scissors":
                return (Objects.equals(computer_action, "Paper") ? 1 : -1);
        }
        return 0;
    }

    private static String computerAction() {
        int random_number = getRandom();
        return getDictionary(random_number);
    }

    private static String getDictionary(int human_key) {
        Map<String, String> action_dict = new HashMap<>();
        action_dict.put("1", "Scissors");
        action_dict.put("2", "Rock");
        action_dict.put("3", "Paper");
        return action_dict.get(Integer.toString(human_key));
    }

    private static int getRandom() {
        return (int) ((3 * Math.random()) + 1);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
