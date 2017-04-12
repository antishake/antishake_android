package io.github.antishake.browser;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.github.antishake.R;
import io.github.antishake.TextReader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This Adapter provides a binding data set to the video tab view that is displayed within a RecyclerView.
 */
public class MyVideoFileRecyclerViewAdapter extends RecyclerView.Adapter<MyVideoFileRecyclerViewAdapter.ViewHolder> {

  private final List<VideoFileItem> mValues;
  private final Context context;

  private String rootPath;

  public MyVideoFileRecyclerViewAdapter(Context context, String rootPath) {
    this.context = context;
    this.rootPath = rootPath;

    mValues = FileHelper.retrieveVideoFiles(rootPath);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
      .inflate(R.layout.video_file_list, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, int position) {
    holder.mItem = mValues.get(position);
    holder.mFilename.setText(mValues.get(position).getName());
    holder.mFilesize.setText(FileHelper.byteToHuman(mValues.get(position).getFilesize()));
    DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
    holder.mModified.setText(formatter.format(new Date(mValues.get(position).getDateModified())).toString());
    holder.mDuration.setText(String.valueOf(mValues.get(position).getDuration()));

    holder.mView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // Load up the contents of folder
        // or if it is a supported video file, open Video player activity
        VideoFileItem item = holder.mItem;
        if (FileHelper.isDirectory(item.getPath())) {
          Log.d("AS", "Opening directory " + item.getPath());
          mValues.clear();
          mValues.addAll(FileHelper.retrieveVideoFiles(item.getPath()));
          notifyDataSetChanged();
        } else if (FileHelper.isFile(item.getPath())) {
          // call VideoPlayer
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return mValues.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final TextView mFilename;
    public final TextView mFilesize;
    public final TextView mModified;
    public final TextView mDuration;
    public VideoFileItem mItem;

    public ViewHolder(View view) {
      super(view);
      mView = view;
      mFilename = (TextView) view.findViewById(R.id.filename);
      mFilesize = (TextView) view.findViewById(R.id.filesize);
      mModified = (TextView) view.findViewById(R.id.modified);
      mDuration = (TextView) view.findViewById(R.id.duration);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + mFilename.getText() + "', '" + mFilesize.getText() + "', '" + mModified.getText() + "'";
    }
  }
}
