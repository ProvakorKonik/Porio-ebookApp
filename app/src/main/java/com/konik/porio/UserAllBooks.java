package com.konik.porio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Text;

public class UserAllBooks extends AppCompatActivity {
    private TextView mWaitText;
    private RecyclerView recyclerViewUserBooks;
    /////////FIREBASE
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference user_allbooks_ref ;
    private UserAllBooksAdapter userAllBooksAdapter;

    String dUserUID = "NO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_all_books);

        mWaitText =  (TextView)findViewById(R.id.user_all_books_wait_text);
        recyclerViewUserBooks =  findViewById(R.id.recyclerview_allbooks);
        recyclerViewUserBooks.setHasFixedSize(false);//recyclerViewUserBooks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUserBooks.setLayoutManager(new GridLayoutManager(UserAllBooks.this,1));
        //LOGIN CHECK
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    dUserUID = firebaseAuth.getUid();
                }else{
                    Toast.makeText(getApplicationContext(),"Not Logged in",Toast.LENGTH_SHORT).show();
                    //signOut(); //404
                    finish();
                    Intent intent = new Intent(UserAllBooks.this, MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        };
        delay();
        setupUserAllBooksRecycler();
    }
    private void setupUserAllBooksRecycler() {
        dUserUID = mAuth.getCurrentUser().getUid();
        user_allbooks_ref =  db.collection("USER_DATA").document("UserBOOKS").collection(dUserUID);
        Query query = user_allbooks_ref; //.orderBy("rating", Query.Direction.DESCENDING).limit(10);
        FirestoreRecyclerOptions<UserAllBooksModel> options_latest_book = new FirestoreRecyclerOptions.Builder<UserAllBooksModel>()
                .setQuery(query,UserAllBooksModel.class)
                .build();

        userAllBooksAdapter = new UserAllBooksAdapter(options_latest_book);
        userAllBooksAdapter.notifyDataSetChanged();
        recyclerViewUserBooks.setAdapter(userAllBooksAdapter);

        //for recycler view click item
        userAllBooksAdapter.addBtnClickListener(new UserAllBooksAdapter.ClickListenerPackage(){
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int postion, String dCategoryUID, String dCategoryNAME, String dSectorType) {
                final String dBookUID = documentSnapshot.getId();
                Intent intent = new Intent(UserAllBooks.this,ReadChapAdd.class);
                intent.putExtra("Book_UID",dBookUID);
                intent.putExtra("Category_UID",dCategoryUID);
                intent.putExtra("SECTOR",dSectorType);
                startActivity(intent);
            }
        });

        userAllBooksAdapter.viewBtnClickListener(new UserAllBooksAdapter.ClickListenerPackage() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int postion, final String dCategoryUID, final String dCategoryNAME, final String dSectorType) {
                final String dBookUID = documentSnapshot.getId();
                if(!dBookUID.equals("NO") && !dCategoryUID.equals("NO") && !dCategoryNAME.equals("NO") && !dSectorType.equals("NO")){

                    db.collection("All_Type").document("BOOKS").collection(dCategoryUID).document(dBookUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                String dAuthorUID = documentSnapshot.getString("author");
                                Intent intent = new Intent(UserAllBooks.this, BookInfo.class);
                                intent.putExtra("Category_UID",dCategoryUID);
                                intent.putExtra("Category_Name",dCategoryNAME);
                                intent.putExtra("SECTOR",dSectorType);
                                intent.putExtra("Book_UID",dBookUID);

                                intent.putExtra("Book_AUTHOR_UID",dAuthorUID);
                                intent.putExtra("UserUID",dUserUID);
                                startActivity(intent);
                            }else{
                                Toast.makeText(UserAllBooks.this,"AUTHOR NOT FOUND",Toast.LENGTH_SHORT).show();;
                            }
                        }
                    });


                }else{
                    Toast.makeText(UserAllBooks.this,"CODE 404",Toast.LENGTH_SHORT).show();;
                }
            }
        });
    }
    public void delay(){
        new CountDownTimer(3000, 1000) {
            public void onFinish() {
                int test = userAllBooksAdapter.getItemCount();
                if(test == 0){
                    mWaitText.setText("You Have no Book");
                }else{
                    mWaitText.setVisibility(View.GONE);
                    Toast.makeText(UserAllBooks.this, "Found "+test , Toast.LENGTH_SHORT).show();
                }
            }
            public void onTick(long millisUntilFinished) {
                // millisUntilFinished    The amount of time until finished.
                mWaitText.setText("Wait : " + millisUntilFinished / 1000);
            }
        }.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        userAllBooksAdapter.startListening();
    }
}