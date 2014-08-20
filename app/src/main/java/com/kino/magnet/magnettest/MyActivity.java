package com.kino.magnet.magnettest;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;


public class MyActivity extends Activity implements SensorEventListener{
    private SensorManager manager;
    private TextView value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        manager = (SensorManager)getSystemService(SENSOR_SERVICE);
        value = (TextView)findViewById(R.id.textView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Listenerの登録
        List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
        if(sensors.size() > 0) {
            Sensor s = sensors.get(0);
            manager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
            }
    }

    @Override
    protected void onStop() {
        super.onStop();
        manager.unregisterListener(this);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        String str = "";
        if(sensorEvent.sensor.getType() == sensorEvent.sensor.TYPE_MAGNETIC_FIELD){
            str = "X軸:" + sensorEvent.values[0]
                    + "\nY軸:" + sensorEvent.values[1]
                    + "\nZ軸:" + sensorEvent.values[2];
            value.setText(str);

        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
