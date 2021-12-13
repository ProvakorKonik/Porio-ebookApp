package com.konik.porio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DiscussionAdd extends AppCompatActivity {

    String dRandomUID = "NO";
    String dUserUID = "NO";
    private TextView mTextViewHeading;
    private EditText mDiscussEdit;
    private Button mSubmitBtn;
    ////Firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference noteRef; // = db.collection("xNotebook").document("MY First Note");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_add);
        mTextViewHeading = (TextView)findViewById(R.id.discussion_head_text);
        mDiscussEdit = (EditText)findViewById(R.id.edit_discussion_text);
        mSubmitBtn = (Button)findViewById(R.id.discussion_submit_btn);
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    dUserUID = mAuth.getUid();

                }else{
                    Toast.makeText(getApplicationContext(),"Please Login !",Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(DiscussionAdd.this, MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putExtra("GoToLogin","True");
                    startActivity(intent);
                }
            }
        };
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDiscussEdit.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Please Type a Question",Toast.LENGTH_SHORT).show();
                }else {
                    String dDate = String.valueOf(System.currentTimeMillis());
                    String dDiscussionTopic = mDiscussEdit.getText().toString();
                    dRandomUID = UUID.randomUUID().toString();

                    Map<String, Object> discussion_map = new HashMap<>();
                    discussion_map.put("topic", dDiscussionTopic);
                    discussion_map.put("author", dUserUID); //map is done
                    discussion_map.put("time", dDate);
                    discussion_map.put("condition", "1A0B0C0D0E0F0G0H000L000C");//A = AllowPrivacy, L = LikeCount, C = CommentCount;
                    discussion_map.put("like", 0);
                    discussion_map.put("comment", 0);

                    // db.document("Notebook/My First Note")

                    db.collection("All_Type").document("DISCUSS").collection("ACategory").add(discussion_map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String UID = documentReference.getId(); //SETUPING THE UID
                            db.collection("All_Type").document("DISCUSS").collection("ACategory").document(UID).update("discuss_id", UID).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(DiscussionAdd.this, "Successfully Done", Toast.LENGTH_SHORT).show();
                                    mSubmitBtn.setText("SUBMITTED");
                                    mDiscussEdit.setText("");
                                    mSubmitBtn.setEnabled(false);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(DiscussionAdd.this, "FAILED", Toast.LENGTH_SHORT).show();
                                    mSubmitBtn.setText("FAILED TRY LATER");
                                    mDiscussEdit.setText("");
                                    mSubmitBtn.setEnabled(false);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(DiscussionAdd.this, "FAILED", Toast.LENGTH_SHORT).show();
                            mSubmitBtn.setText("FAILED TRY LATER");
                            mDiscussEdit.setText("");
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