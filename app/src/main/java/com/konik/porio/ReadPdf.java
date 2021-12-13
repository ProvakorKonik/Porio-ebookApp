package com.konik.porio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReadPdf extends AppCompatActivity {

    private PDFView mPDFView;
    private ProgressBar mProgressbar;
    private TextView mTextTotalPage;
    String dCategoryUID = "NO", dPDF_LINK = "NO", dBookUID = "NO";
    String dSummary = "NO", dPdfLink = "NO";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_pdf);
        mPDFView = (PDFView)findViewById(R.id.pdfView);
        mProgressbar = (ProgressBar) findViewById(R.id.mReadProgressBar);
        mTextTotalPage = (TextView) findViewById(R.id.textTotalPage);

        //////////////GET INTENT DATA
        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {
            dCategoryUID = intent.getExtras().getString("Category_UID");
            dPDF_LINK = intent.getExtras().getString("PDF_LINK");
            dBookUID = intent.getExtras().getString("Book_UID");

            if (TextUtils.isEmpty(dCategoryUID)) {
                dCategoryUID= "NO";
                Toast.makeText(ReadPdf.this, "Category_UID NULL  " , Toast.LENGTH_SHORT).show();
            }if (TextUtils.isEmpty(dBookUID)) {
                dBookUID= "NO";
                Toast.makeText(ReadPdf.this, "BOOK_UID NULL  " , Toast.LENGTH_SHORT).show();
            }if (TextUtils.isEmpty(dPDF_LINK)) {
                dPDF_LINK= "NO";
                Toast.makeText(ReadPdf.this, "dPDF_LINK NULL  " , Toast.LENGTH_SHORT).show();
            }

            if (dCategoryUID.equals("")){
                dCategoryUID= "NO";
                Toast.makeText(ReadPdf.this, "BOOK UID 404" , Toast.LENGTH_SHORT).show();
            }if (dBookUID.equals("")){
                dBookUID= "NO";
                Toast.makeText(ReadPdf.this, "Book UID 404" , Toast.LENGTH_SHORT).show();
            }if (dPDF_LINK.equals("")){
                dPDF_LINK= "NO";
                Toast.makeText(ReadPdf.this, "dPDF_LINK  404" , Toast.LENGTH_SHORT).show();
            }else{
                /////////////////////////INITIALIZE
                new RetrievePDFStream().execute(dPDF_LINK);
            }
        }else{
            dCategoryUID = "NO";
            dPDF_LINK = "NO";
            dBookUID= "NO";
            Toast.makeText(ReadPdf.this, "Intent Not Found" , Toast.LENGTH_SHORT).show();
        }



    }
    class RetrievePDFStream extends AsyncTask<String,Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream=null;

            try{

                URL urlx=new URL(strings[0]);
                HttpURLConnection urlConnection=(HttpURLConnection) urlx.openConnection();
                if(urlConnection.getResponseCode()==200){
                    inputStream=new BufferedInputStream(urlConnection.getInputStream());

                }
            }catch (IOException e){
                return null;
            }
            return inputStream;

        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            mPDFView.fromStream(inputStream).load();
            mProgressbar.setVisibility(View.GONE);
            int dPageNo = mPDFView.getCurrentPage();
            int dTotalPage = mPDFView.getPageCount();
            //mTextTotalPage.setText("Page : "+dPageNo+"/"+dTotalPage);
            mTextTotalPage.setText(" ");
        }

    }

}