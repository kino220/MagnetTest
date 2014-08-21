package com.kino.magnet.magnettest;

import android.util.Log;

/**
 * Created by Iplab on 2014/08/21.
 */
public class MagUtility {
    static String TAG = "MagUtility";

    public static float calcDistance(float[] p0, float[] p1){

        float distance = 0.f;

        if(p0.length !=3 || p1.length !=3){
            Log.d(TAG, "引数 p0,p1 は３次元ベクトル");
            return distance;
        }

        distance = (float)Math.sqrt((double)((p1[0]-p0[0])*(p1[0]-p0[0]) + (p1[1]-p0[1])*(p1[1]-p0[1]) + (p1[2]-p0[2])*(p1[2]-p0[2])));

        return distance;
    }

}
