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
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DiscussionReply extends AppCompatActivity {
    int dTotalLike = 0; int dTotalComment = 0;
    String dCommentTopic = "NO", dCommentUSERUID = "NO", dComment_Time = "NO",dCommentUID = "NO",dUserUID = "NO", dCommentAuthorName = "NO", dCommentAuhorPhotoURL = "NO";
    String dReply_text = "NO";

    private ImageView mBossImage, mBossThumsUp;
    private TextView mBossName, mBossTextTopic, mBossTextTime, mBossTextLikeBtn, mBossTextReplyBtn, mBossTotalLike;
    private Button mReplyBtn;
    private EditText mReplyEditText;


    ////FIREBASE
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference user_data_ref = db.collection("USER_DATA").document("REGISTER");
    private CollectionReference reply_text_Ref; // = db.collection("xNotebook").document("MY First Note");
    private DiscussionReplyAdapter reply_text_adapter;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_reply);


        mBossImage = (ImageView)findViewById(R.id.reply_boss_img);
        mBossThumsUp = (ImageView)findViewById(R.id.reply_boss_thumsup);
        mBossName = (TextView)findViewById(R.id.reply_boos_name);
        mBossTextTopic = (TextView)findViewById(R.id.reply_boos_text_topic);
        mBossTextTime = (TextView)findViewById(R.id.reply_boos_time);
        mBossTextLikeBtn = (TextView)findViewById(R.id.reply_boos_like_txt_btn);
        mBossTextReplyBtn = (TextView)findViewById(R.id.reply_boss_reply_txt_btn);
        mBossTotalLike = (TextView)findViewById(R.id.reply_boss_total_like);

        mReplyBtn = (Button)findViewById(R.id.reply_send_btn);
        mReplyEditText = (EditText)findViewById(R.id.reply_edit);

        final Intent intent = getIntent();
        if(intent.getExtras() != null) {
            dCommentTopic = intent.getExtras().getString("iComment_TOPIC");
            dCommentUSERUID = intent.getExtras().getString("iComment_AuthorUID");
            dCommentUID = intent.getExtras().getString("iComment_UID");
            dComment_Time = intent.getExtras().getString("iComment_Time");
            dCommentAuthorName = intent.getExtras().getString("iComment_AuthorName");
            dCommentAuhorPhotoURL = intent.getExtras().getString("iComment_AuthorPhotoURL");


            if (TextUtils.isEmpty(dCommentUSERUID)) {
                dCommentUSERUID = "NO";
            } else if (dCommentUSERUID.equals("")) {
                dCommentUSERUID = "NO";
            } else {
                //CORRECTLY FOUND THE KEY
            }
            if (TextUtils.isEmpty(dCommentUID)) {
                dCommentUID = "NO";
            } else if (dCommentUID.equals("")) {
                dCommentUID = "NO";
            } else {
                //CORRECTLY FOUND THE KEY
            }

            if (TextUtils.isEmpty(dCommentTopic)) {
                dCommentTopic = "NO";
            } else if (dCommentTopic.equals("")) {
                dCommentTopic = "NO";
            } else {
                //CORRECTLY FOUND THE KEY
            }

            if (TextUtils.isEmpty(dCommentAuhorPhotoURL)) {
                dCommentAuhorPhotoURL = "NO";
            } else if (dCommentAuhorPhotoURL.equals("")) {
                dCommentAuhorPhotoURL = "NO";
            } else if (dCommentAuhorPhotoURL == null ){
                dCommentAuhorPhotoURL = "NO";
            }else {
                //CORRECTLY FOUND THE KEY
            }



        }
        mBossTextTopic.setText(dCommentTopic);
        //mBossTextTime.setText(TimeAgo(dComment_Time));
        mBossTotalLike.setText(String.valueOf(dTotalLike));
        mBossTotalLike.setText(String.valueOf(dTotalComment));
        /////FIREBASE GET BOSS NAME AND URL
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    dUserUID = mAuth.getUid();
                }else{
                    Toast.makeText(getApplicationContext(),"Not Logged in",Toast.LENGTH_SHORT).show();
                    //signOut();
                    /*finish();
                    Intent intent = new Intent(DiscussionReply.this, LoginFirebase.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);*/
                }
            }
        };
        mBossName.setText(dCommentAuthorName);
        if(dCommentAuhorPhotoURL.equals("NO")){

        }else{
            Picasso.get().load(dCommentAuhorPhotoURL).into(mBossImage);
        }
        /*user_data_ref.collection("NORMAL_USER").document(dCommentUSERUID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String UserName =documentSnapshot.getString("user_name");
                            String UserPhotoUrl = documentSnapshot.getString("user_PhotoURL");



                        }else{
                            mBossName.setText("Name Not FOUND");
                        }
                    }
                });*/
        mReplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 dReply_text = mReplyEditText.getText().toString();
                if(dReply_text.equals("")){
                    Toast.makeText(getApplicationContext(),"FILL UP ALL",Toast.LENGTH_SHORT).show();
                }else if(dUserUID.equals("NO")){
                    Toast.makeText(getApplicationContext(),"Please Login",Toast.LENGTH_SHORT).show();
                }else {
                    String dDate = String.valueOf(System.currentTimeMillis());
                    //String discuss_topic, String discuss_author, String discuss_id, String discuss_time, String discuss_condition, int like, int comment
                    DiscussionReplyModel discussionReplyModel = new DiscussionReplyModel(dReply_text,dUserUID,dDate,"1A0L0C");

                    reply_text_Ref = db.collection("All_Type").document("DC_REPLY").collection(dCommentUID);
                    reply_text_Ref.add(discussionReplyModel)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    String Reply_UID = documentReference.getId();
                                    Toast.makeText(DiscussionReply.this,"Successfully Done", Toast.LENGTH_SHORT).show();
                                    mReplyEditText.setText("");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DiscussionReply.this,"Failled", Toast.LENGTH_SHORT).show();
                            mReplyEditText.setText("");
                        }
                    });

                }
            }
        });
        setupReplyRecyclerView();
    }
    private void setupReplyRecyclerView() {
        final Intent intent = getIntent();
        if(intent.getExtras() != null) {
            dCommentTopic = intent.getExtras().getString("iComment_TOPIC");
            dCommentUSERUID = intent.getExtras().getString("iComment_AuthorUID");
            dCommentUID = intent.getExtras().getString("iComment_UID");
            dComment_Time = intent.getExtras().getString("iComment_Time");
            dCommentAuthorName = intent.getExtras().getString("iComment_AuthorName");
            dCommentAuhorPhotoURL = intent.getExtras().getString("iComment_AuthorPhotoURL");

            if (TextUtils.isEmpty(dCommentUSERUID)) {
                dCommentUSERUID = "NO";
            } else if (dCommentUSERUID.equals("")) {
                dCommentUSERUID = "NO";
            } else {
                //CORRECTLY FOUND THE KEY
            }
            if (TextUtils.isEmpty(dCommentUID)) {
                dCommentUID = "NO";
            } else if (dCommentUID.equals("")) {
                dCommentUID = "NO";
            } else {
                //CORRECTLY FOUND THE KEY
            }
            if (TextUtils.isEmpty(dCommentAuhorPhotoURL)) {
                dCommentAuhorPhotoURL = "NO";
            } else if (dCommentAuhorPhotoURL.equals("")) {
                dCommentAuhorPhotoURL = "NO";
            } else if (dCommentAuhorPhotoURL == null ){
                dCommentAuhorPhotoURL = "NO";
            }else {
                //CORRECTLY FOUND THE KEY
            }
        }

        reply_text_Ref = db.collection("All_Type").document("DC_REPLY").collection(dCommentUID);
        Query query = reply_text_Ref.orderBy("r_time", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<DiscussionReplyModel> options_latest_book = new FirestoreRecyclerOptions.Builder<DiscussionReplyModel>()
                .setQuery(query,DiscussionReplyModel.class)
                .build();

        reply_text_adapter = new DiscussionReplyAdapter(options_latest_book);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_reply);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(reply_text_adapter);


        ////////////////////BUTTON MODE
        /*reply_text_adapter.replyBtnClickListener(new DiscussionCommentAdapter.ClickListenerPackage(){
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int postion) {
                String dCommnetUID = documentSnapshot.getId();
                DiscussionModel dCommentModel = documentSnapshot.toObject(DiscussionModel.class);

                String dCommetTopic= dCommentModel.getDiscuss_topic();
                String dCommentUserID = dCommentModel.getDiscuss_author();
                String dCommentTime = dCommentModel.getDiscuss_time();
                int dTotalLike = dCommentModel.getLike();
                int dTotalComment = dCommentModel.getComment();
                Toast.makeText(Discussion.this, "Total Comment "+dTotalComment , Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(Discussion.this, DiscussionReply.class);
                intent.putExtra("iComment_TOPIC",dCommetTopic);
                intent.putExtra("iComment_AuthorUID",dCommentUserID);
                intent.putExtra("iComment_UID",dCommnetUID);
                intent.putExtra("iComment_Time",dCommentTime);
                intent.putExtra("iTotalLike",dTotalLike);
                intent.putExtra("iTotalComment",dTotalComment);
                startActivity(intent);

            }
        });*/
    }

    private String TimeAgo(String dtime){
        try
        {
            Long currentTime = Long.parseLong(dtime);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
            Date date = new Date(currentTime);
            String time = simpleDateFormat.format(date);

            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
            Date past = format.parse(time);
            Date now = new Date();
            long seconds= TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes=TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours=TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days=TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

            if(seconds<60)
                dtime = seconds+" seconds ago";
            else if(minutes<60)
                dtime = minutes+" minutes ago";
            else if(hours<24)
                dtime = hours+" hours ago";
            else
                dtime = days+" days ago";


        }
        catch (Exception j){
            j.printStackTrace();
        }

        return dtime;
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        reply_text_adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        reply_text_adapter.stopListening();
    }
}