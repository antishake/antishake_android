package io.github.antishake;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import io.github.antishake.utils.AntiShakeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AntiShakeWorkerService extends Service implements SensorEventListener, MotionCorrectionListener {
  private final String TAG = "AS";
  private AntiShake antiShake;
  private SensorManager sensorManager;
  private Sensor linearAccelerometer;

  // TODO Get sampling rate from config
  private int samplingRate = 20000;

  private IBinder mBinder = new Binder();

  private boolean changed = false;

  private int vectorIdx = 1;
  private Timer timer = new Timer();
  private final TimerTask timerTask = new TimerTask() {
    @Override
    public void run() {
      if (vector.size() == 0 || vectorIdx == 2) {
        return;
      }
//      List<Coordinate> copy = vector.subList(1, vector.size()-1);
//      for (Coordinate coordinate : copy) {
//        Log.v("AS", "Translation vector: " + vector);
        Intent intent = new Intent("TransVect");
        intent.putExtra("vector", vector.get(vectorIdx++)); //coordinate);
        sendBroadcast(intent);

//        if (changed) {
//          break;
//        }

//        synchronized (timerTask) {
//          try {
//            wait(20);
//          } catch (InterruptedException e) {
//            e.printStackTrace();
//          }
//        }
//      }
    }
  };

  private ArrayList<Coordinate> vector = new ArrayList<>();

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
    antiShake.calculateTransformationVector(5 * sensorEvent.values[0], 5 * sensorEvent.values[1], sensorEvent.values[2]);
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {

  }

  @Override
  public void onTranslationVectorReceived(ArrayList<Coordinate> arrayList) {
    vector = arrayList;

    vectorIdx = 1;
//    timerTask.cancel();
//    changed = true;
//    timerTask.run();
  }

  @Override
  public void onDeviceSteady() {
    Log.v("AS", "Device Steady");
  }

  @Override
  public void onCreate() {
    antiShake = new AntiShake(this, AntiShakeUtils.getConfigProperties(this));
    sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    linearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    sensorManager.registerListener(this, linearAccelerometer, samplingRate);

    timer.schedule(timerTask, 0, 20);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    sensorManager.unregisterListener(this);
  }

  @Override
  public IBinder onBind(Intent intent) {
    Log.d(TAG, "service bound");
    return mBinder;
  }
}
