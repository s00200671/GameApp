package ronnie.conlon.gameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GameOverActivity extends AppCompatActivity {

    int Score;
    TextView ScoreTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        Score = getIntent().getExtras().getInt("score", 0);
        ScoreTV = findViewById(R.id.scoreTV);

        ScoreTV.setText("Score: " + String.valueOf(Score));
    }

    public void PlayAgain(View view) {
        finish();
    }

    public void ShowHighScores(View view) {
        Intent highScoreIntent = new Intent(this, HighScoreActivity.class);
        highScoreIntent.putExtra("Score", Score);
        startActivity(highScoreIntent);

        finish();
    }
}