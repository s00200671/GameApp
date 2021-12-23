package ronnie.conlon.gameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import DB.DatabaseHandler;
import DB.HiScore;

public class HighScoreActivity extends AppCompatActivity {

    DatabaseHandler db;
    int Score;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy");
    EditText Name;
    ListView ScoreLV;
    Button SubmitBtn;
    TextView ScoreTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        // Get Score from activity
        Score = getIntent().getExtras().getInt("Score", 0);

        ScoreTV = findViewById(R.id.myScoreTV);

        ScoreTV.setText(String.valueOf(Score));
        // Get ET
        Name = findViewById(R.id.NameET);
        // Get LV
        ScoreLV = findViewById(R.id.scoresLV);
        // Get btn
        SubmitBtn = findViewById(R.id.submitBtn);

        // DB
        db = new DatabaseHandler(this);

        List<HiScore> top5HiScores = db.getTopFiveScores();

        if(top5HiScores.size() < 1) {
            // Inserting hi scores
            Log.i("Insert: ", "Inserting ..");
            db.addHiScore(new HiScore("20 OCT 2020", "Frodo", 1));
            db.addHiScore(new HiScore("28 OCT 2020", "Dobby", 1));
            db.addHiScore(new HiScore("20 NOV 2020", "DarthV", 3));
            db.addHiScore(new HiScore("20 NOV 2020", "Bob", 2));
            db.addHiScore(new HiScore("22 NOV 2020", "Gemma", 5));
            db.addHiScore(new HiScore("30 NOV 2020", "Joe", 1));
        }
        DisplayScores();
    }

    public void SubmitScore(View view) {

        String name = Name.getText().toString();

        // Get db high scores
        List<HiScore> top5HiScores = db.getTopFiveScores();
        HiScore hiScore = top5HiScores.get(top5HiScores.size() - 1);

        if (hiScore.getScore() < Score) {
            db.addHiScore(new HiScore(dtf.format(LocalDateTime.now()), name, Score));
            DisplayScores();
            Name.setVisibility(View.INVISIBLE);
            SubmitBtn.setVisibility(View.INVISIBLE);
        }
    }

    public void DisplayScores() {

        // Calling SQL statement
        List<HiScore> top5HiScores = db.getTopFiveScores();
        List<String> scoresStr;
        scoresStr = new ArrayList<>();

        int j = 1;
        for (HiScore hs : top5HiScores) {
            // store score in string array
            scoresStr.add(j++ + " : "  +
                    hs.getPlayer_name() + " \t- " +
                    hs.getScore());
        }


        HiScore hiScore = top5HiScores.get(top5HiScores.size() - 1);
        if (hiScore.getScore() > Score) {
            Name.setVisibility(View.INVISIBLE);
            SubmitBtn.setVisibility(View.INVISIBLE);
        }

        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, scoresStr);
        ScoreLV.setAdapter(itemsAdapter);
    }

    public void Done(View view) {
        finish();
    }
}