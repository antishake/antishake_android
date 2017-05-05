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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 */
public class MyTextFileRecyclerViewAdapter extends RecyclerView.Adapter<MyTextFileRecyclerViewAdapter.ViewHolder> {
  private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

  private final List<TextFileItem> mValues;
  private final Context context;

  private String rootPath;

  public MyTextFileRecyclerViewAdapter(Context context, String rootPath) {
    this.context = context;
    this.rootPath = rootPath;

    mValues = FileHelper.retrieveTextFiles(rootPath);
  }

  @Override
  public int getItemViewType(int position) {
    TextFileItem item = mValues.get(position);
    if (FileHelper.isDirectory(item.getPath())) {
      return 0;
    } else {
      return 1;
    }
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view;
    if (viewType == 0 ) {
      view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.text_folder, parent, false);
    } else {
      view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.text_file, parent, false);
    }
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, int position) {
    holder.mItem = mValues.get(position);
    holder.mFilename.setText(mValues.get(position).getName());
    holder.mFilesize.setText(FileHelper.byteToHuman(mValues.get(position).getFilesize()));
    // TODO Use simple date formatter to get a shorter string for this
    holder.mModified.setText(DATE_FORMAT.format(mValues.get(position).getDateModified()));

    holder.mView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // Load up the contents of folder
        // or if it is a file, open TextReader activity
        TextFileItem item = holder.mItem;
        if (FileHelper.isDirectory(item.getPath())) {
          Log.d("AS", "Opening directory " + item.getPath());
          List<TextFileItem> fileList;
          if((fileList = FileHelper.retrieveTextFiles(item.getPath())) != null) {
            mValues.clear();
            mValues.addAll(fileList);
            notifyDataSetChanged();
          }
        } else if (FileHelper.isFile(item.getPath())) {
          Log.d("AS", "Opening file " + item.getPath());
          Intent intent = new Intent(context, TextReader.class);
          intent.putExtra("PATH", item.getPath());
          context.startActivity(intent);
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
    public TextFileItem mItem;

    public ViewHolder(View view) {
      super(view);
      mView = view;
      mFilename = (TextView) view.findViewById(R.id.filename);
      mFilesize = (TextView) view.findViewById(R.id.filesize);
      mModified = (TextView) view.findViewById(R.id.modified);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + mFilesize.getText() + "'";
    }
  }
}
