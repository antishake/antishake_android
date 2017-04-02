package io.github.antishake.browser;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Geofe on 3/31/17.
 */

public class MainActiviy extends Activity {
  private ListView fileview;
  private FileListAdapter adapter;
  private List<MyFile> mfiles;
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_file_browser);

    fileview=(ListView)findViewById(R.id.FileList);

    mfiles=new ArrayList<>();
    //samples

        /*
        mfiles.add(new MyFile(1,"resume",23,"09/08/1234"));
        mfiles.add(new MyFile(2,"resume1",45,"09/08/1234"));
        mfiles.add(new MyFile(3,"resume2",63,"09/08/1234"));
        mfiles.add(new MyFile(4,"resume3",54,"09/08/1234"));
        mfiles.add(new MyFile(5,"resume4",88,"09/08/1234"));
        mfiles.add(new MyFile(6,"resume5",11,"09/08/1234"));
        mfiles.add(new MyFile(7,"resume6",32,"09/08/1234"));
        mfiles.add(new MyFile(8,"resume7",29,"09/08/1234"));
        */

    //Initialized adapter
    adapter=new FileListAdapter(getApplicationContext(), mfiles);
    //GETTING FILE

    retriveFile(Environment.getExternalStorageDirectory().getAbsolutePath());

    fileview.setAdapter(adapter);

    fileview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //testing
        Toast.makeText(getApplicationContext(), "Clicked File id ="+ view.getTag(), Toast.LENGTH_SHORT).show();
        MyFile clicked=(MyFile) adapterView.getItemAtPosition(i);
        retriveFile(clicked.getPath());
      }
    });

    //get the onclickdata



  }
  public void retriveFile(String path){
    File root = new File(path);
    final File[] files = root.listFiles();

    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        mfiles.clear();
        for(File file:files) {
          MyFile f = new MyFile(
            file.getName(),
            file.length(),
            file.lastModified(),
            file.getAbsolutePath()
          );
          mfiles.add(f);
        }
        adapter.notifyDataSetChanged();
      }
    });
  }
}
