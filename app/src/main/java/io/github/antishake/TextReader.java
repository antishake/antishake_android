package io.github.antishake;

import android.annotation.SuppressLint;
import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.ScrollBar;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TextReader extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
  /**
   * Whether or not the system UI should be auto-hidden after
   * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
   */
  private static final boolean AUTO_HIDE = true;

  /**
   * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
   * user interaction before hiding the system UI.
   */
  private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

  /**
   * Some older devices needs a small delay between UI widget updates
   * and a change of the status and navigation bar.
   */
  private static final int UI_ANIMATION_DELAY = 300;
  private final Handler mHideHandler = new Handler();
  private View mContentView;

  private Intent serviceIntent;

  private final Runnable mHidePart2Runnable = new Runnable() {
    @SuppressLint("InlinedApi")
    @Override
    public void run() {
      // Delayed removal of status and navigation bar

      // Note that some of these constants are new as of API 16 (Jelly Bean)
      // and API 19 (KitKat). It is safe to use them, as they are inlined
      // at compile-time and do nothing on earlier devices.
      mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
        | View.SYSTEM_UI_FLAG_FULLSCREEN
        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
  };
  private View mControlsView;
  private final Runnable mShowPart2Runnable = new Runnable() {
    @Override
    public void run() {
      // Delayed display of UI elements
      ActionBar actionBar = getSupportActionBar();
      if (actionBar != null) {
        actionBar.show();
      }
      mControlsView.setVisibility(View.VISIBLE);
    }
  };
  private boolean mVisible;
  private final Runnable mHideRunnable = new Runnable() {
    @Override
    public void run() {
      hide();
    }
  };
  /**
   * Touch listener to use for in-layout UI controls to delay hiding the
   * system UI. This is to prevent the jarring behavior of controls going away
   * while interacting with activity UI.
   */
  private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
      if (AUTO_HIDE) {
        delayedHide(AUTO_HIDE_DELAY_MILLIS);
      }
      return false;
    }
  };
  private static PDFView pdfView;
  private boolean m_bound;
  private BroadcastReceiver m_receiver;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_text_reader);

    // TextView SHALL DISPLAY PDF

    pdfView = (PDFView) findViewById(R.id.pdfView);

    //TO ENABLE SCROLLING

    ScrollBar scrollBar = (ScrollBar) findViewById(R.id.scrollBar);
    pdfView.setScrollBar(scrollBar);
    scrollBar.setHorizontal(false);

    //UNPACK DATA FROM INTENT

    Intent i = this.getIntent();
    String path = i.getExtras().getString("PATH");

    System.out.println(path);
    //GET THE PDF FILE
    File file = new File(path);

    if (file.canRead()) {
      //LOAD IT
      pdfView.fromFile(file).defaultPage(1).onLoad(new OnLoadCompleteListener()

      {
        @Override
        public void loadComplete(int nbPages) {
          Toast.makeText(TextReader.this, String.valueOf(nbPages), Toast.LENGTH_LONG).show();
        }
      }).load();

    } else {
      System.out.println("Cannot read file");
    }


    mVisible = true;
    mControlsView = findViewById(R.id.fullscreen_content_controls);
    mContentView = findViewById(R.id.fullscreen_content_controls);         //look


    // Set up the user interaction to manually show or hide the system UI.
    mContentView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        toggle();
      }
    });

    // Upon interacting with UI controls, delay any scheduled hide()
    // operations to prevent the jarring behavior of controls going away
    // while interacting with the UI.
    findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

  }

  @Override
  public void onResume() {
    super.onResume();
    Intent intent = new Intent(this, AntiShakeWorkerService.class);
    bindService(intent, m_connection, Context.BIND_AUTO_CREATE);
    m_bound = true;
    IntentFilter intentFilter = new IntentFilter("TransVect");
    m_receiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        Coordinate transform = (Coordinate) intent.getSerializableExtra("vector");

//        Log.d("AS", transformArray.get(transformArray.size()-1).getX() + ", " + transformArray.get(transformArray.size()-1).getY());
        pdfView.setTranslationX(5.0f * (float) transform.getX());
        pdfView.setTranslationY(5.0f * (float) transform.getY());

      }
    };
    registerReceiver(m_receiver, intentFilter);
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);

    // Trigger the initial hide() shortly after the activity has been
    // created, to briefly hint to the user that UI controls
    // are available.
    delayedHide(100);
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (updateThread != null) {
      updateThread.interrupt();
    }
    if (m_bound) {
      unbindService(m_connection);
      m_bound = false;
    }
    unregisterReceiver(m_receiver);
  }

  private void toggle() {
    if (mVisible) {
      hide();
    } else {
      show();
    }
  }

  private void hide() {
    // Hide UI first
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.hide();
    }
    mControlsView.setVisibility(View.GONE);
    mVisible = false;

    // Schedule a runnable to remove the status and navigation bar after a delay
    mHideHandler.removeCallbacks(mShowPart2Runnable);
    mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
  }

  @SuppressLint("InlinedApi")
  private void show() {
    // Show the system bar
    mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
      | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    mVisible = true;

    // Schedule a runnable to display UI elements after a delay
    mHideHandler.removeCallbacks(mHidePart2Runnable);
    mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
  }

  /**
   * Schedules a call to hide() in [delay] milliseconds, canceling any
   * previously scheduled calls.
   */
  private void delayedHide(int delayMillis) {
    mHideHandler.removeCallbacks(mHideRunnable);
    mHideHandler.postDelayed(mHideRunnable, delayMillis);
  }

  private static UpdateThread updateThread;

  public static class TransVectReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals("TransVect")) {
        ArrayList<Coordinate> vector = (ArrayList<Coordinate>) intent.getSerializableExtra("vector");

        Log.d("AS", System.nanoTime() + ": " + vector.get(0).getX() + "," + vector.get(0).getY());

        pdfView.setTranslationX((float) vector.get(0).getX());
        pdfView.setTranslationY((float) vector.get(0).getY());
//        if (updateThread != null) {
//          updateThread.breakOff();
//          // Make sure that the previous thread is done running
//          try {
//            updateThread.join();
//          } catch (InterruptedException e) {
//            e.printStackTrace();
//          }
//        }
//        updateThread = new UpdateThread(vector);
//        updateThread.start();
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

        pdfView.setTranslationX((float) coordinate.getX());
        pdfView.setTranslationY((float) coordinate.getY());

//        Log.d("AS", "Updated graph");
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

  private ServiceConnection m_connection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName className, IBinder binder) {
    }

    @Override
    public void onServiceDisconnected(ComponentName className) {
    }
  };

  @Override
  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {
  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {
  }
}
