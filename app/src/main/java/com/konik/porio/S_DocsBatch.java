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
import android.widget.Button;
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
import java.util.List;

public class S_DocsBatch extends AppCompatActivity implements RecylerviewClickInterface{
    private Button mDocsBatchAddBtn;
    String dBookUID = "NO";
    private RecyclerView mBatchRecyclerview;

    //FIREBASE
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef ;    //for quires of data
    List<S_DocsBatchModel> listBatchListItem;
    S_DocsBatchAdapter docsBatchAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s__docs_batch);
        mDocsBatchAddBtn = (Button)findViewById(R.id.docs_batch_add_batchbtn);
        listBatchListItem = new ArrayList<>();
        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {
            dBookUID = intent.getExtras().getString("Book_UID");
            if (TextUtils.isEmpty(dBookUID)) {
                dBookUID= "NO";
                Toast.makeText(S_DocsBatch.this, "BOOK UID NULL  " , Toast.LENGTH_SHORT).show();
            }else if (dBookUID.equals("")){
                dBookUID= "NO";
                Toast.makeText(S_DocsBatch.this, "BOOK UID 404" , Toast.LENGTH_SHORT).show();
            }else{
                /////////////////////////BOOK UID FOUND

            }
        }else{
            dBookUID= "NO";
            Toast.makeText(S_DocsBatch.this, "Intent Not Found" , Toast.LENGTH_SHORT).show();
        }


        mDocsBatchAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!dBookUID.equals("") && !dBookUID.equals("NO") && dBookUID != null){
                    Intent intent = new Intent(S_DocsBatch.this,S_DocsBatchAdd.class);
                    intent.putExtra("Book_UID",dBookUID);
                    startActivity(intent);
                }else{
                    Toast.makeText(S_DocsBatch.this, "BOOK UID 404" , Toast.LENGTH_SHORT).show();
                }

            }
        });

        //////////////RECYCLER VIEW
        mBatchRecyclerview = (RecyclerView)findViewById(R.id.recyclerview_batchlist);
        LoadBatchList(dBookUID);
    }
    private void LoadBatchList(String BookUID){     //CORRECT
        notebookRef = db.collection("All_Type").document("BATCH").collection(BookUID);
        notebookRef.orderBy("batch_name", Query.Direction.ASCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {   //documnet er vitore je multiple document query ache er jonno for loop
                        String data = "";
                        //Collections.reverse(listBook);
                        if(queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(S_DocsBatch.this,"No Data Found ",Toast.LENGTH_SHORT).show();
                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, "No Batch Found", Snackbar.LENGTH_LONG)
                                    .setAction("CLOSE", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    })
                                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                                    .show();
                        }else{
                            for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                S_DocsBatchModel s_docsBatchModel = documentSnapshot.toObject(S_DocsBatchModel.class);
                                //messageModel.setDocumentID(documentSnapshot.getId());
                                String dBatchUID = documentSnapshot.getId();
                                String dBatchName = s_docsBatchModel.getBatch_name();
                                String dCourseTeacherName = s_docsBatchModel.getTeacher();
                                String dTotalData = s_docsBatchModel.getTotal_data();
                                String dUploadedBy = s_docsBatchModel.getUploaded_by();
                                int diTotalLike = s_docsBatchModel.getLike();
                                //String getId, String batch_name, String uploaded_by, String teacher, String total_data, int like
                                listBatchListItem.add(new S_DocsBatchModel(dBatchUID,dBatchName,dUploadedBy,dCourseTeacherName,dTotalData,diTotalLike));
                                //Toast.makeText(getApplicationContext(), UsetMessageData+ " "+UserMessageTime, Toast.LENGTH_SHORT).show();
                            }
//                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
//                            linearLayoutManager.setStackFromEnd(true);
//                            mRecylerVieOldMessage.setLayoutManager(linearLayoutManager);
                            //Collections.reverse(listBook);
                            docsBatchAdapter = new S_DocsBatchAdapter(S_DocsBatch.this,listBatchListItem,S_DocsBatch.this);
                            docsBatchAdapter.notifyDataSetChanged();
                            mBatchRecyclerview.setLayoutManager(new GridLayoutManager(S_DocsBatch.this,1));
                            mBatchRecyclerview.setAdapter(docsBatchAdapter);
                        }

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
        String dBatchUID = listBatchListItem.get(position).getGetId();
        if(!dBatchUID.equals("NO")){
            Toast.makeText(this," "+dBatchUID,Toast.LENGTH_SHORT).show();;
            Intent intent = new Intent(S_DocsBatch.this, S_DocsBatchClass.class);
            intent.putExtra("Batch_UID",dBatchUID);
            startActivity(intent);
        }else{
            Toast.makeText(this," "+ listBatchListItem.get(position).getBatch_name()+ " UID 404",Toast.LENGTH_SHORT).show();;
        }
    }
    @Override
    public void onItemClickAuthorID(int position, String AuthorUID) {

    }
    @Override
    public void onItemLongCLick(int postion) {

    }
}