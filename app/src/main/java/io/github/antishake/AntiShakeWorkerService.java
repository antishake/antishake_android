package io.github.antishake;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import io.github.antishake.utils.AntiShakeUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class AntiShakeWorkerService extends Service implements SensorEventListener, MotionCorrectionListener {
  private AntiShake antiShake;
  private SensorManager sensorManager;
  private Sensor linearAccelerometer;

  private static long lastAccelTs;

  // TODO Get sampling rate from config
  private int samplingRate = 20000;

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
    antiShake.calculateTransformationVector(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);

//    long ts = System.currentTimeMillis();
//    if (ts - lastAccelTs > 20) {
//      lastAccelTs = ts;
//       Broadcast accelerometer data
//      Intent intent = new Intent("AccelVal");
//      intent.putExtra("vals", sensorEvent.values);
//      intent.putExtra("ts", ts);
//      sendBroadcast(intent);
//    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {

  }

  @Override
  public void onTranslationVectorReceived(ArrayList<Coordinate> arrayList) {
    Log.v("AS", "Translation vector: " + Arrays.toString(Arrays.copyOfRange(arrayList.toArray(), 0, 5)));
    Intent intent = new Intent("TransVect");
    intent.putExtra("vector", arrayList);
    sendBroadcast(intent);
  }

  @Override
  public void onDeviceSteady() {
    Log.v("AS", "Device Steady");
  }

  @Override
  public void onStart(Intent intent, int startId) {
    super.onStart(intent, startId);
    antiShake = new AntiShake(this, AntiShakeUtils.getConfigProperties(this));
    sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    linearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    sensorManager.registerListener(this, linearAccelerometer, samplingRate);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    sensorManager.unregisterListener(this);
  }

  @Override
  public IBinder onBind(Intent intent) {
    // TODO: Return the communication channel to the service.
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
