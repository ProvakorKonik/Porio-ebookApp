package com.konik.porio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;

public class S_DocsBatchAdd extends AppCompatActivity {

    String dBookUID = "NO";
    String dUserUID = "NO";

    String dAcademicName = "NO", dDepartmentName = "NO", dBatchNO = "NO", dSectionNO = "NO", dCourseTeacherName = "NO", dCourseTime = "NO";
    String dGenerateTime = "NO";
    String dRandomUID = "NO";
    boolean dUserHideBool;

    private TextView mAcademicNameText, mDepartmentNameText, mBatchNoText, mSectionNameText, mTeacherNameText, mCourseStartTime;
    private Button mUploadBtn;
    private Switch mUserHideSwitch;

    //////////FIREBASE
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s__docs_batch_add);

        //INITIALIZE
        mAcademicNameText = (TextView)findViewById(R.id.edit_dba_academic_name);
        mDepartmentNameText = (TextView)findViewById(R.id.edit_dba_dept_name);
        mBatchNoText = (TextView)findViewById(R.id.edit_dba_batch_no);
        mSectionNameText = (TextView)findViewById(R.id.edit_dba_section);
        mTeacherNameText = (TextView)findViewById(R.id.edit_dba_teacher_name);
        mCourseStartTime = (TextView)findViewById(R.id.edit_dba_course_start_month);
        mUploadBtn = (Button)findViewById(R.id.dba_batch_upload_btn);
        mUserHideSwitch = (Switch)findViewById(R.id.dba_user_hide_switch);

        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {
            dBookUID = intent.getExtras().getString("Book_UID");
            if (TextUtils.isEmpty(dBookUID)) {
                dBookUID= "NO";
                Toast.makeText(S_DocsBatchAdd.this, "BOOK UID NULL  " , Toast.LENGTH_SHORT).show();
            }else if (dBookUID.equals("")){
                dBookUID= "NO";
                Toast.makeText(S_DocsBatchAdd.this, "BOOK UID 404" , Toast.LENGTH_SHORT).show();
            }else{
                /////////////////////////BOOK UID FOUND
            }
        }else{
            dBookUID= "NO";
            Toast.makeText(S_DocsBatchAdd.this, "Intent Not Found" , Toast.LENGTH_SHORT).show();
        }
        ///////////////AUTH CHECK
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    dUserUID = mAuth.getUid();
                    checkAdminLevel();
                }else{
                    Toast.makeText(getApplicationContext(),"Please Login !",Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(S_DocsBatchAdd.this, MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putExtra("GoToLogin","True");
                    startActivity(intent);
                }
            }
        };
        /////////////////////UPDATE BTN
        mUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dAcademicName = mAcademicNameText.getText().toString();
                dDepartmentName = mDepartmentNameText.getText().toString();
                dBatchNO = mBatchNoText.getText().toString();
                dSectionNO = mSectionNameText.getText().toString();
                dCourseTeacherName = mTeacherNameText.getText().toString();
                dCourseTime = mCourseStartTime.getText().toString();
                dGenerateTime = String.valueOf(System.currentTimeMillis()) ;
                dUserHideBool = mUserHideSwitch.isChecked();

                if(dUserUID.equals("NO") || dBookUID.equals("NO") || dUserUID.equals("") || dBookUID.equals("") ){
                    Toast.makeText(S_DocsBatchAdd.this,"USER or BOOK UID MISSING",Toast.LENGTH_SHORT).show();
                }else if(dAcademicName.equals("NO") || dDepartmentName.equals("NO") || dBatchNO.equals("NO") || dCourseTeacherName.equals("NO") || dCourseTime.equals("NO")){
                    Toast.makeText(S_DocsBatchAdd.this,"Fill up All",Toast.LENGTH_SHORT).show();
                }else if (dAcademicName.equals("") || dDepartmentName.equals("") || dBatchNO.equals("") || dCourseTeacherName.equals("") || dCourseTime.equals("")){
                    Toast.makeText(S_DocsBatchAdd.this,"Fill up All",Toast.LENGTH_SHORT).show();
                }else{
                    //Sent data to firebase
                    //ERROR CHECK BOOK ID IS VALID
                    String dUserType = "1";
                    if(dUserHideBool == true){
                        dUserType = "1";
                    }else{
                        dUserType = "0";
                    }
                    dCourseTime = String.valueOf(System.currentTimeMillis());
                    dGenerateTime = dCourseTime;
                    String dBatchTotalData = "1T0L0C0V0F0C0P"+dCourseTime+"S"+dGenerateTime+"U";
                    //Type, Like, Comment, Views,totalFiles, totalClass PendingLevel, courseStartDate, courseUploadDate,
                    //dRandomUID = UUID.randomUUID().toString();
                    String dBatchName = dAcademicName+"#"+dDepartmentName+"#"+dBatchNO+"#"+dSectionNO;
                    Map<String, Object> batch_info = new HashMap<>();
                    batch_info.put("batch_name", dBatchName);
                    batch_info.put("uploaded_by", dUserType+dUserUID);//HideUserType + User UID
                    batch_info.put("teacher", dCourseTeacherName);
                    batch_info.put("total_data", dBatchTotalData);
                    batch_info.put("like", 0);


                    db.collection("All_Type").document("BATCH").collection(dBookUID).add(batch_info)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    //dRandomUID =  documentReference.getId().toString();
                                    //db.collection("DOCUMENT").document("BATCH").collection(dBookUID).document(dRandomUID).update("batch_uid",dRandomUID);
                                    Toast.makeText(S_DocsBatchAdd.this,"Successfully Generated",Toast.LENGTH_SHORT).show();
                                    mUploadBtn.setText("GENERATED");
                                    mAcademicNameText.setText("");
                                    mDepartmentNameText.setText("");
                                    mBatchNoText.setText("");
                                    mSectionNameText.setText("");
                                    mTeacherNameText.setText("");
                                    mCourseStartTime.setText("");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(S_DocsBatchAdd.this,"FAILED TO Generate",Toast.LENGTH_SHORT).show();
                                    mUploadBtn.setText("FAILED");
                                }
                            });
                }
            }
        });
    }

    private void checkAdminLevel() {
        //Get Data From Phone Memory
        SharedPreferences prefs = getSharedPreferences("PorioApp", MODE_PRIVATE);
        String dUID = prefs.getString("user_uid", "NO");//"No name defined" is the default value.
        int dAdminLevel = prefs.getInt("admin_level", 0); //0 is the default value.

        if(dAdminLevel < 5){
            //INITIALIZE
            Toast.makeText(S_DocsBatchAdd.this,"Please Level Up",Toast.LENGTH_SHORT).show();
            mAcademicNameText.setVisibility(View.GONE);
            mDepartmentNameText.setVisibility(View.GONE);
            mBatchNoText.setVisibility(View.GONE);
            mSectionNameText.setVisibility(View.GONE);
            mTeacherNameText.setVisibility(View.GONE);
            mCourseStartTime.setVisibility(View.GONE);
            mUploadBtn.setVisibility(View.GONE);
            mUserHideSwitch.setVisibility(View.GONE);
            Intent intent = new Intent(S_DocsBatchAdd.this,UserAdminRequ.class);
            intent.putExtra("type","Batch (5)");
            finish();
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }
}