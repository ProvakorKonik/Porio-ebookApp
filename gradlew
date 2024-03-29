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
            intent.getExtras().clear();

            if(dChapterPhotoUrl.equals(null)){
                mBtnSelectImage.setText("Uploading Withoutx Image");
            }else{
                mBtnSelectImage.setText("Uploading With Image");
                mBtnSelectImage.setEnabled(false);
            }
            /*
            if(!dBookUID.equals("")){
                Toast.makeText(SingleBookAddStories.this,"BOOK UID Found = "+dBookUID,Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(SingleBookAddStories.this,"BOOK UID Not Found = "+dBookUID,Toast.LENGTH_SHORT).show();
            }*/
        }

       /* mBtnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go with book uid
            }
        });

        mUploadStoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dChapterNmae = mChapterName.getText().toString();
                dChapterText = mChapterText.getText().toString();
                dChapterPriority = mChapterPrioriy.getText().toString();
                if(mChapterName.equals("") || mChapterText.equals("") || mChapterPrioriy.equals("")){
                    Toast.makeText(SingleBookAddStories.this,"Fill Up ALL",Toast.LENGTH_SHORT).show();
                }else{
                    if(!dChapterPhotoUrl.equals("NO")){
                        Toast.makeText(SingleBookAddStories.this,"Uploading with Image",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(SingleBookAddStories.this,"Uploading without Image",Toast.LENGTH_SHORT).show();
                    }

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference storyRef_db = db.collection("Chapter").document(dBookUID).collection("All_Chapter");  //for quires of data

                    SingleBookAddStoriesModel singleBookAddStoriesModel = new SingleBookAddStoriesModel(dChapterNmae,dChapterText,dChapterPhotoUrl,dChapterPriority);
                    storyRef_db.add(singleBookAddStoriesModel).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            mChapterName.setText("");
                            mChapterText.setText("");
                            Toast.makeText(SingleBookAddStories.this,"Successfully Uploaded",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SingleBookAddStories.this,"Failed ! "+e,Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }
        });*/
    }
}                                                                                                                                           