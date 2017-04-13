package io.github.antishake.browser;

import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Geofe on 3/31/17.
 */

public class FileHelper {

  public static boolean isFile(String path) {
    return new File(path).isFile();
  }

  public static boolean isDirectory(String path) {
    return new File(path).isDirectory();
  }

  public static List<TextFileItem> retrieveTextFiles(String path) {
    File root = new File(path);
    Log.d("AS", "Exists? " + root.exists());
    Log.d("AS", "Is file? " + root.isFile());
    Log.d("AS", "Is directory? " + root.isDirectory());
    Log.d("AS", "Listing files from " + path);
    File[] files = root.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File file, String s) {
        return new File(root, s).isDirectory() || s.toLowerCase().endsWith(".txt") || s.toLowerCase().endsWith(".pdf");
      }
    });
    Log.d("AS", "files list size: " + ((files == null) ? 0 : files.length));

    if (files == null) return null;

    List<TextFileItem> fileItems = new ArrayList<>();
    // Add an item to move back one directory
    File prev = new File(root, "..");
    fileItems.add(new TextFileItem("..", prev.length(), prev.lastModified(), prev.getAbsolutePath()));

    for (File file : files) {
      TextFileItem f = new TextFileItem(
        file.getName(),
        file.length(),
        file.lastModified(),
        file.getAbsolutePath()
      );
      fileItems.add(f);
    }

    return fileItems;
  }

  public static List<VideoFileItem> retrieveVideoFiles(String path) {
    File root = new File(path);
    File[] files = root.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File file, String s) {
        return new File(root, s).isDirectory() || s.toLowerCase().endsWith(".mp4") || s.toLowerCase().endsWith(".3gb");
      }
    });

    if (files == null) return null;

    List<VideoFileItem> fileItems = new ArrayList<>();
    // Add an item to move back one directory
    File prev = new File(root, "..");
    fileItems.add(new VideoFileItem("..", prev.length(), prev.lastModified(), prev.getAbsolutePath(), 0));

    for (File file : files) {
      VideoFileItem f = new VideoFileItem(
        file.getName(),
        file.length(),
        file.lastModified(),
        file.getAbsolutePath(),
        // TODO Get length of video
        1000
      );
      fileItems.add(f);
    }

    return fileItems;
  }

  public static String byteToHuman(long filesize) {
    return filesize + "b";
  }
}
