package io.github.antishake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import com.androidplot.Plot;
import com.androidplot.util.Redrawer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruraj on 3/28/17.
 */
public class TestScreenActivity extends AppCompatActivity {

  private static int MAX_X_COUNT = 1000;
  private float x;
  private float y;
  private static ImageView point;

  private Intent serviceIntent;

  private static UpdateThread updateThread;
  private XYPlot graphView;
  private Handler uiHandler = new Handler();
  private static SimpleXYSeries seriesX;
  //  private static LineGraphSeries<DataPoint> seriesY;
//  private static LineGraphSeries<DataPoint> seriesZ;
  private static SimpleXYSeries accelX;
  //  private static LineGraphSeries<DataPoint> accelY;
//  private static LineGraphSeries<DataPoint> accelZ;
  private Redrawer redrawer;

  public static class TransVectReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals("TransVect")) {
        ArrayList<Coordinate> vector = (ArrayList<Coordinate>) intent.getSerializableExtra("vector");

        if (updateThread != null) {
          updateThread.breakOff();
          // Make sure that the previous thread is done running
          try {
            updateThread.join();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        updateThread = new UpdateThread(vector);
        updateThread.start();
      } else if (intent.getAction().equals("AccelVal")) {
        float[] values = intent.getFloatArrayExtra("vals");
        double ts = (double) intent.getLongExtra("ts", System.currentTimeMillis());
        updateSeries(accelX, ts, values[0]);
      }
    }
  }

  private static class UpdateThread extends Thread {

    private List<Coordinate> coordinateList;
    private boolean breakOff = false;

    UpdateThread(List<Coordinate> list) {
      this.coordinateList = list;
    }

    @Override
    public void run() {
      for (Coordinate coordinate : coordinateList) {
        if (breakOff || isInterrupted()) {
          // This means we have a new array available so we need to start over
          // or that the activity has been closed
          break;
        }
        // Move the screen
        double t = System.currentTimeMillis();
        updateSeries(seriesX, t, coordinate.getX());

        point.setTranslationX((float) coordinate.getX());
        point.setTranslationY((float) coordinate.getY());

        Log.d("AS", "Updated graph");
        try {
          sleep(20);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

    void breakOff() {
      breakOff = true;
    }
  }

  private static void updateSeries(SimpleXYSeries series, double x, double y) {
//    series.resize(MAX_X_COUNT);

    series.addLast(x, y);
//    setModel(
//      Arrays.asList(new Number[]{x, y}),
//      SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_testscreen);

    point = (ImageView) findViewById(R.id.point);
    x = point.getX();
    y = point.getY();

    graphView = (XYPlot) findViewById(R.id.graph);
//    graphView.setDomainBoundaries(0, 1000, BoundaryMode.GROW);
//    graphView.setRenderMode(Plot.RenderMode.USE_BACKGROUND_THREAD);

    seriesX = new SimpleXYSeries("VX");
    accelX = new SimpleXYSeries("X");

    LineAndPointFormatter formatter;
    graphView.addSeries(seriesX,
      formatter = new LineAndPointFormatter(Color.GREEN, Color.BLUE, Color.TRANSPARENT, null));
    formatter.getVertexPaint().setAlpha(0);
    graphView.addSeries(accelX,
      formatter = new LineAndPointFormatter(Color.RED, Color.BLACK, Color.TRANSPARENT, null));
    formatter.getVertexPaint().setAlpha(0);

    redrawer = new Redrawer(graphView, 20, false);

//    graphView = (GraphView) findViewById(R.id.graph);
//    graphView.getViewport().setXAxisBoundsManual(true);
//    graphView.getViewport().setMinX(0);
//    graphView.getViewport().setMaxX(10000);
//
//    seriesX = new LineGraphSeries<>();
//    seriesX.setTitle("X");
//    seriesY = new LineGraphSeries<>();
//    seriesY.setTitle("Y");
//    seriesZ = new LineGraphSeries<>();
//    seriesZ.setTitle("Z");
//
//    accelX = new LineGraphSeries<>();
//    accelX.setColor(Color.RED);
//
//    graphView.addSeries(seriesX);
//    graphView.addSeries(seriesY);
//    graphView.addSeries(seriesZ);
//
//    graphView.addSeries(accelX);

    serviceIntent = new Intent(this, AntiShakeWorkerService.class);
    startService(serviceIntent);
  }

  @Override
  protected void onResume() {
    super.onResume();
    redrawer.start();
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (updateThread != null) {
      updateThread.interrupt();
    }
    stopService(serviceIntent);
  }

  @Override
  protected void onStop() {
    super.onStop();
    stopService(serviceIntent);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    stopService(serviceIntent);
    redrawer.finish();
  }
}
