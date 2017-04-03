package io.github.antishake.browser;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.content.Context;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

/**
 * Created by Geofe on 3/31/17.
 */

public class FileListAdapter extends BaseAdapter {

  private Context context;
  private List<MyFile> mfile;

  //constructor


  public FileListAdapter(Context context, List<MyFile> mfile) {
    this.context = context;
    this.mfile = mfile;
  }

  @Override
  public int getCount() {
    return mfile.size();
  }

  @Override
  public Object getItem(int i) {
    return mfile.get(i);
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  @Override
  public View getView(int i, View view, ViewGroup viewGroup) {
    View v=View.inflate(context,R.layout.filelist,null);
    TextView filename=(TextView)v.findViewById(R.id.filename);
    TextView filesize=(TextView)v.findViewById(R.id.filesize);
    TextView fileModified=(TextView)v.findViewById(R.id.modified);

    //Set text for TextView
    filename.setText(mfile.get(i).getName());
    filesize.setText(String.valueOf(mfile.get(i).getFilesize() +"Mb"));
    fileModified.setText(new Date(mfile.get(i).getDateModified()).toString());
    // File id to Tag
    v.setTag(mfile.get(i));
    return v;
  }

}
