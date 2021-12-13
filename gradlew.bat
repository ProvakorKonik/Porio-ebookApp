package com.tron.anulipi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SingleBookAddStories extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference storyRef_db = db.collection("Chapter").document("STORYID").collection("All_Chapter");  //for quires of data

    ////////////////////////////////////////////////////////////
    private Button mBtnSelectImage, mUploadStoryBtn;
    private EditText mChapterName, mChapterText, mChapterPrioriy;

    private String dChapterNmae = "", dChapterText = "", dChapterPhotoUrl = "NO", dChapterPriority = "",dBookUID = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_book_add_stories);

        mBtnSelectImage = (Button)findViewById(R.id.add_story_photo_select_btn);
        mUploadStoryBtn = (Button)findViewById(R.id.add_story_upload_btn);
        mChapterName = (EditText)findViewById(R.id.edit_add_chapter_name);
        mChapterText = (EditText)findViewById(R.id.edit_add_story_text);
        mChapterPrioriy = (EditText)findViewById(R.id.edit_add_story_priority_no);
////////////////////////////////////////////////////////////////////////////////
        final Intent intent = getIntent();
        if(intent.getExtras() != null){
            dChapterPhotoUrl = intent.getExtras().getString("PhotoURL");
            dBookUID = intent.getExtras().getString("BOOK_UID");
            //intent.getExtras().clear();

            if(dChapterPhotoUrl.isEmpty()){
                mBtnSelectImage.setText("Uploading Withoutx Image");
            }else{
                mBtn