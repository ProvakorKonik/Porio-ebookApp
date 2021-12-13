package com.konik.porio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.HashMap;
import java.util.Map;

public class S_DBCFilesAddYT extends AppCompatActivity {
    private YouTubePlayerView mYoutubePlayerTest;
    private Button mAddVideoBtn, mRetryBtn;
    String dVideoID = "NO",dBatchUID = "NO", dClassUID = "NO", dUserUID = "NO";

    //////////FIREBASE
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s__d_b_c_files_add_yt);
        mYoutubePlayerTest =(YouTubePlayerView)findViewById(R.id.sdbcf_add_youtube_player);
        mAddVideoBtn = (Button)findViewById(R.id.sdbcf_add_video_btn);
        mRetryBtn = (Button)findViewById(R.id.sdbcf_add_retry_btn);
        getLifecycle().addObserver(mYoutubePlayerTest);



        //////INTENT GET
        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {   dClassUID = intent.getExtras().getString("Class_UID");
            dBatchUID = intent.getExtras().getString("Batch_UID");
            dVideoID = intent.getExtras().getString("YoutubeID");
            if (TextUtils.isEmpty(dBatchUID)) {
                dBatchUID= "NO";
                Toast.makeText(S_DBCFilesAddYT.this, "CLASS UID NULL  " , Toast.LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(dClassUID)) {
                dClassUID= "NO";
                Toast.makeText(S_DBCFilesAddYT.this, "CLASS UID NULL  " , Toast.LENGTH_SHORT).show();
            }if (TextUtils.isEmpty(dVideoID)) {
                dVideoID= "NO";
                Toast.makeText(S_DBCFilesAddYT.this, "Y VideoID UID NULL  " , Toast.LENGTH_SHORT).show();
            }if (dClassUID.equals("")){
                dClassUID= "NO";
                Toast.makeText(S_DBCFilesAddYT.this, "CLASS UID 404" , Toast.LENGTH_SHORT).show();
            }if (dVideoID.equals("")){
                dVideoID= "NO";
                Toast.makeText(S_DBCFilesAddYT.this, "Y Video UID 404" , Toast.LENGTH_SHORT).show();
            }else{
                /////////////////////////CLASS UID FOUND
            }
        }else{
            dClassUID= "NO";
            dVideoID= "NO";
            Toast.makeText(S_DBCFilesAddYT.this, "Intent Not Found" , Toast.LENGTH_SHORT).show();
        }

        ///////////////AUTH CHECK
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    dUserUID = mAuth.getUid();
                }else{
                    Toast.makeText(getApplicationContext(),"Not Logged in",Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(S_DBCFilesAddYT.this, MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        };


        mYoutubePlayerTest.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(dVideoID, 0);
            }
        });
        mAddVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dDate = String.valueOf(System.currentTimeMillis());
                //Total FIle + Class Date + Uploaded Time
                Map<String, Object> file_info = new HashMap<>();
                file_info.put("name", "Youtube Video");
                file_info.put("link", dVideoID);
                file_info.put("size", "0");
                file_info.put("type", "ytbX.link");
                file_info.put("date", dDate);
                file_info.put("by", dUserUID);

                if(!dClassUID.equals("NO") && !dUserUID.equals("NO") && !dBatchUID.equals("NO")){
                    db.collection("All_Type").document("FILES").collection(dClassUID).add(file_info)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    //dRandomUID =  documentReference.getId().toString();
                                    //db.collection("DOCUMENT").document("BATCH").collection(dBookUID).document(dRandomUID).update("batch_uid",dRandomUID);
                                    Toast.makeText(S_DBCFilesAddYT.this,"Successfully Generated",Toast.LENGTH_SHORT).show();
                                    mAddVideoBtn.setText("UPLOADED");
                                    mAddVideoBtn.setEnabled(false);

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(S_DBCFilesAddYT.this,"FAILED to Add Data",Toast.LENGTH_SHORT).show();
                                    mAddVideoBtn.setText("FAILED");
                                    mAddVideoBtn.setEnabled(false);
                                }
                            });
                }else{
                    Toast.makeText(S_DBCFilesAddYT.this,"UID 4040",Toast.LENGTH_SHORT).show();
                }


            }
        });

        mRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(dClassUID)) {
                    dClassUID= "NO";
                    Toast.makeText(S_DBCFilesAddYT.this, "CLASS UID NULL  " , Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(dBatchUID)) {
                    dBatchUID= "NO";
                    Toast.makeText(S_DBCFilesAddYT.this, "CLASS UID NULL  " , Toast.LENGTH_SHORT).show();
                }
                if(!dBatchUID.equals("NO") && !dClassUID.equals("NO")){
                    Intent intent = new Intent(S_DBCFilesAddYT.this, S_DBCFilesAdd.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putExtra("Class_UID",dClassUID);
                    intent.putExtra("Batch_UID",dBatchUID);
                    startActivity(intent);
                }

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }
}