package com.konik.porio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReadChapTwoAdapter extends RecyclerView.Adapter<ReadChapTwoAdapter.ReadChapTwoHolder>  {
    private Context mContext;
    private List<ReadChapModel> mData;
    private RecylerviewClickInterface recylerviewClickInterface;
    public ReadChapTwoAdapter (Context mContext, List<ReadChapModel> mData, RecylerviewClickInterface recylerviewClickInterface) {
        this.mContext = mContext;
        this.mData = mData;
        this.recylerviewClickInterface = recylerviewClickInterface;
    }

    @NonNull
    @Override
    public ReadChapTwoAdapter.ReadChapTwoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.chapter_item_single,parent,false); //connecting to cardview
        return new ReadChapTwoAdapter.ReadChapTwoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReadChapTwoAdapter.ReadChapTwoHolder holder, int position) {
        holder.mReadStoryBtn.setText(mData.get(position).getChapterNmae());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    public class ReadChapTwoHolder extends RecyclerView.ViewHolder{
        Button mReadStoryBtn;
        public ReadChapTwoHolder(@NonNull View itemView){
            super(itemView);
            mReadStoryBtn = (Button)itemView.findViewById(R.id.chapter_item_btn);
            mReadStoryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recylerviewClickInterface .onItemClick(getAdapterPosition());
                }
            });
        }
    }

}
