package com.konik.porio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class S_DBCFilesAdapter extends RecyclerView.Adapter<S_DBCFilesAdapter.FileHolder> {
    private Context mContext;
    private List<S_DBCFilesModel> mData;
    private RecylerviewClickInterface recylerviewClickInterface;

    public S_DBCFilesAdapter(Context mContext, List<S_DBCFilesModel> mData, RecylerviewClickInterface recylerviewClickInterface) {
        this.mContext = mContext;
        this.mData = mData;
        this.recylerviewClickInterface = recylerviewClickInterface;
    }

    @NonNull
    @Override
    public S_DBCFilesAdapter.FileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.s_file_single_item,parent,false); //connecting to cardview
        return new FileHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull S_DBCFilesAdapter.FileHolder holder, int position) {
            holder.mFileName.setText(mData.get(position).getName());
            //String Time = mData.get(position).getDate();
            //holder.mFileName.setText(dDate);
            final String dYoutubeVideoID = mData.get(position).getLink();
            String dFileType = mData.get(position).getType();
            if(dFileType.equals("ytbX.link")){
                //////youtube
                holder.mFileIconImage.setImageResource(R.drawable.ic_outline_play_circle_filled_24);
                holder.mYoutubePlayerCard.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        //String videoId = "S0Q4gqBUs7c";
                        youTubePlayer.loadVideo(dYoutubeVideoID, 0);
                        youTubePlayer.pause();

                    }
                });
            }else{
                holder.mYoutubePlayerCard.setVisibility(View.GONE);
            }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class FileHolder extends  RecyclerView.ViewHolder {
        TextView mFileName, mFileDate ;
        ImageView mFileIconImage;
        private YouTubePlayerView mYoutubePlayerCard;
        public FileHolder(@NonNull View itemView) {
            super(itemView);
            mFileName = (TextView)itemView.findViewById(R.id.dbcsf_file_name);
            mFileDate = (TextView)itemView.findViewById(R.id.dbcsf_file_time);
            mYoutubePlayerCard = (YouTubePlayerView)itemView.findViewById(R.id.dbcsf_youtube_view);
            mFileIconImage = (ImageView) itemView.findViewById(R.id.dbcsf_file_icon);

            itemView.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    recylerviewClickInterface.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
