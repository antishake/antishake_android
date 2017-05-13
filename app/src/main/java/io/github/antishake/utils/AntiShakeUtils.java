package io.github.antishake.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Saranya Shanmugam on 4/5/2017.
 */

public class AntiShakeUtils {

  public static Properties getConfigProperties(Context context) {
    Properties properties = new Properties();
    AssetManager assetManager = context.getAssets();
    try {
      InputStream inputStream = assetManager.open("config.properties");
      properties.load(inputStream);
    } catch (IOException e) {
      Log.e("ANTISHAKE_ANDROID_ERROR", "Error while getting config properties", e);
    }
    return properties;
  }
}
