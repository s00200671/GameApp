package ronnie.conlon.gameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PlayActivity extends AppCompatActivity implements SensorEventListener {

    private final int RESULT_WIN = 1;
    private final int RESULT_LOSE = 0;

    private final int BLUE = 1;
    private final int RED = 2;
    private final int YELLOW = 3;
    private final int GREEN = 4;

    Button bRed, bBlue, bYellow, bGreen, bp;
    ArrayList<Integer> gameSeq;
    ArrayList<Integer> buttons_pressed = new ArrayList<Integer>();
    int bpVal = 0;

    // TILT
    // experimental values for hi and lo magnitude limits

    // RED
    private final double NORTH_MOVE_FORWARD = 7.0;
    private final double NORTH_MOVE_BACKWARD = 4.0;
    // YELLOW
    private final double SOUTH_MOVE_FORWARD = -7.0;
    private final double SOUTH_MOVE_BACKWARD = -4.0;
    // BLUE
    private final double WEST_MOVE_FORWARD = -7.0;
    private final double WEST_MOVE_BACKWARD = -4.0;
    // GREEN
    private final double EAST_MOVE_FORWARD = 7.0;
    private final double EAST_MOVE_BACKWARD = 4.0;
    boolean highLimit = false;      // detect high limit
    int counter = 0;                // step counter

    TextView tvx, tvy, tvz, tvSteps;
    public SensorManager mSensorManager;
    public Sensor mSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        bRed = findViewById(R.id.btnRed);
        bBlue = findViewById(R.id.btnBlue);
        bYellow = findViewById(R.id.btnYellow);
        bGreen = findViewById(R.id.btnGreen);

        gameSeq = getIntent().getExtras().getIntegerArrayList("game_seq");

        // TILT
        tvx = findViewById(R.id.tvX);
        tvy = findViewById(R.id.tvY);
        tvz = findViewById(R.id.tvZ);

        // we are going to use the sensor service
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void BtnTap(View view) {
        Button b = findViewById(view.getId());

        // Add the color value depending on which button was pressed
        if (b == bRed) {
            this.buttons_pressed.add(RED);
        }
        else if (b == bBlue) {
            this.buttons_pressed.add(BLUE);
        }
        else if (b == bYellow) {
            this.buttons_pressed.add(YELLOW);
        }
        else if (b == bGreen) {
            this.buttons_pressed.add(GREEN);
        }
        else {
            throw new Error();
        }

        CheckFinish();
    }

    // When the app is brought to the foreground - using app on screen
    protected void onResume() {
        super.onResume();
        // turn on the sensor
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    //App running but not on screen - in the background
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);    // turn off listener to save power
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        tvx.setText(String.valueOf(x));
        tvy.setText(String.valueOf(y));
        tvz.setText(String.valueOf(z));


        // Can we get a north movement
        // you need to do your own mag calculating
        if (!highLimit) {
            if (x > NORTH_MOVE_FORWARD) {
                highLimit = true;
                bpVal = RED;
                bp = bRed;
                flash(bp);
            }
            else if (x < SOUTH_MOVE_FORWARD) {
                highLimit = true;
                bpVal = YELLOW;
                bp = bYellow;
                flash(bp);
            }
            else if (y < WEST_MOVE_FORWARD) {
                highLimit = true;
                bpVal = BLUE;
                bp = bBlue;
                flash(bp);
            }
            else if (y > EAST_MOVE_FORWARD) {
                highLimit = true;
                bpVal = GREEN;
                bp = bGreen;
                flash(bp);
            }
            else {
                bpVal = 0;
                bp = null;
            }
        }
        else {
            if (x < NORTH_MOVE_BACKWARD && x > SOUTH_MOVE_BACKWARD && y > WEST_MOVE_BACKWARD && y < EAST_MOVE_BACKWARD) {
                // If we have tilted back

                highLimit = false;
                buttons_pressed.add(bpVal);

                bp.setPressed(false);
                bp.invalidate();
                bpVal = 0;
                bp = null;

                CheckFinish();
            }
        }
    }

    public void flash(Button btn) {
        btn.setPressed(true);
        btn.invalidate();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not used
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public void CheckFinish() {
        if (buttons_pressed.size() == gameSeq.size()) {
            // Default result is win
            setResult(RESULT_WIN);
            // If any of the sequence doesn't match, result is lose
            for(int i = 0; i < gameSeq.size(); i++) {
                if (!(gameSeq.get(i).equals(this.buttons_pressed.get(i)) )) {
                    setResult(RESULT_LOSE); }
            }
            // Finish activity with result
            finish();
        }
    }
}