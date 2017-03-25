package io.github.antishake;

import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(toolbar);
    setSupportActionBar(toolbar);
    final ListView lv = (ListView) findViewById(R.id.lv);

    FloatingActionButton fab = (FloatingActionButton) findViewById(fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        lv.setAdapter(new CustomAdapter(MainActivity.this, getPDFs()));
      }
    });

    private ArrayList<PDFDocs> getPDFs()
      {
        ArrayList <PDFDocs> pdfDocs = new ArrayList<>();
    //TARGET FOLDER
    File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    PDFDocs pdfDoc;
    if (downloadsFolder.exists()) {
      //GET ALL FILES IN DOWNLOAD FOLDER
      File[] files = downloadsFolder.listFiles();
      //LOOP THROUGH THOSE FILES GETTING NAME AND URI
      for (int i = 0; i < files.length; i++) {
        File file = files[i];
        if (file.getPath().endsWith("pdf")) {
          pdfDoc = new PDFDocs();
          pdfDoc.setName(file.getName());
          pdfDoc.setPath(file.getAbsolutePath());
          pdfDocs.add(pdfDoc);
        }
      }
    }

    return pdfDocs;
    }
    }
  }