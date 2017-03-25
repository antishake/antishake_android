package io.github.antishake;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dell pc on 25-03-2017.
 */

public class CustomAdapter extends BaseAdapter {

  Context c;
  ArrayList<PDFDocs> pdfDocs;

  public CustomAdapter(Context c, ArrayList<PDFDocs> pdfDocs) {
    this.c = c;
    this.pdfDocs = pdfDocs;
  }
        @Override
       public int getCount() {
       return pdfDocs.size();
  }
  @Override
  public Object getItem(int i) {
    return pdfDocs.get(i);
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  @Override
  public View getView(int i, View view, ViewGroup viewGroup) {
   if (view==null) {
        //INFLATE CUSTOM LAYOUT
     view = LayoutInflater.from(c).inflate(R.layout.fragment_textfile, viewGroup, false); //look
   }
    final PDFDocs pdfDocs = (PDFDocs) this.getItem(i);
    TextView nameTxt = (TextView) view.findViewById(R.id.nameTxt);
    ImageView img = (ImageView) view.findViewById(R.id.pdfImage);
    // BIND DATA
    nameTxt.setText(pdfDocs.getName());
    //img.setImageResource(R.drawable.pdf_icon);


    // VIEW CLICKED ITEM
    view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        openPDFView(pdfDocs.getPath());
      }
    });
    return view;
  }


    //OPEN FOR VIEW
  public void openPDFView(String path)
  {
    Intent i= new Intent(c, TextReader.class);
    i.putExtra("Path", path);
    c.startActivity(i);
  }
}


