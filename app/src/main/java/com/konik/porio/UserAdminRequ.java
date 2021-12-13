package com.konik.porio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.lang.UCharacterEnums;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserAdminRequ extends AppCompatActivity {
    private TextView mHeadText, mNoticeText;
    private EditText mEditMsgType;
    private CheckBox mAcceptCheckbox;
    private Button mSentBtn;
    String dWantToAdd = "NO", dUserUID = "NO", dUserName = "NO", dTotal = "NO";
    int dAdminLevel = 0;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_admin_requ);
        mHeadText = (TextView)findViewById(R.id.user_admin_requ_headtext);
        mNoticeText = (TextView)findViewById(R.id.user_admin_requ_texnotice);
        mEditMsgType = (EditText)findViewById(R.id.user_admin_requ_editMsg);
        mAcceptCheckbox = (CheckBox)findViewById(R.id.user_admin_requ_checkbox);
        mSentBtn = (Button)findViewById(R.id.user_admin_requ_sentBtn);


        ///////////////AUTH CHECK
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    dUserUID = mAuth.getUid();
                    db.collection("USER_DATA").document("REGISTER").collection("NORMAL_USER").document(dUserUID).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()) {
                                        dUserName = documentSnapshot.getString("name");
                                        dTotal = documentSnapshot.getString("total");
                                        dAdminLevel = dTotal.charAt(0)-48;

                                        final Intent intent = getIntent();
                                        if(intent.getExtras() != null)
                                        {
                                            dWantToAdd = intent.getExtras().getString("type");
                                            if (TextUtils.isEmpty(dWantToAdd)){
                                                dWantToAdd = "NO";
                                            }
                                            else if (dWantToAdd.equals("")){
                                                dWantToAdd = "NO";
                                            }
                                            else{
                                                mEditMsgType.setText("Hi Brother,\nMy name is "+dUserName+ "["+dAdminLevel+"]. I want to be an admin. I will add "+dWantToAdd+ " and many more. It will help us to grow our community. I promised that I will never do any unethical activity. \n");
                                            }
                                        }else
                                            mEditMsgType.setText("Hi Brother,\nMy name is "+dUserName+ "["+dAdminLevel+"]. I want to be an admin. I will add Something. It will help us to grow our community. I promised that I will never do any unethical activity. \n");

                                    }else{
                                        Toast.makeText(getApplicationContext(),"User 404",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    Toast.makeText(getApplicationContext(),"Please Login !",Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(UserAdminRequ.this, MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putExtra("GoToLogin","True");
                    startActivity(intent);
                }
            }
        };



        mSentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAcceptCheckbox.isChecked()){
                    if(mEditMsgType.getText().toString().isEmpty()){
                        Toast.makeText(UserAdminRequ.this,"Please Type msg",Toast.LENGTH_SHORT).show();
                    }else{
                        String dTime = String.valueOf(System.currentTimeMillis());
                        Map<String, Object> admin_info = new HashMap<>();
                        admin_info.put("user_uid", dUserUID);
                        admin_info.put("user_total", dTotal);//HideUserType + User UID
                        admin_info.put("date", dTime);

                        db.collection("All_Type").document("AdminRequ").collection("2021").add(admin_info)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(UserAdminRequ.this,"Successfully Submitted",Toast.LENGTH_SHORT).show();
                                        mSentBtn.setVisibility(View.GONE);
                                        mNoticeText.setTextSize(20);
                                        mNoticeText.setText("SUBMITTED");
                                        mEditMsgType.setVisibility(View.GONE);
                                        mAcceptCheckbox.setVisibility(View.GONE);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UserAdminRequ.this,"FAILED TO Generate",Toast.LENGTH_SHORT).show();
                                        mSentBtn.setText("FAILED");
                                    }
                                });
                    }
                }else{
                    Toast.makeText(UserAdminRequ.this,"Please Select the Check Box",Toast.LENGTH_SHORT).show();
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