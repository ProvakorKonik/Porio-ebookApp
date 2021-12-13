package com.konik.porio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class S_DocsBatchClassAdd extends AppCompatActivity {
    private TextView mClassChapterName, mClassNO, mClassDate, mClassNote;
    private Button mCreateClassbtn;
    private String dChapterName, dClassNo, dClassDate, dClassNote;
    String dBatchUID = "NO";
    String dUserUID = "NO";
    //////////FIREBASE
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s__docs_batch_class_add);

        mClassChapterName = (TextView)findViewById(R.id.edit_dbc_chapter_name);
        mClassNO = (TextView)findViewById(R.id.edit_dbc_no);
        mClassDate = (TextView)findViewById(R.id.edit_dbc_date);
        mClassNote =  (TextView)findViewById(R.id.edit_dbc_note);
        mCreateClassbtn = (Button)findViewById(R.id.dbc_create_class_btn);

        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {   dBatchUID = intent.getExtras().getString("Batch_UID");
            if (TextUtils.isEmpty(dBatchUID)) {
                dBatchUID= "NO";
                Toast.makeText(S_DocsBatchClassAdd.this, "Batch UID NULL  " , Toast.LENGTH_SHORT).show();
            }else if (dBatchUID.equals("")){
                dBatchUID= "NO";
                Toast.makeText(S_DocsBatchClassAdd.this, "Batch UID 404" , Toast.LENGTH_SHORT).show();
            }else{
                /////////////////////////BATCH UID FOUND
            }
        }else{
            dBatchUID= "NO";
            Toast.makeText(S_DocsBatchClassAdd.this, "Intent Not Found" , Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(S_DocsBatchClassAdd.this, MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        };

        mCreateClassbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dChapterName = mClassChapterName.getText().toString();
                dClassNo = mClassNO.getText().toString();
                dClassDate = mClassDate.getText().toString();
                dClassNote = mClassNote.getText().toString();

                if(dUserUID.equals("NO") || dUserUID.equals("") || dUserUID == null || dBatchUID.equals("NO") || dBatchUID.equals("")|| dBatchUID == null){
                    Toast.makeText(S_DocsBatchClassAdd.this,"UID ERROR",Toast.LENGTH_SHORT).show();
                }else if(dChapterName.equals("") || dClassNo.equals("") || dClassDate.equals("") || dClassNo.equals("")){
                    Toast.makeText(S_DocsBatchClassAdd.this,"Fill up All",Toast.LENGTH_SHORT).show();
                }else{
                    String dDate = "0F"+String.valueOf(System.currentTimeMillis())+"C"+String.valueOf(System.currentTimeMillis())+"U";
                                //Total FIle + Class Date + Uploaded Time
                    Map<String, Object> create_class_info = new HashMap<>();
                    create_class_info.put("topic", dChapterName);
                    create_class_info.put("no", dClassNo);
                    create_class_info.put("date", dDate);
                    create_class_info.put("note", dClassNote);
                    create_class_info.put("by", dUserUID);

                    db.collection("All_Type").document("CLASS").collection(dBatchUID).add(create_class_info)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    //dRandomUID =  documentReference.getId().toString();
                                    //db.collection("DOCUMENT").document("BATCH").collection(dBookUID).document(dRandomUID).update("batch_uid",dRandomUID);
                                    Toast.makeText(S_DocsBatchClassAdd.this,"Successfully Generated",Toast.LENGTH_SHORT).show();
                                    mCreateClassbtn.setText("GENERATED");
                                    mClassChapterName.setText("");
                                    mClassNO.setText("");
                                    mClassDate.setText("");
                                    mClassNote.setText("");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(S_DocsBatchClassAdd.this,"FAILED TO Generate",Toast.LENGTH_SHORT).show();
                                    mCreateClassbtn.setText("FAILED");
                                }
                            });
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