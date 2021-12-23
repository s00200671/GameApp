package ronnie.conlon.gameapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import DB.DatabaseHandler;
import DB.HiScore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> playActivityResultLauncher;

    private final int RESULT_WIN = 1;
    private final int RESULT_LOSE = 0;

    private final int BLUE = 1;
    private final int RED = 2;
    private final int YELLOW = 3;
    private final int GREEN = 4;

    Button bRed, bBlue, bYellow, bGreen, playBtn, fb;
    TextView ScoreTV;
    int sequenceCount = 4, n = 0;
    private Object mutex = new Object();
    ArrayList<Integer> gameSequence = new ArrayList<Integer>();

    // Keep game score and button count for amount of times buttons should flash
    int GameScore = 0, BtnCount = 4;

    CountDownTimer ct = GenerateTimer(BtnCount);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_WIN) {
                            Intent data = result.getData();
                            ++GameScore;
                            BtnCount += 2;
                        } else if (result.getResultCode() == RESULT_LOSE) {
                            Intent gameOverIntent = new Intent(MainActivity.this, GameOverActivity.class);
                            gameOverIntent.putExtra("score", GameScore);
                            startActivity(gameOverIntent);

                            GameScore = 0;
                            BtnCount = 4;
                        }
                        else {}

                        ScoreTV.setText("Score: " + String.valueOf(GameScore));
                    }
                });

        // MAIN
        bRed = findViewById(R.id.btnRed);
        bBlue = findViewById(R.id.btnBlue);
        bYellow = findViewById(R.id.btnYellow);
        bGreen = findViewById(R.id.btnGreen);
        playBtn = findViewById(R.id.playBtn);
        ScoreTV = findViewById(R.id.ScoreTV);

        ScoreTV.setText("Score: " + String.valueOf(GameScore));
    }

    public void doPlay(View view) {
        ct.start();
    }

    private void oneButton() {
        n = getRandom(sequenceCount);

        switch (n) {
            case 1:
                flashButton(bBlue);
                gameSequence.add(BLUE);
                break;
            case 2:
                flashButton(bRed);
                gameSequence.add(RED);
                break;
            case 3:
                flashButton(bYellow);
                gameSequence.add(YELLOW);
                break;
            case 4:
                flashButton(bGreen);
                gameSequence.add(GREEN);
                break;
            default:
                break;
        }   // end switch
    }

    //
    // return a number between 1 and maxValue
    private int getRandom(int maxValue) {
        return ((int) ((Math.random() * maxValue) + 1));
    }

    private void flashButton(Button button) {
        fb = button;
        Handler handler = new Handler();
        // end runnable
        Runnable r = () -> {

            fb.setPressed(true);
            fb.invalidate();
            fb.performClick();
            Handler handler1 = new Handler();
            Runnable r1 = () -> {
                fb.setPressed(false);
                fb.invalidate();
            };
            handler1.postDelayed(r1, 600);

        };
        handler.postDelayed(r, 600);
    }

    public void doTest(View view) {
        for (int i = 0; i < BtnCount; i++) {
            int x = getRandom(BtnCount);

            if (x == 1)
                flashButton(bBlue);
            else if (x == 2)
                flashButton(bRed);
            else if (x == 3)
                flashButton(bYellow);
            else if (x == 4)
                flashButton(bGreen);
        }

    }

    public void PlayGame(View view) {
        ct = GenerateTimer(BtnCount);
        this.doPlay(view);
    }

    public CountDownTimer GenerateTimer(int btns_to_show) {
        return new CountDownTimer(btns_to_show * 1500, 1500) {

            public void onTick(long millisUntilFinished) {
                //mTextField.setText("seconds remaining: " + millisUntilFinished / 1500);
                oneButton();
                //here you can have your logic to set text to edittext
            }
            public void onFinish() {
                //mTextField.setText("done!");

                Log.d("game sequence", String.valueOf(gameSequence.size()));

                // Make new activity
                Intent playIntent = new Intent(MainActivity.this, PlayActivity.class);
                // put the sequence into the next activity
                playIntent.putExtra("game_seq", gameSequence);

                playActivityResultLauncher.launch(playIntent);

                ScoreTV.setText("Score: " + String.valueOf(GameScore));
                gameSequence.clear();
                // start the next activity
                // int[] arrayB = extras.getIntArray("numbers");
            }
        };
    };
}

