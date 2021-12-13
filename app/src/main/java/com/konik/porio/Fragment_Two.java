package com.konik.porio;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Fragment_Two extends Fragment {

    String dCategoryUID = "ZOGSLn0cUvQbBJjkB2dv", dCategoryNAME = "Motivation", dBookUID = "NO", dSectorType = "USER", dUserUID = "NO";


    RecyclerView recyclerViewCategoryLink;
    RecyclerView recyclerViewBookShow;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    /////////FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page

    //Category Link List RecyclerView
    private CollectionReference category_link_ref ;
    private CategoryLinkAdapter categoryLinkAdapter;
    //BookShow RecyclerView
    private CollectionReference bookShow_ref ;
    private BookShowAdapter  bookShowAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_two, container, false);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // do your variables initialisations here except Views!!!
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    dUserUID = firebaseAuth.getUid();
                }else{
                    dUserUID = "NO";
                }
            }
        };

    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewCategoryLink = view.findViewById(R.id.recycler_categoryLink);
        recyclerViewCategoryLink.setHasFixedSize(false);//recyclerViewDiscussion.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCategoryLink.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));

        recyclerViewBookShow = view.findViewById(R.id.recycler_bookshow);
        recyclerViewBookShow.setHasFixedSize(false);
        recyclerViewBookShow.setLayoutManager(new GridLayoutManager(getContext(),2));

        //recyclerViewBookShow.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));



        setupCategoryListRecyclerView();
        setupBookShowRecyclerView(dCategoryUID);
    }

    private void setupBookShowRecyclerView(String ddCategoryUID) {
        bookShow_ref = db.collection("All_Type").document("BOOKS").collection(ddCategoryUID);
        Query query = bookShow_ref; //.orderBy("rating", Query.Direction.DESCENDING).limit(10);
        FirestoreRecyclerOptions<BookInfoModel> options_latest_book = new FirestoreRecyclerOptions.Builder<BookInfoModel>()
                .setQuery(query,BookInfoModel.class)
                .build();

        bookShowAdapter = new BookShowAdapter(options_latest_book);
        recyclerViewBookShow.setAdapter(bookShowAdapter);
        ////////////////////BUTTON MODE
        bookShowAdapter.latestCardViewonClick(new BookShowAdapter.ClickListenerPackage(){
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int postion, String dAuthorUID) {
                String BookUID = documentSnapshot.getId();
                if(!BookUID.equals("NO") && !dCategoryUID.equals("NO") && !dCategoryNAME.equals("NO") && !dSectorType.equals("NO")){
                    Intent intent = new Intent(getContext(), BookInfo.class);
                    intent.putExtra("Category_UID",dCategoryUID);
                    intent.putExtra("Category_Name",dCategoryNAME);
                    intent.putExtra("SECTOR",dSectorType);
                    intent.putExtra("Book_UID",BookUID);

                    intent.putExtra("Book_AUTHOR_UID",dAuthorUID);
                    intent.putExtra("UserUID",dUserUID);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(),"CODE 404",Toast.LENGTH_SHORT).show();;
                }
            }
        });
    }
    private void updateBookShowRecyclerView(String ddCategoryUID) {
        bookShow_ref = db.collection("All_Type").document("BOOKS").collection(ddCategoryUID);
        Query query = bookShow_ref; //.orderBy("rating", Query.Direction.DESCENDING).limit(10);
        FirestoreRecyclerOptions<BookInfoModel> options_latest_book = new FirestoreRecyclerOptions.Builder<BookInfoModel>()
                .setQuery(query,BookInfoModel.class)
                .build();

        bookShowAdapter.updateOptions(options_latest_book);
        recyclerViewBookShow.setAdapter(bookShowAdapter);
        ////////////////////BUTTON MODE
        bookShowAdapter.latestCardViewonClick(new BookShowAdapter.ClickListenerPackage(){
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int postion,String dAuthorUID) {
                String BookUID = documentSnapshot.getId();
                if(!BookUID.equals("NO") && !dCategoryUID.equals("NO") && !dCategoryNAME.equals("NO") && !dSectorType.equals("NO")){
                    Intent intent = new Intent(getContext(), BookInfo.class);
                    intent.putExtra("Category_UID",dCategoryUID);
                    intent.putExtra("Category_Name",dCategoryNAME);
                    intent.putExtra("SECTOR",dSectorType);
                    intent.putExtra("Book_UID",BookUID);

                    intent.putExtra("Book_AUTHOR_UID",dAuthorUID);
                    intent.putExtra("UserUID",dUserUID);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(),"CODE 404",Toast.LENGTH_SHORT).show();;
                }
            }
        });
    }


    private void setupCategoryListRecyclerView() {
        category_link_ref = db.collection("All_Type").document("CATEGORYLink").collection("AllCategoryInfo");
        Query query = category_link_ref; //.orderBy("rating", Query.Direction.DESCENDING).limit(10);
        FirestoreRecyclerOptions<CategoryLinkModel> options_latest_book = new FirestoreRecyclerOptions.Builder<CategoryLinkModel>()
                .setQuery(query,CategoryLinkModel.class)
                .build();

        categoryLinkAdapter = new CategoryLinkAdapter(options_latest_book);
        recyclerViewCategoryLink.setAdapter(categoryLinkAdapter);

        ////////////////////BUTTON MODE
        categoryLinkAdapter.OnClickDiscussionCard(new CategoryLinkAdapter.ClickListenerPackage(){
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int postion) {
                String dCategoryLinkUID = documentSnapshot.getId();
                dCategoryNAME = documentSnapshot.getString("name");
                dSectorType = documentSnapshot.getString("sector");
                dCategoryUID = documentSnapshot.getString("catUID");
                updateBookShowRecyclerView(dCategoryUID);
            }
        });
    }





    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        categoryLinkAdapter.startListening();
        bookShowAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
        categoryLinkAdapter.stopListening();
        bookShowAdapter.stopListening();
    }

}
