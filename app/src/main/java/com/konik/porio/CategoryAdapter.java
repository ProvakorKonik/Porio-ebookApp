package com.konik.porio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.sql.BatchUpdateException;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder>  {
    private Context mContext;
    private List<CategoryModel> mData;
    private RecylerviewClickInterface recylerviewClickInterface;
    public CategoryAdapter(Context mContext, List<CategoryModel> mData, RecylerviewClickInterface recylerviewClickInterface) {
        this.mContext = mContext;
        this.mData = mData;
        this.recylerviewClickInterface = recylerviewClickInterface;
    }

    @NonNull
    @Override
    public CategoryAdapter.CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.card_category_item_single,parent,false); //connecting to cardview
        return new CategoryHolder(view);
    }

    boolean dFollowPressed = false;
    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryHolder holder, int position) {
        String dPhotoURL = mData.get(position).getPhoto_url();
        Picasso.get().load(dPhotoURL).into(holder.mCategoryImage);
        holder.mCategoryName.setText(mData.get(position).getName());

        Random TotalFollowerRandom = new Random();
        int dTotalFollowerInt = TotalFollowerRandom.nextInt(8000)+1001;
        holder.mCategoryTotalFollowerText.setText(String.valueOf(dTotalFollowerInt) );
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CategoryHolder extends RecyclerView.ViewHolder{
        ImageView mCategoryImage;
        TextView mCategoryName;
        TextView mCategoryTotalFollowerText;
        Button mCategoryFollowerBtn;
        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            mCategoryImage = (ImageView)itemView.findViewById(R.id.category_item_image);
            mCategoryName = (TextView)itemView.findViewById(R.id.category_item_name);
            mCategoryTotalFollowerText = (TextView)itemView.findViewById(R.id.category_item_follower);
            mCategoryFollowerBtn = (Button)itemView.findViewById(R.id.category_item_follow_btn);

            mCategoryFollowerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(dFollowPressed == false){
                        dFollowPressed = true;
                        mCategoryFollowerBtn.setText("FOLLOWED");

                        String getFollower = mCategoryTotalFollowerText.getText().toString();
                        int dGetFollowerint = Integer.parseInt(getFollower); dGetFollowerint += 1;
                        mCategoryTotalFollowerText.setText(String.valueOf(dGetFollowerint));
                    }else{
                        dFollowPressed = false;
                        mCategoryFollowerBtn.setText("FOLLOW");
                        String getFollower = mCategoryTotalFollowerText.getText().toString();
                        int dGetFollowerint = Integer.parseInt(getFollower); dGetFollowerint -= 1;
                        mCategoryTotalFollowerText.setText(String.valueOf(dGetFollowerint));
                    }

                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recylerviewClickInterface .onItemClick(getAdapterPosition());
                }
            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    recylerviewClickInterface.onItemLongCLick(getAdapterPosition());
                    return true;
                }
            });
        }
    }


}
