package io.github.antishake;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ruraj on 3/24/17.
 */
public class AntiShakeWorker implements SensorEventListener, MotionCorrectionListener {

  private final AntiShake antiShake;

  public AntiShakeWorker() {
    antiShake = new AntiShake(this);
  }

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
    antiShake.calculateTransformationVector(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {

  }

  @Override
  public void onTranslationVectorReceived(ArrayList<Coordinate> arrayList) {
    Log.v("AS", "Size of translation vector: " + arrayList.size());
  }

  @Override
  public void onDeviceSteady() {
    Log.v("AS", "Steady now!");
  }
}
