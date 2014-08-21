package com.kino.magnet.magnettest;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MyActivity extends Activity implements SensorEventListener{
    private SensorManager manager;
    private TextView value,value2;
    public int count = 0;
    public int lap = 0;
    public boolean startFlag =false;

    public float[] prePoint = new float[3];
    public int buttonState =0;
    public float[] startPoint = new float[3];
    public float totalDistance = 0;
    public boolean isCalib = true;

    public ArrayList<float[]> valuesList = new ArrayList<float[]>();
    public ArrayList<float[]> referencePoint = new ArrayList<float[]>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        manager = (SensorManager)getSystemService(SENSOR_SERVICE);
        value = (TextView)findViewById(R.id.textView);
        value2 = (TextView)findViewById(R.id.textView2);

        final Button startButton = (Button)findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(buttonState == 0){
                    startFlag = true;
                    startButton.setText("リセット");
                    buttonState =1;
                }

                else if(buttonState == 1){
                    startFlag = false;
                    count = 0;
                    prePoint = new float[3];
                    startButton.setText("スタート");
                    buttonState = 0;
                    startPoint = new float[3];
                    lap =0;
                    valuesList = new ArrayList<float[]>();
                    referencePoint = new ArrayList<float[]>();
                    isCalib = true;

                    totalDistance = 0;
                }

            }
        });
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
        if(sensorEvent.sensor.getType() == sensorEvent.sensor.TYPE_MAGNETIC_FIELD && startFlag == true){

            valuesList.add(sensorEvent.values);

            if(valuesList.size() > 5){
                valuesList.remove(0);
            }
            float[] tmp = new float[3];

            for(int i = 0; i < valuesList.size(); i++){
                tmp[0] += valuesList.get(i)[0];
                tmp[1] += valuesList.get(i)[1];
                tmp[2] += valuesList.get(i)[2];
            }

            tmp[0] /= valuesList.size();
            tmp[1] /= valuesList.size();
            tmp[2] /= valuesList.size();

            sensorEvent.values[0] = tmp[0];
            sensorEvent.values[1] = tmp[1];
            sensorEvent.values[2] = tmp[2];


            if(prePoint[0] != 0) {

                str = "X軸:" + sensorEvent.values[0]
                        + "\nY軸:" + sensorEvent.values[1]
                        + "\nZ軸:" + sensorEvent.values[2];
                value.setText(str);

                if(count > 5){
                    if(MagUtility.calcDistance(startPoint,sensorEvent.values) < 1){
                        count = 0;
                        totalDistance = 0;
                        lap++;
                        isCalib = false;
                    }
                }

                if(MagUtility.calcDistance(prePoint, sensorEvent.values) > 1.3){
                    count++;
                    totalDistance += MagUtility.calcDistance(prePoint, sensorEvent.values);



                    if(startPoint[0] == 0 && prePoint[0] != 0){
                        startPoint[0] = prePoint[0];
                        startPoint[1] = prePoint[1];
                        startPoint[2] = prePoint[2];
                    }

                    if(isCalib){
                        if(startPoint[0] != 0) {
                            referencePoint.add(sensorEvent.values);

                            str = "prePointとの距離:" + MagUtility.calcDistance(prePoint, sensorEvent.values)
                                    + "\nポイント更新回数" + count
                                    + "\n総変化距離" + totalDistance
                                    + "\n" + lap + "周目";
                            value2.setText(str);
                        }

                    }
                    else {

                        float min = 100;
                        int minP = -1;

                        Log.d("main activity","matching start");
                        for(int i = 0; i < referencePoint.size(); i++){
                            Log.d("main activity", ""+MagUtility.calcDistance(referencePoint.get(i),sensorEvent.values));
                            if(MagUtility.calcDistance(referencePoint.get(i),sensorEvent.values) < min){
                                min = MagUtility.calcDistance(referencePoint.get(i),sensorEvent.values);
                                minP = i;
                            }
                        }



                        str = "prePointとの距離:" + MagUtility.calcDistance(prePoint, sensorEvent.values)
                                +"\nポイント更新回数" + count
                                +"\nリファレンスポイントサイズ"+ referencePoint.size()
                                +"\n"+ lap +"周目"
                                +"\n"+minP+"点目";
                        value2.setText(str);
                    }

                    prePoint[0] = sensorEvent.values[0];
                    prePoint[1] = sensorEvent.values[1];
                    prePoint[2] = sensorEvent.values[2];


                }

            }
            else {
                prePoint[0] = sensorEvent.values[0];
                prePoint[1] = sensorEvent.values[1];
                prePoint[2] = sensorEvent.values[2];
            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
