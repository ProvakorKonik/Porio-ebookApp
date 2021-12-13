package com.konik.porio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class UserList extends AppCompatActivity {

    private RecyclerView mRecyclerViewUserList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference user_list_ref ;
    private UserListAdapter userListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        mRecyclerViewUserList = (RecyclerView) findViewById(R.id.recyclerviewUserList);
        mRecyclerViewUserList.setHasFixedSize(false);//mRecyclerViewUserList.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewUserList.setLayoutManager(new GridLayoutManager(UserList.this,1));
        setupAllUserListRecyclerView();
    }

    private void setupAllUserListRecyclerView() {

        user_list_ref =  db.collection("USER_DATA").document("REGISTER").collection("NORMAL_USER");
        Query query = user_list_ref; //.orderBy("rating", Query.Direction.DESCENDING).limit(10);
        FirestoreRecyclerOptions<UserListModel> option_all_user = new FirestoreRecyclerOptions.Builder<UserListModel>()
                .setQuery(query,UserListModel.class)
                .build();

        userListAdapter = new UserListAdapter(option_all_user);
        userListAdapter.notifyDataSetChanged();
        mRecyclerViewUserList.setAdapter(userListAdapter);

        //for recycler view click item
        userListAdapter.addBtnClickListener(new UserListAdapter.ClickListenerPackage(){
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int postion) {
                /*//Category_Model category_model = documentSnapshot.toObject(Category_Model.class);
                dBookUID = documentSnapshot.getString("bookUID");
                String get_id = documentSnapshot.getId();
                Toast.makeText(getApplicationContext(),"GET ID  "+get_id,Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MyBooks.this, SingleBookAddStories.class);
                intent.putExtra("BOOK_UID",dBookUID);
                startActivity(intent);*/
            }
        });

        userListAdapter.viewBtnClickListener(new UserListAdapter.ClickListenerPackage() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int postion) {
                /*//Category_Model category_model = documentSnapshot.toObject(Category_Model.class);
                dBookUID = documentSnapshot.getString("bookUID");

                Intent intent = new Intent(MyBooks.this, SingleBookInfo.class);
                intent.putExtra("iBookUID",dBookUID);
                startActivity(intent);*/
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        userListAdapter.startListening();
    }
}