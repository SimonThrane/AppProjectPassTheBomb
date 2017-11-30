package com.thrane.simon.passthebomb.Util;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Created by kaspe on 30-11-2017.
 */

public class CalibrationHelper implements SensorEventListener {
    public final float[] accelerometerReading = new float[3];
    public final float[] magnetometerReading = new float[3];
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(sensorEvent.values, 0, accelerometerReading,
                    0, accelerometerReading.length);
        }
        else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(sensorEvent.values, 0, magnetometerReading,
                    0, magnetometerReading.length);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
