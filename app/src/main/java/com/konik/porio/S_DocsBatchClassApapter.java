package com.konik.porio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class S_DocsBatchClassApapter extends RecyclerView.Adapter<S_DocsBatchClassApapter.BatchClassHolder>  {
    private Context mContext;
    private List<S_DocsBatchClassModel> mData;
    private RecylerviewClickInterface recylerviewClickInterface;

    public S_DocsBatchClassApapter(Context mContext, List<S_DocsBatchClassModel> mData, RecylerviewClickInterface recylerviewClickInterface) {
        this.mContext = mContext;
        this.mData = mData;
        this.recylerviewClickInterface = recylerviewClickInterface;
    }


    @NonNull
    @Override
    public S_DocsBatchClassApapter.BatchClassHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.s_class_single_item,parent,false); //connecting to cardview
        return new BatchClassHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull S_DocsBatchClassApapter.BatchClassHolder holder, int position) {
        holder.mClassTopicName.setText(mData.get(position).getTopic());
        holder.mClassNo.setText("Serial: "+mData.get(position).getNo());

        String dUploadDate= mData.get(position).getDate();
        dUploadDate = dUploadDate.substring(2,15);
        holder.mClassNote.setText(mData.get(position).getNote());

        if(isNumericString(dUploadDate)){
            Long currentTime = Long.parseLong(dUploadDate);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d/MMMM/yyyy");
            //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM yyyy");
            Date date = new Date(currentTime);
            String time = simpleDateFormat.format(date);
            holder.mClassDate.setText(""+time);
        }else{
            holder.mClassDate.setText("error date");
        }

        //////youtube
        /*holder.mYoutubePlayerCard.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = "S0Q4gqBUs7c";
                youTubePlayer.loadVideo(videoId, 0);
                youTubePlayer.pause();

            }
        });*/
    }
    private boolean isNumericString(String dsBookPublishTime) {
        if (dsBookPublishTime.matches("[0-9]+") && dsBookPublishTime.length() > 0) {
            return true;
        }else{
            return false;   //integer not found
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    public class BatchClassHolder extends  RecyclerView.ViewHolder{
        TextView mClassTopicName, mClassNo, mClassDate, mClassNote, mClassUploadDate;
        //private YouTubePlayerView mYoutubePlayerCard;
        public BatchClassHolder(@NonNull View itemView){
            super(itemView);
            mClassTopicName = (TextView)itemView.findViewById(R.id.edit_dbcs_topic_name);
            mClassNo = (TextView)itemView.findViewById(R.id.edit_dbcs_serail_no);
            mClassDate = (TextView)itemView.findViewById(R.id.edit_dbcs_class_date);
            mClassNote = (TextView)itemView.findViewById(R.id.edit_dbcs_class_note);
            mClassUploadDate = (TextView)itemView.findViewById(R.id.edit_dbcs_class_created);

            //mYoutubePlayerCard =(YouTubePlayerView)itemView.findViewById(R.id.test_card_youtube_player);
            //getLifecycle().addObserver(mYoutubePlayerCard);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recylerviewClickInterface .onItemClick(getAdapterPosition());
                }
            });

        }
    }
}
