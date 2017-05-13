package io.github.antishake.browser;

/**
 * Created by ruraj on 3/22/17.
 */
public class VideoFileItem extends FileItem {
  private long duration;

  public VideoFileItem(String name, long filesize, long dateModified, String path, long duration) {
    super(name, filesize, dateModified, path);
    this.duration = duration;
  }

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }
}
