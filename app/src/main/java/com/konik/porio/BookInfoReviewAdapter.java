package com.konik.porio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import static android.view.View.GONE;

public class BookInfoReviewAdapter extends FirestoreRecyclerAdapter<BookInfoReviewModel, BookInfoReviewAdapter.BookReviewHolder> {
    private boolean ddUserisAuthor = false;
    public BookInfoReviewAdapter(@NonNull FirestoreRecyclerOptions<BookInfoReviewModel> options, boolean dUserIsAuthor) {
        super(options);
        if(dUserIsAuthor == true){
            ddUserisAuthor = true;
        }else{
            ddUserisAuthor = false;
        }
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference user_data_ref =  db.collection("USER_DATA").document("REGISTER");


    @Override
    public BookReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_book_review_per_item, parent,false);
        return new BookReviewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull final BookReviewHolder holder, int position, @NonNull BookInfoReviewModel model) {
        holder.mBookReviewUserText.setText(model.getUser_review());
        String dUserUID = model.getUser_uid();
        String dAdminUID =  model.getAdmin_id();
        Double dRate = model.getUser_rating();
        Float ddRate = dRate.floatValue();

        String dPublishDate = model.getUser_time();
        Long currentTime = Long.parseLong(dPublishDate);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy");
        Date date = new Date(currentTime);
        String dTime = simpleDateFormat.format(date);


        holder.mBookReviewUserRating.setRating(ddRate);
        holder.mBookReviewUserTime.setText(dTime);
        holder.mBookReviewAdminText.setText(model.getAdmin_reply());

        //user_data_ref = db.collection("USER_DATA").document("REGISTER").collection("NORMAL_USER").document(dUserUID);
        user_data_ref.collection("NORMAL_USER").document(dUserUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String UserName =documentSnapshot.getString("name");
                    String UserPhotoUrl = documentSnapshot.getString("photoURL");
                    holder.mBookReviewUserName.setText(UserName);
                    if(UserPhotoUrl.equals("NO")){

                    }else if(UserPhotoUrl == null){

                    }else{
                        Picasso.get().load(UserPhotoUrl).into(holder.mBookReviewUserImg);
                    }


                }else{
                    holder.mBookReviewUserName.setText("ERROR FOUND");
                }
            }
        });

        if(dAdminUID.equals("NO")){
            holder.mBookReviewAdminName.setVisibility(GONE);
            holder.mBookReviewAdminImg.setVisibility(GONE);
            holder.mBookReviewAdminType.setVisibility(GONE);
            holder.mBookReviewAdminText.setVisibility(GONE);
            if(ddUserisAuthor == true){
                holder.mBookReviewAdminReplyBtn.setVisibility(View.VISIBLE);
                holder.mBookReviewAdminReplyEditText.setVisibility(View.VISIBLE);
            }
        }else{
            holder.mBookReviewAdminName.setVisibility(View.VISIBLE);
            holder.mBookReviewAdminImg.setVisibility(View.VISIBLE);
            holder.mBookReviewAdminType.setVisibility(View.VISIBLE);
            holder.mBookReviewAdminText.setVisibility(View.VISIBLE);


            db.collection("USER_DATA").document("REGISTER").collection("NORMAL_USER").document(dAdminUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        String UserName =   documentSnapshot.getString("name");
                        String UserPhotoUrl = documentSnapshot.getString("photoURL");

                        holder.mBookReviewAdminName.setText(UserName);
                        if(UserPhotoUrl.equals("NO")){

                        }else if(UserPhotoUrl == null){

                        }else{
                            Picasso.get().load(UserPhotoUrl).into(holder.mBookReviewAdminImg);
                        }

                    }else{
                        holder.mBookReviewAdminName.setText("ERROR FOUND");
                    }
                }
            });
        }
    }

    class BookReviewHolder extends RecyclerView.ViewHolder{
        ImageView mBookReviewUserImg, mBookReviewAdminImg;
        TextView mBookReviewUserName, mBookReviewUserText, mBookReviewUserTime;
        TextView mBookReviewAdminName, mBookReviewAdminText, mBookReviewAdminType;
        EditText mBookReviewAdminReplyEditText; Button mBookReviewAdminReplyBtn;
        RatingBar mBookReviewUserRating;
        public BookReviewHolder(@NonNull View itemView){
            super(itemView);
            mBookReviewUserImg = (ImageView)itemView.findViewById(R.id.book_review_user_img);
            mBookReviewAdminImg = (ImageView)itemView.findViewById(R.id.book_review_admin_img);

            mBookReviewUserName = (TextView)itemView.findViewById(R.id.book_review_user_name);
            mBookReviewAdminName = (TextView)itemView.findViewById(R.id.book_review_admin_name);
            mBookReviewUserText = (TextView)itemView.findViewById(R.id.book_review_text);
            mBookReviewAdminText = (TextView)itemView.findViewById(R.id.book_review_admin_reply);
            mBookReviewUserTime = (TextView)itemView.findViewById(R.id.book_review_time);
            mBookReviewAdminType = (TextView)itemView.findViewById(R.id.book_review_user_typp);
            mBookReviewUserRating = (RatingBar)itemView.findViewById(R.id.book_review_rating);
            mBookReviewAdminReplyEditText = (EditText) itemView.findViewById(R.id.book_review_admin_reply_edit);
            mBookReviewAdminReplyBtn = (Button)itemView.findViewById(R.id.book_review_admin_reply_btn);

            mBookReviewAdminReplyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String dReplyEditText = mBookReviewAdminReplyEditText.getText().toString();
                    int postion = getAdapterPosition();
                    if(postion != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(postion), postion,dReplyEditText);
                        mBookReviewAdminReplyBtn.setVisibility(GONE);
                        mBookReviewAdminReplyEditText.setVisibility(GONE);
                    }
                }
            });

        }
    }
    private ClickListenerPackage listener;
    private ClickListenerPackage listener2;

    public interface ClickListenerPackage {
        void onItemClick(DocumentSnapshot documentSnapshot, int postion, String dReplyEditText);
    }
    public void replyBtnClickListener(BookInfoReviewAdapter.ClickListenerPackage listener){
        this.listener = listener;
    }
}
