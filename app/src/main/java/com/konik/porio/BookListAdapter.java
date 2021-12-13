package com.konik.porio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.resources.TextAppearanceFontCallback;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.BookListHolder>  {
    private Context mContext;
    private List<BookInfoModel> mData;
    private RecylerviewClickInterface recylerviewClickInterface;
    public BookListAdapter (Context mContext, List<BookInfoModel> mData, RecylerviewClickInterface recylerviewClickInterface) {
        this.mContext = mContext;
        this.mData = mData;
        this.recylerviewClickInterface = recylerviewClickInterface;
    }

    @NonNull
    @Override
    public BookListAdapter.BookListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.card_book_list_item,parent,false); //connecting to cardview
        return new BookListHolder(view);
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference user_data_ref =  db.collection("USER_DATA").document("REGISTER");
    String dAuthorName = "NO";
    String dAuthorUID = "NO";
    @Override
    public void onBindViewHolder(@NonNull final BookListAdapter.BookListHolder holder, int position) {
            String dPhotoURL = mData.get(position).getPhotoURL();
            String dTotalData = mData.get(position).getTotal_data();
            Vector<String> vec_total = new Vector<String>();
            vec_total = methodGetBookTotalData(dTotalData);
            int vec_len = vec_total.size();
            if(vec_len == 10){
                String dsBookRating = vec_total.elementAt(2); //Book Total Rating
                String dsBookPublishTime = vec_total.elementAt(9);
                if(dsBookRating.equals("0")){
                    Random RatingRandom = new Random();
                    int dRatingRandomInt = (RatingRandom.nextInt(7)+3);
                    holder.mBookRatingText.setText("Rating : "+dRatingRandomInt+"/10");
                }else{
                    holder.mBookRatingText.setText("Rating : "+dsBookRating+"/10");
                }


                if(isNumericString(dsBookPublishTime)){
                    Long currentTime = Long.parseLong(dsBookPublishTime);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM yyyy");
                    //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM yyyy");
                    Date date = new Date(currentTime);
                    String time = simpleDateFormat.format(date);
                    holder.mBookPublishDate.setText("Publish on : "+time);
                }else
                    holder.mBookPublishDate.setText(" ");
            }else{
                holder.mBookRatingText.setText("Rating : "+"7/10");
                holder.mBookPublishDate.setText("Publish :"+"12 May 2020");
            }
        Picasso.get().load(dPhotoURL).into(holder.mBookImage);
        holder.mBookNameText.setText(mData.get(position).getName());
        dAuthorUID= mData.get(position).getAuthor();
        if(dAuthorUID.length() >= 20){
            db.collection("USER_DATA").document("REGISTER").collection("NORMAL_USER").document(dAuthorUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        dAuthorName =documentSnapshot.getString("name");
                        holder.mBookCategoryText.setText(dAuthorName);
                    }else{
                        dAuthorName = "NO";
                        holder.mBookCategoryText.setText(dAuthorName);
                    }
                }
            });
        }else{
            holder.mBookCategoryText.setText(dAuthorName);
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    public class BookListHolder extends RecyclerView.ViewHolder{
        ImageView mBookImage;
        TextView mBookNameText, mBookCategoryText, mBookRatingText, mBookPublishDate;
        public BookListHolder(@NonNull View itemView){
            super(itemView);
            mBookImage = (ImageView)itemView.findViewById(R.id.book_list_img_id);
            mBookNameText = (TextView)itemView.findViewById(R.id.book_list_title_id);
            mBookCategoryText = (TextView)itemView.findViewById(R.id.book_list_category);
            mBookRatingText = (TextView)itemView.findViewById(R.id.book_list_rating);
            mBookPublishDate = (TextView)itemView.findViewById(R.id.book_list_publish_date);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recylerviewClickInterface .onItemClickAuthorID(getAdapterPosition(), dAuthorUID);
                }
            });
        }
    }
    private Vector methodGetBookTotalData(String dTotal) {
        int len = dTotal.length();
        Vector<String> vec = new Vector<String>();

        String target = "TERPLPDVST";
        String new_word = "";
        int i = 0;
        int j = 0;
        for (i = 0; i < len; i++) {
            if (dTotal.charAt(i) == target.charAt(j)) {
                vec.add(new_word);
                new_word = "";
                j++;
            } else {
                new_word += dTotal.charAt(i);
            }
        }
        if(vec.size() == 10) {
            String dsBookType = vec.elementAt(0); //ACTIVE OR BLOCKED
            String dsBookEditNo = vec.elementAt(1); //How Much Time Book ediited
            String dsBookRating = vec.elementAt(2); //Book Total Rating
            String dsBookPriority = vec.elementAt(3); //Book Priority
            String dsBookLevel = vec.elementAt(4); //Book Level
            String dsBookPrice = vec.elementAt(5); //BOokPrice;
            String dsBookDiscount = vec.elementAt(6); //Book Discount;
            String dsBookViews = vec.elementAt(7); //Book Total Viewed;
            String dsBookInStock = vec.elementAt(8); //Book in Stock;
            String dsBookPublishTime = vec.elementAt(9); //Book Publish Time;



        }else{
            //ERORR Toast.makeText(BookInfo.this, "vector size crossed = "+vec.size(), Toast.LENGTH_SHORT).show();
        }

        return vec;
    }
    private boolean isNumericString(String dsBookPublishTime) {
        if (dsBookPublishTime.matches("[0-9]+") && dsBookPublishTime.length() > 0) {
            return true;
        }else{
            return false;   //integer not found
        }
    }
}
