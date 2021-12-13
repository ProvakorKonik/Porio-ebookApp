package com.konik.porio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Category extends AppCompatActivity  implements RecylerviewClickInterface{
    private RecyclerView mCategoryRecyclerView;
    private CollectionReference notebookRef ;    //for quires of data
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<CategoryModel> listCategoryItem;
    CategoryAdapter mCategoryAdapter;

    private String dSectorType = "ACADEMIC", dCategoryUID = "NO",CategoryItemName = "NO",CategoryItemType = "NO";
    private String CategoryItemPriority = "NO",CateagoryItemTotalData = "NO",CategoryItemPhotoURL = "NO";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        mCategoryRecyclerView = (RecyclerView)findViewById(R.id.recycler_view_category);
        listCategoryItem = new ArrayList<>();

        ///CODE START
        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {
            dSectorType = intent.getExtras().getString("SECTOR");
            if (TextUtils.isEmpty(dSectorType)) {
                dSectorType= "ACADEMIC";
                Toast.makeText(Category.this, "SECTOR NULL" , Toast.LENGTH_SHORT).show();
            } else if (dSectorType.equals("")){
                dSectorType= "ACADEMIC";
                Toast.makeText(Category.this, "SECTOR NOT FOUND" , Toast.LENGTH_SHORT).show();
            }else{
                //if it here  it means it get proper book id and name
            }
        }else{
            dSectorType = "ACADEMIC";
            Toast.makeText(Category.this, "intent NULL" , Toast.LENGTH_SHORT).show();
        }
        LoadCategory(dSectorType);
    }

    private void LoadCategory(String type){     //CORRECT

            notebookRef = db.collection("All_Type").document("CATEGORY").collection(type);//.orderBy("time", Query.Direction.ASCENDING).limitToLast(3

            notebookRef.orderBy("priority", Query.Direction.ASCENDING).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {   //documnet er vitore je multiple document query ache er jonno for loop
                            String data = "";
                            //Collections.reverse(listBook);+
                            if(queryDocumentSnapshots.isEmpty()) {
                                Toast.makeText(Category.this,"No Category Found ",Toast.LENGTH_SHORT).show();
                                View parentLayout = findViewById(android.R.id.content);
                                Snackbar.make(parentLayout, "No Category Found", Snackbar.LENGTH_LONG)
                                        .setAction("CLOSE", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                            }
                                        })
                                        .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                                        .show();
                            }else {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    CategoryModel categoryModel = documentSnapshot.toObject(CategoryModel.class);
                                    //messageModel.setDocumentID(documentSnapshot.getId());
                                    dCategoryUID = documentSnapshot.getId();
                                    CategoryItemName = categoryModel.getName();
                                    CategoryItemType = categoryModel.getType();
                                    CategoryItemPriority = categoryModel.getPriority();
                                    CateagoryItemTotalData = categoryModel.getTotal_data();
                                    CategoryItemPhotoURL = categoryModel.getPhoto_url();

                                    listCategoryItem.add(new CategoryModel(dCategoryUID, CategoryItemName, CategoryItemType, CategoryItemPriority, CategoryItemPhotoURL, CateagoryItemTotalData));
                                    //Toast.makeText(getApplicationContext(), UsetMessageData+ " "+UserMessageTime, Toast.LENGTH_SHORT).show();
                                }
                            }
//                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
//                            linearLayoutManager.setStackFromEnd(true);
//                            mRecylerVieOldMessage.setLayoutManager(linearLayoutManager);
                            //Collections.reverse(listBook);
                            mCategoryAdapter = new CategoryAdapter(Category.this,listCategoryItem,Category.this);
                            mCategoryAdapter.notifyDataSetChanged();
                            mCategoryRecyclerView.setLayoutManager(new GridLayoutManager(Category.this,1));
                            mCategoryRecyclerView.setAdapter(mCategoryAdapter);
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
        dCategoryUID = listCategoryItem.get(position).getGetId();
        CategoryItemName = listCategoryItem.get(position).getName();
        if(!dCategoryUID.equals("NO") && !dSectorType.equals("NO")){
            Toast.makeText(this," "+CategoryItemName,Toast.LENGTH_SHORT).show();;
            Intent intent = new Intent(Category.this, BookList.class);
            intent.putExtra("Category_UID",dCategoryUID);
            intent.putExtra("Category_Name",CategoryItemName);
            intent.putExtra("SECTOR",dSectorType);
            startActivity(intent);
        }else{
            Toast.makeText(this," "+listCategoryItem.get(position).getName() + " UID 404",Toast.LENGTH_SHORT).show();;
        }

    }

    @Override
    public void onItemLongCLick(int position) {
        Toast.makeText(this,"XX"+listCategoryItem.get(position),Toast.LENGTH_SHORT).show();;
    }
    @Override
    public void onItemClickAuthorID(int position, String AuthorUID) {
        Toast.makeText(this,"XX"+listCategoryItem.get(position),Toast.LENGTH_SHORT).show();;
    }
}