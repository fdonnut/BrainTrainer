package com.donnut.braintrainer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView textViewQuestion;
    private TextView textViewTimer;
    private TextView textViewScore;
    private final ArrayList<TextView> options = new ArrayList<>();

    private String question;
    private int rightAnswer;
    private int rightAnswerPosition;
    private final int max = 100;
    private int countOfQuestions = 0;
    private int countOfRightAnswers = 0;
    private int countOfWrongAnswer = 0;
    private boolean gameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewQuestion = findViewById(R.id.textViewQuestion);
        textViewTimer = findViewById(R.id.textViewTimer);
        textViewScore = findViewById(R.id.textViewScore);
        TextView textViewOpinion0 = findViewById(R.id.textViewOpinion0);
        TextView textViewOpinion1 = findViewById(R.id.textViewOpinion1);
        TextView textViewOpinion2 = findViewById(R.id.textViewOpinion2);
        TextView textViewOpinion3 = findViewById(R.id.textViewOpinion3);
        options.add(textViewOpinion0);
        options.add(textViewOpinion1);
        options.add(textViewOpinion2);
        options.add(textViewOpinion3);
        playNext();
        CountDownTimer timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                textViewTimer.setText(getTime(l));
                if (l < 10000) {
                    textViewTimer.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                }
            }

            @Override
            public void onFinish() {
                gameOver = true;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int max = preferences.getInt("max", 0);
                if (countOfRightAnswers >= max) {
                    preferences.edit().putInt("max", countOfRightAnswers).apply();
                }
                Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
                intent.putExtra("result", countOfRightAnswers);
                intent.putExtra("mistakes", countOfWrongAnswer);
                startActivity(intent);
            }
        };
        timer.start();
    }

    private void playNext() {
        generateQuestion();
        for (int i = 0; i < options.size(); i++) {
            if (i == rightAnswerPosition) {
                options.get(i).setText(Integer.toString(rightAnswer));
            } else {
                options.get(i).setText(Integer.toString(generateWrongAnswer()));
            }
        }
        String score = String.format("%s / %s", countOfRightAnswers, countOfQuestions);
        textViewScore.setText(score);
    }

    private void generateQuestion() {
        int a;
        int b;
        int mark = (int) (Math.random() * 4);
        int min = 10;
        switch (mark) {
            case 0:
                a = (int) (Math.random() * (max + 1));
                b = (int) (Math.random() * (max + 1 - a));
                rightAnswer = a + b;
                question = String.format("%s + %s", a, b);
                break;
            case 1:
                a = (int) (Math.random() * (max + 1));
                b = (int) (Math.random() * a);
                rightAnswer = a - b;
                question = String.format("%s - %s", a, b);
                break;
            case 2:
                a = (int) (Math.random() * (min + 1));
                b = (int) (Math.random() * (min + 1));
                rightAnswer = a * b;
                question = String.format("%s * %s", a, b);
                break;
            case 3:
                a = (int) (Math.random() * (max + 1));
                do {
                    b = (int) (Math.random() * min + 1);
                } while (b != 0 && a % b != 0);
                rightAnswer = a / b;
                question = String.format("%s / %s", a, b);
        }
        textViewQuestion.setText(question);
        rightAnswerPosition = (int) (Math.random() * 4);
    }

    private int generateWrongAnswer() {
        int result;
        do {
            result = (int) (Math.random() * (max + 1));
        } while (result == rightAnswer);
        return result;
    }

    public void onClickAnswer(View view) {
        if (!gameOver) {
            TextView textView = (TextView) view;
            String answer = textView.getText().toString();
            int chosenAnswer = Integer.parseInt(answer);
            if (chosenAnswer == rightAnswer) {
                countOfRightAnswers++;
//                Toast.makeText(this, "Молодец!", Toast.LENGTH_SHORT).show();
            } else {
                countOfWrongAnswer++;
//                Toast.makeText(this, "Ошибка!", Toast.LENGTH_SHORT).show();
            }
            countOfQuestions++;
            playNext();
        }
    }

    private String getTime(long millis) {
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}