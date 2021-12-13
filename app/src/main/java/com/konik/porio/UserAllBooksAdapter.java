package com.konik.porio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class UserAllBooksAdapter extends FirestoreRecyclerAdapter<UserAllBooksModel, UserAllBooksAdapter.AllBooksHolder> {
    public UserAllBooksAdapter(@NonNull FirestoreRecyclerOptions<UserAllBooksModel> options) {
        super(options);
    }
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String dCategoryUID = "USER";
    String dCategroyName = "NO";
    String dSectorType = "NO";
    @Override
    protected void onBindViewHolder(@NonNull final UserAllBooksAdapter.AllBooksHolder holder, int position, @NonNull UserAllBooksModel model) {
        holder.mMyBookName.setText(model.getName());
        dCategoryUID  = model.getCatUID();
        String dPublishDate = model.getCreate_date();
        String dLastUpdate = model.getLast_update();
        String dImageUrl = model.getPhotoURL();



        Long currentTime = Long.parseLong(dPublishDate);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
        Date date = new Date(currentTime);
        String dTime = simpleDateFormat.format(date);
        holder.mMyBookPublishDate.setText(dTime);
        Picasso.get().load(dImageUrl).into(holder.mMyBookImageView);

        if(model.getName().equals("The Mentor")){
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            //for Visible the itemview
            //holder.itemView.setVisibility(View.VISIBLE);
            //holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        db.collection("All_Type").document("CATEGORY").collection("USER").document(dCategoryUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                     dCategroyName = documentSnapshot.getString("name");
                     holder.mMyBookCategory.setText(dCategroyName);
                    dSectorType = "USER";
                }else{
                    db.collection("All_Type").document("CATEGORY").collection("ACADEMIC").document(dCategoryUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                dCategroyName = documentSnapshot.getString("name");
                                holder.mMyBookCategory.setText(dCategroyName);
                                dSectorType = "ACADEMIC";
                            }else{
                                db.collection("All_Type").document("CATEGORY").collection("PUBLISH").document(dCategoryUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot.exists()){
                                             dCategroyName = documentSnapshot.getString("name");
                                             holder.mMyBookCategory.setText(dCategroyName);
                                             dSectorType = "PUBLISH";
                                        }else{
                                            dSectorType = "NO";
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    @NonNull
    @Override
    public UserAllBooksAdapter.AllBooksHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_user_allbooks_per_item, parent, false);
        return new UserAllBooksAdapter.AllBooksHolder(view);
    }

    class AllBooksHolder extends RecyclerView.ViewHolder {
        CardView mAllBooksCard;
        ImageView mMyBookImageView;
        TextView mMyBookName, mMyBookSummary, mMyBookCategory, mMyBookPublishDate;

        Button mAddChapterBtn, mViewBookBtn;

        public AllBooksHolder(@NonNull View itemView) {
            super(itemView);
            mAllBooksCard = (CardView)itemView.findViewById(R.id.cardview_my_books) ;
            mMyBookImageView = (ImageView)itemView.findViewById(R.id.my_book_img);
            mMyBookName = (TextView)itemView.findViewById(R.id.my_book_title_id);
            mMyBookCategory = (TextView)itemView.findViewById(R.id.my_book_category);
            mMyBookPublishDate = (TextView)itemView.findViewById(R.id.my_book_publish_date);


            mAddChapterBtn = (Button)itemView.findViewById(R.id.my_book_add_chapter_btn);
            mViewBookBtn = (Button)itemView.findViewById(R.id.my_book_view_btn);

            //Declared Button one
            mAddChapterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int postion = getAdapterPosition();
                    if(postion != RecyclerView.NO_POSITION && listener2 != null){
                        listener2.onItemClick(getSnapshots().getSnapshot(postion), postion, "NO", dCategroyName, dSectorType);

                    }
                }
            });
            mViewBookBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int postion = getAdapterPosition();
                    if(postion != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(postion), postion, dCategoryUID, dCategroyName, dSectorType);

                    }
                }
            });

        }
    }

    //for clicking recycler view item
    private ClickListenerPackage listener;
    private ClickListenerPackage listener2;
    private ClickListenerPackage listener3;

    public interface ClickListenerPackage {
        void onItemClick(DocumentSnapshot documentSnapshot, int postion, String CategoryUID, String CategoryName, String SectorType);
    }

    public void viewBtnClickListener(UserAllBooksAdapter.ClickListenerPackage listener){
        this.listener = listener;
    }

    public void addBtnClickListener(UserAllBooksAdapter.ClickListenerPackage listener){
        this.listener2 = listener;
    }
}