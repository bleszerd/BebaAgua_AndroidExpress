package bleszerd.com.github.beberagua;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button btnNotify;
    private EditText editMinutes;
    private TimePicker timePicker;

    private int hour;
    private int minute;
    private int interval;

    private boolean activated = false;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNotify = findViewById(R.id.btn_notify);
        editMinutes = findViewById(R.id.edit_text_number_interval);
        timePicker = findViewById(R.id.time_picker);

        btnNotify.setOnClickListener(notifyClick);

        timePicker.setIs24HourView(true);

        preferences = getSharedPreferences("database", Context.MODE_PRIVATE);

        activated = preferences.getBoolean("activated", false);

        if (activated) {
            btnNotify.setText(R.string.pause);
            ColorStateList color = ContextCompat.getColorStateList(this, R.color.black);
            btnNotify.setBackgroundTintList(color);

            int interval = preferences.getInt("interval", 0);
            int hour = preferences.getInt("hour", timePicker.getCurrentHour());
            int minute = preferences.getInt("minute", timePicker.getCurrentMinute());

            editMinutes.setText(String.valueOf(interval));
            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(minute);
        }
    }

    public View.OnClickListener notifyClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String sInterval = editMinutes.getText().toString();

            if (sInterval.isEmpty()) {
                Toast.makeText(v.getContext(), R.string.error_msg, Toast.LENGTH_SHORT).show();
                return;
            }

            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
            interval = Integer.parseInt(sInterval);

            if (!activated) {
                btnNotify.setText(R.string.pause);
                ColorStateList color = ContextCompat.getColorStateList(v.getContext(), R.color.black);
                btnNotify.setBackgroundTintList(color);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("activated", !activated);
                editor.putInt("interval", interval);
                editor.putInt("hour", hour);
                editor.putInt("minute", minute);
                editor.apply();

            } else {
                btnNotify.setText(R.string.notify);
                ColorStateList color = ContextCompat.getColorStateList(v.getContext(), R.color.teal_200);
                btnNotify.setBackgroundTintList(color);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("activated", !activated);
                editor.remove("interval");
                editor.remove("hour");
                editor.remove("minute");
                editor.apply();
            }

            activated = !activated;

            Log.d("DebugTest", "notifyClick: " + hour + " : " + minute + " : " + interval + ".");
        }
    };


//    public void notifyClick(View view){
//        String sInterval = editMinutes.getText().toString();
//
//        hour = timePicker.getCurrentHour();
//        minute = timePicker.getCurrentMinute();
//        interval = Integer.parseInt(sInterval);
//
//        Log.d("DebugTest", "notifyClick: " + hour + " : " + minute + " : " + interval + ".");
//    }

}