package com.konik.porio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookList extends AppCompatActivity  implements RecylerviewClickInterface {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page

    private CollectionReference notebookRef ;    //for quires of data
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private RecyclerView mBookListRecyclerView;
    List<BookInfoModel> listBookListItem;

    BookListAdapter mBookListAdapter;
    private String dCategoryUID = "NO", dCategoryNAME = "NO",dSectorType = "NO";
    private String dUserUID = "NO",dBookAuthorUID = "NO";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        mBookListRecyclerView = (RecyclerView)findViewById(R.id.recyclerview_book_list);
        listBookListItem = new ArrayList<>();

        //////////////GET INTENT DATA
        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {
            dCategoryUID = intent.getExtras().getString("Category_UID");
            dCategoryNAME = intent.getExtras().getString("Category_Name");
            dSectorType = intent.getExtras().getString("SECTOR");
            if (TextUtils.isEmpty(dSectorType)) {
                dSectorType= "NO";
                Toast.makeText(BookList.this, "SectorType NULL  " , Toast.LENGTH_SHORT).show();
            } else if (dSectorType.equals("")){
                dSectorType= "NO";
                Toast.makeText(BookList.this, "dSectorType 404" , Toast.LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(dCategoryUID)) {
                dCategoryUID= "NO";
                Toast.makeText(BookList.this, "Category_UID NULL  " , Toast.LENGTH_SHORT).show();
            } else if (dCategoryUID.equals("")){
                dCategoryUID= "NO";
                Toast.makeText(BookList.this, "Category_Name 404" , Toast.LENGTH_SHORT).show();
            }else{
                //Toast.makeText(All_Books.this, "Category_Name Found  " , Toast.LENGTH_SHORT).show();
                LoadCategory(dCategoryUID);
            }
        }else{
            dCategoryUID = "NO";
            dSectorType = "NO";
            dCategoryNAME  = "NO";
        }

        mAuth = FirebaseAuth.getInstance();     //AUTH CHECKING
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    dUserUID = FirebaseAuth.getInstance().getUid();
                    //Toast.makeText(BookList.this,"USER LOGGED IN",Toast.LENGTH_SHORT).show();
                }else{
                    dUserUID = "NO";
                    //Toast.makeText(BookList.this,"USER NOT LOGGED IN",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void LoadCategory(String CategoryUID){     //CORRECT

        notebookRef = db.collection("All_Type").document("BOOKS").collection(CategoryUID);//.orderBy("time", Query.Direction.ASCENDING).limitToLast(3

        notebookRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {   //documnet er vitore je multiple document query ache er jonno for loop
                        String data = "";
                        //Collections.reverse(listBook);
                        if(queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(BookList.this,"No Book Found ",Toast.LENGTH_SHORT).show();
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
                                BookInfoModel bookInfoModel = documentSnapshot.toObject(BookInfoModel.class);
                                //messageModel.setDocumentID(documentSnapshot.getId());
                                String dBookUID = documentSnapshot.getId();
                                String dBookName = bookInfoModel.getName();
                                String dBookCourseCode = bookInfoModel.getCode();
                                dBookAuthorUID = bookInfoModel.getAuthor();
                                String dBookSummary = bookInfoModel.getSummary();
                                String dBookTotalData = bookInfoModel.getTotal_data();
                                String dBookPhotoUrl = bookInfoModel.getPhotoURL();
                                String dPdfLink = bookInfoModel.getPdf();

                                //String name, String code, String summary, String author, String total_data, String photoURL
                                listBookListItem.add(new BookInfoModel(dBookUID, dBookName, dBookCourseCode, dBookSummary, dBookAuthorUID, dBookTotalData, dBookPhotoUrl, dPdfLink));
                                //Toast.makeText(getApplicationContext(), UsetMessageData+ " "+UserMessageTime, Toast.LENGTH_SHORT).show();
                            }
                        }
//                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
//                            linearLayoutManager.setStackFromEnd(true);
//                            mRecylerVieOldMessage.setLayoutManager(linearLayoutManager);
                        //Collections.reverse(listBook);
                        mBookListAdapter = new BookListAdapter(BookList.this,listBookListItem,BookList.this);
                        mBookListAdapter.notifyDataSetChanged();
                        mBookListRecyclerView.setLayoutManager(new GridLayoutManager(BookList.this,1));
                        mBookListRecyclerView.setAdapter(mBookListAdapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    public void onItemClickAuthorID(int position, String AuthorUID) {
        String BookUID = listBookListItem.get(position).getGetId();
        String dAuthorUID = listBookListItem.get(position).getAuthor();
        if(!BookUID.equals("NO") && !dCategoryUID.equals("NO") && !dCategoryNAME.equals("NO") && !dSectorType.equals("NO")){
            //Toast.makeText(this," "+listBookListItem.get(position).getName(),Toast.LENGTH_SHORT).show();;
            Toast.makeText(this," "+dAuthorUID,Toast.LENGTH_SHORT).show();;
            Intent intent = new Intent(BookList.this, BookInfo.class);
            intent.putExtra("Category_UID",dCategoryUID);
            intent.putExtra("Category_Name",dCategoryNAME);
            intent.putExtra("SECTOR",dSectorType);
            intent.putExtra("Book_UID",BookUID);

            intent.putExtra("Book_AUTHOR_UID",dAuthorUID);
            intent.putExtra("UserUID",dUserUID);
            startActivity(intent);  //This intent also connected with Fregment TWO, USER ALL BOOOKS
        }else{
            Toast.makeText(this," "+listBookListItem.get(position).getName() + " UID 404",Toast.LENGTH_SHORT).show();;
        }
    }

    @Override
    public void onItemLongCLick(int postion) {

    }
    @Override
    public void onItemClick(int postion) {

    }
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}