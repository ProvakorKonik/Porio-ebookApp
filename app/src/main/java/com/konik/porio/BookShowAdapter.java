package com.konik.porio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BookShowAdapter  extends FirestoreRecyclerAdapter<BookInfoModel, BookShowAdapter.DLatestBookHolder> {
    public BookShowAdapter(@NonNull FirestoreRecyclerOptions<BookInfoModel> options) {
        super(options);
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference user_data_ref =  db.collection("USER_DATA").document("REGISTER");
    String dAuthorName = "NO";
    String dBookAuthorUID = "NO";
    @Override
    protected void onBindViewHolder(@NonNull final DLatestBookHolder holder, int position, @NonNull BookInfoModel model) {
        holder.mLatestBookName.setText(model.getName());
        holder.mLatestBookCategory.setText("Category");
        holder.mLatestBookRating.setRating((float)(5.0));
        //holder.mLatestBookRating.setRating((float)(model.getRating()/2.0));

        //String dPublishDate = model.getPublishDate();
        String dImageUrl = model.getPhotoURL();
        dBookAuthorUID = model.getAuthor();
        /*Long currentTime = Long.parseLong(dPublishDate);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
        Date date = new Date(currentTime);
        String time = simpleDateFormat.format(date);*/

        holder.mLatestBookPublishDate.setText("12 May 2020");
        //holder.mLatestBookRating.setRating(dRatingStar);
        Picasso.get().load(dImageUrl).into(holder.mLatestBookImageView);

        String dAuthorUID= model.getAuthor();
        if(dAuthorUID.length() >= 20){
            db.collection("USER_DATA").document("REGISTER").collection("NORMAL_USER").document(dAuthorUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        dAuthorName =documentSnapshot.getString("name");
                        holder.mLatestBookAuthorName.setText(dAuthorName);
                    }else{
                        dAuthorName = "NO";
                        holder.mLatestBookAuthorName.setText(dAuthorName);
                    }
                }
            });
        }else{
            holder.mLatestBookAuthorName.setText(dAuthorUID);
        }

    }



    @NonNull
    @Override
    public DLatestBookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_bookshow_item, parent,false);
        return new DLatestBookHolder(view);
    }

    class DLatestBookHolder extends RecyclerView.ViewHolder{
        ImageView mLatestBookImageView;
        TextView mLatestBookName, mLatestBookAuthorName, mLatestBookPublishDate, mLatestBookCategory;
        RatingBar mLatestBookRating;

        public DLatestBookHolder(@NonNull View itemView){
            super(itemView);
            mLatestBookImageView = (ImageView)itemView.findViewById(R.id.latest_book_img_id);
            mLatestBookName = (TextView)itemView.findViewById(R.id.latest_book_title_id);
            mLatestBookAuthorName = (TextView)itemView.findViewById(R.id.latest_book_author_name);
            mLatestBookCategory = (TextView)itemView.findViewById(R.id.latest_book_category);
            mLatestBookPublishDate = (TextView)itemView.findViewById(R.id.latest_book_publish_date);
            mLatestBookRating = (RatingBar)itemView.findViewById(R.id.latest_book_rating);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int postion = getAdapterPosition();
                    if(postion != RecyclerView.NO_POSITION && listener1 != null){
                        listener1.onItemClick(getSnapshots().getSnapshot(postion), postion, dBookAuthorUID);

                    }
                }
            });
        }
    }
    private  ClickListenerPackage listener1;
    public interface ClickListenerPackage {
        void onItemClick(DocumentSnapshot documentSnapshot, int postion, String AuthorUID);
    }

    public void latestCardViewonClick(BookShowAdapter.ClickListenerPackage listener1){
        this.listener1 = listener1;
    }

}
