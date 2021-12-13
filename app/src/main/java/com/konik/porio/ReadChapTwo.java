package com.konik.porio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ReadChapTwo extends AppCompatActivity {
    private Button mAddChapBtn;
    String dCategoryUID = "NO",dBookUID = "NO", dSectorType = "NO", dUserUID = "NO";

    int dChapterClickNo = 1;
    int dTotalChapter = 1;
    private TextView mReadStoryText;
    private TextView mReadStoryChapterName;
    private ImageView mReadStoryChapterImage;

    private LinearLayout mBottomLayout;
    private ImageView mImageTextPlus, mImageTextMinus,mImageTextLineSpace;
    private RecyclerView recyclerView;

    ///EXTRA
    boolean dShowTextBool = false;
    int dTextSize = 22;
    float dLineHeight = 8;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_chap_two);
        mAddChapBtn = (Button)findViewById(R.id.read_chap_two_add_chap_btn);
        mReadStoryText = (TextView)findViewById(R.id.read_chap_two_text);
        mReadStoryChapterName = (TextView)findViewById(R.id.read_chap_two_name);
        mReadStoryChapterImage = (ImageView) findViewById(R.id.read_chap_two_image);

        mBottomLayout = (LinearLayout)findViewById(R.id.readchap_layout_bottom);
        mImageTextPlus = (ImageView)findViewById(R.id.read_chap_two_imageTextPlus);
        mImageTextLineSpace = (ImageView)findViewById(R.id.read_chap_two_imageTextLineSpace);
        mImageTextMinus = (ImageView)findViewById(R.id.read_chap_two_imageTextMinus);
        recyclerView = findViewById(R.id.recycler_view_read_chap_two);


        mAddChapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReadChapTwo.this,ReadChapAdd.class);
                intent.putExtra("Book_UID",dBookUID);
                intent.putExtra("Category_UID",dCategoryUID);
                intent.putExtra("SECTOR",dSectorType);
                startActivity(intent);
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

        mImageTextLineSpace.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

            }
        });
        intentCollectionFunc();
    }
    private void intentCollectionFunc() {
        /*final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {
            dCategoryUID = intent.getExtras().getString("Category_UID");
            dBookUID = intent.getExtras().getString("Book_UID");
            dSectorType = intent.getExtras().getString("SECTOR");
            if (TextUtils.isEmpty(dCategoryUID)) {
                dCategoryUID= "NO";
                Toast.makeText(ReadChapTwo.this, "Category_UID NULL  " , Toast.LENGTH_SHORT).show();
            }
            if (dCategoryUID.equals("")){
                dCategoryUID= "NO";
                Toast.makeText(ReadChapTwo.this, "BOOK UID 404" , Toast.LENGTH_SHORT).show();
            }if (TextUtils.isEmpty(dSectorType)) {
                dSectorType= "NO";
                Toast.makeText(ReadChapTwo.this, "SectorType NULL  " , Toast.LENGTH_SHORT).show();
            }else if (dSectorType.equals("")){
                dSectorType= "NO";
                Toast.makeText(ReadChapTwo.this, "dSectorType 404" , Toast.LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(dBookUID)) {
                dBookUID= "NO";
                Toast.makeText(ReadChapTwo.this, "BOOK_UID NULL  " , Toast.LENGTH_SHORT).show();
            }else if (dBookUID.equals("")){
                dBookUID= "NO";
                Toast.makeText(ReadChapTwo.this, "Book UID 404" , Toast.LENGTH_SHORT).show();
            };
            LoadChapterRecyclerView(dSectorType,dCategoryUID,dBookUID);

        }else{
            dCategoryUID = "NO";
            dBookUID= "NO";
            dSectorType= "NO";
            Toast.makeText(ReadChapTwo.this, "Intent Not Found" , Toast.LENGTH_SHORT).show();
        }*/
    }
/*    private CollectionReference notebookRef ;    //for quires of data
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    
    List<ReadChapModel> listChapterListItem;
    ReadChapTwoAdapter mReadChapTwoAdapter;
    private void LoadChapterRecyclerView(String SectorType,String CategoryUID, String BookUID){     //CORRECT

        notebookRef =  db.collection("All_Type").document("Chapter").collection(SectorType).document(CategoryUID).collection(BookUID);//.orderBy("time", Query.Direction.ASCENDING).limitToLast(3

        notebookRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {   //documnet er vitore je multiple document query ache er jonno for loop
                        String data = "";
                        //Collections.reverse(listBook);
                        if(queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(ReadChapTwo.this,"No Book Found ",Toast.LENGTH_SHORT).show();
                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, "No Book Found", Snackbar.LENGTH_LONG)
                                    .setAction("CLOSE", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    })
                                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                                    .show();
                        }else {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                ReadChapModel ReadChapModel = documentSnapshot.toObject(ReadChapModel.class);
                                //messageModel.setDocumentID(documentSnapshot.getId());
                                String dChapterUID = documentSnapshot.getId();
                                String dChapterName = ReadChapModel.getChapterNmae();
                                String dChapterText = ReadChapModel.getChapterText();
                                String dChapterPhotoUrl = ReadChapModel.getChapterPhotoUrl();
                                String dChapterPriority = ReadChapModel.getChapterPriority();
                                //ReadChapModel(String chapterNmae, String chapterText, String chapterPhotoUrl, String chapterPriority)
                                listChapterListItem.add(new ReadChapModel(dChapterUID,dChapterName, dChapterText, dChapterPhotoUrl, dChapterPriority));
                                //Toast.makeText(getApplicationContext(), UsetMessageData+ " "+UserMessageTime, Toast.LENGTH_SHORT).show();
                            }
                        }
//                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
//                            linearLayoutManager.setStackFromEnd(true);
//                            mRecylerVieOldMessage.setLayoutManager(linearLayoutManager);
                        //Collections.reverse(listBook);
                        mReadChapTwoAdapter = new ReadChapTwoAdapter(ReadChapTwo.this,listChapterListItem,ReadChapTwo.this);
                        mReadChapTwoAdapter.notifyDataSetChanged();
                        recyclerView.setLayoutManager(new GridLayoutManager(ReadChapTwo.this,1));
                        recyclerView.setAdapter(mReadChapTwoAdapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        String BookUID = listChapterListItem.get(position).getGetId();
        *//*if(!BookUID.equals("NO") && !dCategoryUID.equals("NO") && !dCategoryNAME.equals("NO") && !dSectorType.equals("NO")){
            Toast.makeText(this," "+listChapterListItem.get(position).getName(),Toast.LENGTH_SHORT).show();;
            Intent intent = new Intent(BookList.this, BookInfo.class);
            intent.putExtra("Category_UID",dCategoryUID);
            intent.putExtra("Category_Name",dCategoryNAME);
            intent.putExtra("SECTOR",dSectorType);
            intent.putExtra("Book_UID",BookUID);
            startActivity(intent);
        }else{
            Toast.makeText(this," "+listChapterListItem.get(position).getName() + " UID 404",Toast.LENGTH_SHORT).show();;
        }*//*
    }

    @Override
    public void onItemLongCLick(int postion) {

    }
    */
}