package io.github.antishake;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PDFDocs extends AppCompatActivity {

  String name, path;

  public String getName(){
    return name;
  }
  public void setName(String name){
    this.name = name;
  }

  public String getPath(){
    return path;
  }

  public void setPath(String path){
    this.path = path;
  }
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pdfdocs);
  }
}
