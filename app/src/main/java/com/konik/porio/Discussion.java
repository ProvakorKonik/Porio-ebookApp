package com.konik.porio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Discussion extends AppCompatActivity {

    String dDiscussUID = "NO", dDiscussTopic = "NO", dDiscussAuthor = "NO",dUserUID = "NO";

    private TextView mDisucssTopicTextShow;
    private EditText mDiscussEditComment;
    private Button mDiscussCommentSent;

    ////Firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference noteRef; // = db.collection("xNotebook").document("MY First Note");

    //Recyclerview
    private CollectionReference comment_text_ref ;
    private DiscussionCommentAdapter comment_text_adapter;
    private DocumentSnapshot LastResult; //pagination

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);
        mDisucssTopicTextShow = (TextView)findViewById(R.id.discuss_show_text);
        mDiscussEditComment = (EditText)findViewById(R.id.discuss_comment_edit);
        mDiscussCommentSent = (Button)findViewById(R.id.discuss_comment_btn);
        ////////CODE
        final Intent intent = getIntent();
        if(intent.getExtras() != null) {
            dDiscussUID = intent.getExtras().getString("iDiscuss_UID");
            dDiscussTopic = intent.getExtras().getString("iDiscuss_TOPIC");
            dDiscussAuthor = intent.getExtras().getString("iDiscuss_AUTHOR");

            if (TextUtils.isEmpty(dDiscussUID)) {
                dDiscussUID = "NO";
            } else if (dDiscussUID.equals("")) {
                dDiscussUID = "NO";
            } else {
                //CORRECTLY FOUND THE KEY
            }

            if (TextUtils.isEmpty(dDiscussAuthor)) {
                dDiscussAuthor = "NO";
            } else if (dDiscussAuthor.equals("")) {
                dDiscussAuthor = "NO";
            } else {
                mDisucssTopicTextShow.setText(dDiscussTopic);
            }
        }
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    dUserUID = mAuth.getUid();

                }else{
                    dUserUID = "NO";
                    /*Toast.makeText(getApplicationContext(),"Not Logged in",Toast.LENGTH_SHORT).show();
                    //signOut();
                    finish();
                    Intent intent = new Intent(Discussion.this, LoginFirebase.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);*/
                }
            }
        };
        //////////////FINAL CODE START
        setupCommentRecyclerView();
        mDiscussCommentSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commet_word = mDiscussEditComment.getText().toString();
                if(commet_word.equals("")){
                    Toast.makeText(getApplicationContext(),"FILL UP ALL",Toast.LENGTH_SHORT).show();
                }else if(dUserUID.equals("NO")){
                    Toast.makeText(getApplicationContext(),"Please Login",Toast.LENGTH_SHORT).show();
                }else{
                    String dDate = String.valueOf(System.currentTimeMillis());
                    //String discuss_topic, String discuss_author, String discuss_id, String discuss_time, String discuss_condition, int like, int comment
                    DiscussionCommentModel discussionCommentModel = new DiscussionCommentModel(commet_word,dUserUID,dDate,"1A0L0C");
                    comment_text_ref = db.collection("All_Type").document("DCOMMENT").collection(dDiscussUID);
                    comment_text_ref.add(discussionCommentModel)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    mDiscussEditComment.setText("");
                                    /*comment_text_ref.document(Comment_UID).update("discuss_id", Comment_UID).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(Discussion.this,"Successfully Done", Toast.LENGTH_SHORT).show();
                                            mDiscussEditComment.setText("");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Discussion.this,"FAILLED UID", Toast.LENGTH_SHORT).show();
                                            mDiscussEditComment.setText("");
                                        }
                                    });*/
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Discussion.this,"Failled", Toast.LENGTH_SHORT).show();
                            mDiscussEditComment.setText("");
                        }
                    });

                }
            }
        });
    }
    private void setupCommentRecyclerView() {
        final Intent intent = getIntent();
        if(intent.getExtras() != null) {
            dDiscussUID = intent.getExtras().getString("iDiscuss_UID");
            dDiscussTopic = intent.getExtras().getString("iDiscuss_TOPIC");
            dDiscussAuthor = intent.getExtras().getString("iDiscuss_AUTHOR");

            if (TextUtils.isEmpty(dDiscussUID)) {
                dDiscussUID = "NO";
            } else if (dDiscussUID.equals("")) {
                dDiscussUID = "NO";
            } else {
                //CORRECTLY FOUND THE KEY
            }
        }

        comment_text_ref = db.collection("All_Type").document("DCOMMENT").collection(dDiscussUID);

        Query query = comment_text_ref.orderBy("c_time", Query.Direction.ASCENDING);



        FirestoreRecyclerOptions<DiscussionCommentModel> options_latest_book = new FirestoreRecyclerOptions.Builder<DiscussionCommentModel>()
                .setQuery(query,DiscussionCommentModel.class)
                .build();

        comment_text_adapter = new DiscussionCommentAdapter(options_latest_book);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_comment);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(comment_text_adapter);


        ////////////////////BUTTON MODE
        comment_text_adapter.replyBtnClickListener(new DiscussionCommentAdapter.ClickListenerPackage(){
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int postion,  String UserName, String UserPhotoURl) {
                String dCommnetUID = documentSnapshot.getId();
                DiscussionCommentModel dCommentModel = documentSnapshot.toObject(DiscussionCommentModel.class);

                String dCommetTopic= dCommentModel.getC_topic();
                String dCommentUserID = dCommentModel.getC_author();
                String dCommentTime = dCommentModel.getC_time();
                Toast.makeText(Discussion.this, "Comment "+dCommetTopic , Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(Discussion.this, DiscussionReply.class);
                intent.putExtra("iComment_TOPIC",dCommetTopic);
                intent.putExtra("iComment_UID",dCommnetUID);
                intent.putExtra("iComment_Time",dCommentTime);

                intent.putExtra("iComment_AuthorUID",dCommentUserID);
                intent.putExtra("iComment_AuthorName",UserName);
                intent.putExtra("iComment_AuthorPhotoURL",UserPhotoURl);
                startActivity(intent);

            }
        });
        //when click on user name its goes to user profile
        comment_text_adapter.userInfoBtnClickListener(new DiscussionCommentAdapter.ClickListenerPackage(){
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int postion, String UserName, String UserPhotoURL) {
                /*String dCommnetUID = documentSnapshot.getId();
                DiscussionCommentModel dCommentModel = documentSnapshot.toObject(DiscussionCommentModel.class);

                String dCommetTopic= dCommentModel.getC_topic();
                String dCommentUserID = dCommentModel.getC_author();
                String dCommentTime = dCommentModel.getC_time();
                //int dTotalLike = dCommentModel.ge;
                //int dTotalComment = dCommentModel.getComment();
                Toast.makeText(Discussion.this, "Comment Topic "+dCommetTopic , Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(Discussion.this, UserProfile.class);
                intent.putExtra("USER_ID",dCommentUserID);
                startActivity(intent);*/

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        comment_text_adapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        comment_text_adapter.stopListening();
    }

}