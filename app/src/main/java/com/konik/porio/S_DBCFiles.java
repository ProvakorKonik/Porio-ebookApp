package com.konik.porio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class S_DBCFiles extends AppCompatActivity  implements RecylerviewClickInterface {
    private Button mFilesAddBtn;
    private String dClassUID = "NO", dBatchUID = "NO";

    private RecyclerView mFileRecyclerView;
    //FIREBASE
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference fileListRef;    //for quires of data
    List<S_DBCFilesModel> listFilesItem;
    S_DBCFilesAdapter docsFileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s__d_b_c_files);
        mFilesAddBtn = (Button) findViewById(R.id.sdbc_files_add_btn);
        mFileRecyclerView = (RecyclerView) findViewById(R.id.sdbc_files_add_recyclerview);
        listFilesItem = new ArrayList<>();
        final Intent intent = getIntent();
        if (intent.getExtras() != null) {
            dClassUID = intent.getExtras().getString("Class_UID");
            dBatchUID = intent.getExtras().getString("Batch_UID");
            if (TextUtils.isEmpty(dClassUID)) {
                dClassUID = "NO";
                Toast.makeText(S_DBCFiles.this, "CLASS UID NULL  ", Toast.LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(dBatchUID)) {
                dBatchUID = "NO";
                Toast.makeText(S_DBCFiles.this, "Batch UID NULL  ", Toast.LENGTH_SHORT).show();
            }
            if (dClassUID.equals("")) {
                dClassUID = "NO";
                Toast.makeText(S_DBCFiles.this, "CLASS UID 404", Toast.LENGTH_SHORT).show();
            }
            if (dBatchUID.equals("")) {
                dBatchUID = "NO";
                Toast.makeText(S_DBCFiles.this, "BATCH UID 404", Toast.LENGTH_SHORT).show();
            }
        } else {
            dClassUID = "NO";
            dBatchUID = "NO";
            Toast.makeText(S_DBCFiles.this, "Intent Not Found", Toast.LENGTH_SHORT).show();
        }

        mFilesAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!dClassUID.equals("NO")) {
                    Intent intent = new Intent(S_DBCFiles.this, S_DBCFilesAdd.class);
                    intent.putExtra("Class_UID", dClassUID);
                    intent.putExtra("Batch_UID", dBatchUID);
                    startActivity(intent);
                } else {
                    Toast.makeText(S_DBCFiles.this, "Class UID 404", Toast.LENGTH_SHORT).show();
                    ;
                }
            }
        });
        LoadClassist(dClassUID);
    }

    private void LoadClassist(String ClassUID) {     //CORRECT
        fileListRef = db.collection("All_Type").document("FILES").collection(ClassUID);//.orderBy("time", Query.Direction.ASCENDING).limitToLast(3
        fileListRef.orderBy("date", Query.Direction.ASCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {   //documnet er vitore je multiple document query ache er jonno for loop
                        String data = "";
                        //Collections.reverse(listBook);
                        if (queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(S_DBCFiles.this, "No Data Found ", Toast.LENGTH_SHORT).show();
                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, "No Files Found.", Snackbar.LENGTH_LONG)
                                    .setAction("CLOSE", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    })
                                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                    .show();
                        } else {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                S_DBCFilesModel sDbcFilesModel = documentSnapshot.toObject(S_DBCFilesModel.class);
                                //messageModel.setDocumentID(documentSnapshot.getId());
                                String dFileUID = documentSnapshot.getId();
                                String dFileName = sDbcFilesModel.getName();
                                String dFileLink = sDbcFilesModel.getLink();
                                String dFileType = sDbcFilesModel.getType();
                                String dDate = sDbcFilesModel.getDate();
                                String dUploadedBy = sDbcFilesModel.getBy();
                                String dFileSize = sDbcFilesModel.getSize();

                                //String getId, String topic, String no, String date, String note, String by
                                listFilesItem.add(new S_DBCFilesModel(dFileUID, dFileName, dFileLink, dFileSize, dFileType, dDate, dUploadedBy));
                            }
                            docsFileAdapter = new S_DBCFilesAdapter(S_DBCFiles.this, listFilesItem, S_DBCFiles.this);
                            docsFileAdapter.notifyDataSetChanged();
                            mFileRecyclerView.setLayoutManager(new GridLayoutManager(S_DBCFiles.this, 1));
                            mFileRecyclerView.setAdapter(docsFileAdapter);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        String dFileName = listFilesItem.get(position).getName();
        String dFileURL = listFilesItem.get(position).getLink();
        downloadLocation(S_DBCFiles.this,dFileName, "jpeg", DIRECTORY_DOWNLOADS,dFileURL);

    }

    @Override
    public void onItemLongCLick(int postion) {

    }
    @Override
    public void onItemClickAuthorID(int position, String AuthorUID) {

    }

    public void downloadLocation(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);

            Toast.makeText(S_DBCFiles.this, "Downloading", Toast.LENGTH_SHORT).show();
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalFilesDir(context,destinationDirectory,fileName);
            downloadManager.enqueue(request);

    }
}