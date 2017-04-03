package io.github.antishake.browser;
import java.util.Date;

/**
 * Created by Geofe on 3/31/17.
 */

public class MyFile {

  private String name;
  private long filesize;
  private long DateModified;
  private String path;

  //constructo for the class

  public MyFile(String name, long filesize, long dateModified,String path) {

    this.name = name;
    this.filesize = filesize;
    DateModified = dateModified;
    this.path=path;
  }

  //getter,setter


  public void setFilesize(long filesize) {
    this.filesize = filesize;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getFilesize() {
    return filesize;
  }

  public void setFilesize(int filesize) {
    this.filesize = filesize;
  }

  public long getDateModified() {
    return DateModified;
  }

  public void setDateModified(long dateModified) {
    DateModified = dateModified;
  }
}
