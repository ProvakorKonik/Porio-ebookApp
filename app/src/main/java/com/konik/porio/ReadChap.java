package com.konik.porio;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class ReadChap extends AppCompatActivity {
    private Button mAddChapBtn;
    String dCategoryUID = "NO",dBookUID = "NO", dSectorType = "NO", dUserUID = "NO";

    int dChapterClickNo = 1;
    int dTotalChapter = 1;
    private TextView mReadStoryText;
    private TextView mReadStoryChapterName;
    private ImageView mReadStoryChapterImage;

    private LinearLayout mBottomLayout;
    private ImageView mImageTextPlus, mImageTextMinus;
    private RecyclerView recyclerView;
    //FIREBASE
    /////////FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //private DocumentReference chapterRef = db.collection("xNotebook").document("MY First Note");

    //All chapter Collect to recycler view
    ReadChapAdapter readChapAdapter;
    ///EXTRA
    boolean dShowTextBool = false;
    int dTextSize = 22;
    float dLineHeight = 8;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_chap);
        mAddChapBtn = (Button)findViewById(R.id.read_chap_add_chap_btn);
        mReadStoryText = (TextView)findViewById(R.id.read_chap_text);
        mReadStoryChapterName = (TextView)findViewById(R.id.read_chap_name);
        mReadStoryChapterImage = (ImageView) findViewById(R.id.read_chap_image);

        mBottomLayout = (LinearLayout)findViewById(R.id.readchap_layout_bottom);
        mImageTextPlus = (ImageView)findViewById(R.id.imageTextPlus);

        mImageTextMinus = (ImageView)findViewById(R.id.imageTextMinus);
        recyclerView = findViewById(R.id.recycler_view_read_chap);
        intentCollectionFunc();
        setUpChapterRecyclerView();
        mAddChapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Checking the user is the author or not
                if (FirebaseAuth.getInstance().getCurrentUser() != null ) {
                    dUserUID = FirebaseAuth.getInstance().getUid();
                    db.collection("All_Type").document("BOOKS").collection(dCategoryUID).document(dBookUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                String dAuthorUID = documentSnapshot.getString("author");
                                if(dAuthorUID.equals(dUserUID)){
                                    Intent intent = new Intent(ReadChap.this,ReadChapAdd.class);
                                    intent.putExtra("Book_UID",dBookUID);
                                    intent.putExtra("Category_UID",dCategoryUID);
                                    intent.putExtra("SECTOR",dSectorType);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(ReadChap.this,"You are not The Author",Toast.LENGTH_SHORT).show();;
                                }
                            }else{
                                Toast.makeText(ReadChap.this,"Author is Missing!",Toast.LENGTH_SHORT).show();;
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ReadChap.this,"Database ERORR",Toast.LENGTH_SHORT).show();;
                        }
                    });
                }else{
                    Toast.makeText(ReadChap.this,"You are not The Author",Toast.LENGTH_SHORT).show();;
                }


            }
        });
        mReadStoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(dShowTextBool == false){
                    dShowTextBool = true;
                    mBottomLayout.setVisibility(View.GONE);
                }else{
                    dShowTextBool = false;
                    mBottomLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        mImageTextPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dTextSize = dTextSize + 2;
                mReadStoryText.setTextSize(dTextSize);
            }
        });
        mImageTextMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dTextSize = dTextSize - 2;
                mReadStoryText.setTextSize(dTextSize);
            }
        });


        delay();

    }
    public void setUpChapterRecyclerView(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference chapRef_db =  db.collection("All_Type").document("Chapter").collection(dSectorType).document(dCategoryUID).collection(dBookUID);  //for quires of data

        //////////RECYCLER VIEW
        Query query = chapRef_db.orderBy("chapterPriority",Query.Direction.ASCENDING);//.orderBy("publishDate", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<ReadChapModel> options = new FirestoreRecyclerOptions.Builder<ReadChapModel>()
                .setQuery(query,ReadChapModel.class)
                .build();


        readChapAdapter = new ReadChapAdapter(options);

        recyclerView.setHasFixedSize(true);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        //widht = matchparent and height = wrap in recycler view layout
        //recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(readChapAdapter);
        /////////////////////////Click On Recycler View///////////////////////////////



        readChapAdapter.chapterBtnClickListener(new ReadChapAdapter.ClickListenerPackageReadStory() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int postion) {
                String word = documentSnapshot.getString("chapterText");
                String word2 = documentSnapshot.getString("chapterNmae");
                String ImageUrl = documentSnapshot.getString("chapterPhotoUrl");
                dChapterClickNo = Integer.parseInt(documentSnapshot.getString("chapterPriority")) + 1;
                mReadStoryText.setText(word+"\n\n\n");
                mReadStoryChapterName.setText(word2);
                if(ImageUrl.equals("NO")){
                    mReadStoryChapterImage.setVisibility(View.GONE);
                }else{
                    mReadStoryChapterImage.setVisibility(View.VISIBLE);
                    Picasso.get().load(ImageUrl).into(mReadStoryChapterImage);

                }

            }
        });


    }
    private void intentCollectionFunc() {
        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {
                dCategoryUID = intent.getExtras().getString("Category_UID");
                dBookUID = intent.getExtras().getString("Book_UID");
                dSectorType = intent.getExtras().getString("SECTOR");
                if (TextUtils.isEmpty(dCategoryUID)) {
                    dCategoryUID= "NO";
                    Toast.makeText(ReadChap.this, "Category_UID NULL  " , Toast.LENGTH_SHORT).show();
                }
                if (dCategoryUID.equals("")){
                    dCategoryUID= "NO";
                    Toast.makeText(ReadChap.this, "BOOK UID 404" , Toast.LENGTH_SHORT).show();
                }if (TextUtils.isEmpty(dSectorType)) {
                    dSectorType= "NO";
                    Toast.makeText(ReadChap.this, "SectorType NULL  " , Toast.LENGTH_SHORT).show();
                }else if (dSectorType.equals("")){
                    dSectorType= "NO";
                    Toast.makeText(ReadChap.this, "dSectorType 404" , Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(dBookUID)) {
                    dBookUID= "NO";
                    Toast.makeText(ReadChap.this, "BOOK_UID NULL  " , Toast.LENGTH_SHORT).show();
                }else if (dBookUID.equals("")){
                    dBookUID= "NO";
                    Toast.makeText(ReadChap.this, "Book UID 404" , Toast.LENGTH_SHORT).show();
                }

        }else{
            dCategoryUID = "NO";
            dBookUID= "NO";
            dSectorType= "NO";
            Toast.makeText(ReadChap.this, "Intent Not Found" , Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        //mAuth.addAuthStateListener(mAuthListener);
        readChapAdapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        readChapAdapter.stopListening();
        //recentlyReadChapterSentToDb();
    }
    public void delay(){
        new CountDownTimer(3000, 1000) {
            public void onFinish() {
                int test = readChapAdapter.getItemCount();
                if(test == 0){
                    mReadStoryText.setText("No Chapter Added");
                }else{
                    mReadStoryChapterName.setText("");
                    mReadStoryChapterName.setVisibility(View.VISIBLE);
                    mReadStoryText.setText("Select Any Chapter");
                    Toast.makeText(ReadChap.this, "Found "+test , Toast.LENGTH_SHORT).show();

                }
            }

            public void onTick(long millisUntilFinished) {
                // millisUntilFinished    The amount of time until finished.
                mReadStoryText.setText("Wait : " + millisUntilFinished / 1000);
            }
        }.start();
    }
}