package com.konik.porio;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.type.PostalAddressOrBuilder;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Fragment_Three extends Fragment {
    private String dUserUID = "NO";
    private ImageView mAddBooksIcon, mAddChapterIcon, mMyBooksIcon;
    private YouTubePlayerView mYoutubePlayer;
    private LinearLayout mDiscussionLayout ;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView recyclerViewDiscussion;

    /////////FIREBASE
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page

    //Discussion RecyclerView
    private CollectionReference discuss_topic_ref ;
    private DiscussionAdapter discuss_topic_adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_three, container, false);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // do your variables initialisations here except Views!!!



    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAddBooksIcon = (ImageView)view.findViewById(R.id.write_add_books);
        mAddChapterIcon = (ImageView)view.findViewById(R.id.write_add_chapter);
        mMyBooksIcon = (ImageView)view.findViewById(R.id.write_my_all_books);
        mYoutubePlayer = (YouTubePlayerView) view.findViewById(R.id.youtube_player_view_write) ;
        mDiscussionLayout = (LinearLayout) view.findViewById(R.id.linear_discus_layout);

        recyclerViewDiscussion = view.findViewById(R.id.recycler_discussion);
        recyclerViewDiscussion.setHasFixedSize(false);//recyclerViewDiscussion.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDiscussion.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));


        mAddBooksIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dUserUID.equals("NO")){
                    Toast.makeText(getContext(),"Please Login to Add Books",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(getContext(), BookAdd.class);
                    startActivity(intent);
                }

            }
        });
        mMyBooksIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dUserUID.equals("NO")){
                    Toast.makeText(getContext(),"Please login to view your books.",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(getContext(), UserAllBooks.class);
                    startActivity(intent);
                }

            }
        });

        mDiscussionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dUserUID.equals("NO")){
                    Toast.makeText(getContext(),"Please login to write.",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(getContext(), DiscussionAdd.class);
                    startActivity(intent);
                }
            }
        });

        mYoutubePlayer.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                //String videoId = "S0Q4gqBUs7c";
                youTubePlayer.loadVideo("qK4Re_ArS3Q", 0);
                youTubePlayer.pause();

            }
        });
        methodCheckUserLogin();
        setupDiscussTopicRecyclerView("ACategory");
    }
    private void setupDiscussTopicRecyclerView(String dCategory) {
        discuss_topic_ref = db.collection("All_Type").document("DISCUSS").collection(dCategory);
        Query query = discuss_topic_ref; //.orderBy("rating", Query.Direction.DESCENDING).limit(10);
        FirestoreRecyclerOptions<DiscussionModel> options_latest_book = new FirestoreRecyclerOptions.Builder<DiscussionModel>()
                .setQuery(query,DiscussionModel.class)
                .build();

        discuss_topic_adapter = new DiscussionAdapter(options_latest_book);
        recyclerViewDiscussion.setAdapter(discuss_topic_adapter);
        ////////////////////BUTTON MODE
        discuss_topic_adapter.OnClickDiscussionCard(new DiscussionAdapter.ClickListenerPackage(){
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int postion) {
                String dDiscussUID = documentSnapshot.getId();
                String dDiscussTopic= documentSnapshot.getString("topic");
                String dDiscussAuthor = documentSnapshot.getString("author");

                Intent intent = new Intent(getContext(), Discussion.class);
                intent.putExtra("iDiscuss_UID",dDiscussUID);
                intent.putExtra("iDiscuss_TOPIC",dDiscussTopic);
                intent.putExtra("iDiscuss_AUTHOR",dDiscussAuthor);

                startActivity(intent);
            }
        });
    }
    private void methodCheckUserLogin(){
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    dUserUID = FirebaseAuth.getInstance().getUid();

                }else{
                    dUserUID = "NO";
                }
            }
        };
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        discuss_topic_adapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
        discuss_topic_adapter.stopListening();
    }
}