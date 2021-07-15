package bleszerd.com.github.beberagua;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

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

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);

                Intent notificationIntent = new Intent(MainActivity.this, NotificationPublisher.class);
                notificationIntent.putExtra(NotificationPublisher.KEY_NOTIFICATION_ID, 1);
                notificationIntent.putExtra(NotificationPublisher.KEY_NOTIFICATION, "Hora de beber água");

                PendingIntent broadcast = PendingIntent.getBroadcast(MainActivity.this,
                        0, notificationIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);

                AlarmManager alarmManager =
                        (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        interval * 1000 * 60,
                        broadcast);
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

                Intent notificationIntent = new Intent(MainActivity.this, NotificationPublisher.class);
                PendingIntent broadcast = PendingIntent.getBroadcast(MainActivity.this,
                        0, notificationIntent, 0);
                AlarmManager alarmManager =
                        (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(broadcast);
            }

            activated = !activated;
        }
    };
}