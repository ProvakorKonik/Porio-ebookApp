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

public class S_DocsBatchClass extends AppCompatActivity  implements RecylerviewClickInterface{

    private Button mClassAddBtn;
    private String dBatchUID = "NO";
    private RecyclerView mRecyclerViewClass;
    //FIREBASE
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference classListRef ;    //for quires of data
    List<S_DocsBatchClassModel> listClassListItem;
    S_DocsBatchClassApapter docsBatchAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s__docs_batch_class);
        mClassAddBtn = (Button)findViewById(R.id.sdbc_add_btn);
        mRecyclerViewClass = (RecyclerView)findViewById(R.id.recyclerview_class);
        listClassListItem = new ArrayList<>();

        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {   dBatchUID = intent.getExtras().getString("Batch_UID");
            if (TextUtils.isEmpty(dBatchUID)) {
                dBatchUID= "NO";
                Toast.makeText(S_DocsBatchClass.this, "Batch UID NULL  " , Toast.LENGTH_SHORT).show();
            }else if (dBatchUID.equals("")){
                dBatchUID= "NO";
                Toast.makeText(S_DocsBatchClass.this, "Batch UID 404" , Toast.LENGTH_SHORT).show();
            }else{
                /////////////////////////BATCH UID FOUND
            }
        }else{
            dBatchUID= "NO";
            Toast.makeText(S_DocsBatchClass.this, "Intent Not Found" , Toast.LENGTH_SHORT).show();
        }

        mClassAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!dBatchUID.equals("") && !dBatchUID.equals("NO") && dBatchUID != null){
                    Intent intent = new Intent(S_DocsBatchClass.this,S_DocsBatchClassAdd.class);
                    intent.putExtra("Batch_UID",dBatchUID);
                    startActivity(intent);
                }else{
                    Toast.makeText(S_DocsBatchClass.this, "BOOK UID 404" , Toast.LENGTH_SHORT).show();
                }
            }
        });

        LoadClassist(dBatchUID);
    }
    private void LoadClassist(String BatchUID){     //CORRECT
        classListRef = db.collection("All_Type").document("CLASS").collection(BatchUID);//.orderBy("time", Query.Direction.ASCENDING).limitToLast(3
        classListRef.orderBy("no", Query.Direction.ASCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {   //documnet er vitore je multiple document query ache er jonno for loop
                        String data = "";
                        //Collections.reverse(listBook);
                        if(queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(S_DocsBatchClass.this,"No Data Found ",Toast.LENGTH_SHORT).show();
                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, "No Class Found.", Snackbar.LENGTH_LONG)
                                    .setAction("CLOSE", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    })
                                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                                    .show();
                        }else{
                            for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){

                                S_DocsBatchClassModel s_docsBatchClassModel = documentSnapshot.toObject(S_DocsBatchClassModel.class);
                                //messageModel.setDocumentID(documentSnapshot.getId());
                                String dClassUID = documentSnapshot.getId();
                                String dClassTopicName = s_docsBatchClassModel.getTopic();
                                String dClassNo = s_docsBatchClassModel.getNo();
                                String dClassDate = s_docsBatchClassModel.getDate();
                                String dClassNote = s_docsBatchClassModel.getNote();
                                String dClassCreatedBy = s_docsBatchClassModel.getBy();

                                //String getId, String topic, String no, String date, String note, String by
                                listClassListItem.add(new S_DocsBatchClassModel(dClassUID,dClassTopicName,dClassNo,dClassDate,dClassNote,dClassCreatedBy));
                            }
                            docsBatchAdapter = new S_DocsBatchClassApapter(S_DocsBatchClass.this,listClassListItem,S_DocsBatchClass.this);
                            docsBatchAdapter.notifyDataSetChanged();
                            mRecyclerViewClass.setLayoutManager(new GridLayoutManager(S_DocsBatchClass.this,1));
                            mRecyclerViewClass.setAdapter(docsBatchAdapter);
                        }


                        }


                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    String dClassUID = "NO";
    @Override
    public void onItemClick(int position) {
        dClassUID = listClassListItem.get(position).getGetId();

        if(!dClassUID.equals("NO") && !dBatchUID.equals("") && !dBatchUID.equals("NO")){
            Toast.makeText(this," "+dClassUID,Toast.LENGTH_SHORT).show();;
            Intent intent = new Intent(S_DocsBatchClass.this, S_DBCFiles.class);
            intent.putExtra("Class_UID",dClassUID);
            intent.putExtra("Batch_UID",dBatchUID);
            startActivity(intent);
        }else{
            Toast.makeText(this,"Class or Batch UID 404",Toast.LENGTH_SHORT).show();;
        }
    }
    @Override
    public void onItemClickAuthorID(int position, String AuthorUID) {

    }
    @Override
    public void onItemLongCLick(int postion) {

    }
}