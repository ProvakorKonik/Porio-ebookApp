package com.konik.porio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DiscussionReplyAdapter extends FirestoreRecyclerAdapter<DiscussionReplyModel, DiscussionReplyAdapter.DiscussReplyHolder> {
    public DiscussionReplyAdapter(@NonNull FirestoreRecyclerOptions<DiscussionReplyModel> options) {
        super(options);
    }
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference user_data_ref = db.collection("USER_DATA").document("REGISTER");
    @Override
    protected void onBindViewHolder(@NonNull final DiscussionReplyAdapter.DiscussReplyHolder holder, int position, @NonNull DiscussionReplyModel model) {
        holder.mReplyText.setText(model.getR_topic());
        String dTime = model.getR_time();
        holder.mReplyDate.setText(TimeAgo(dTime));
        //holder.mReplyTotalLike.setText());
        String dUserUID = model.getR_author();

        if(dUserUID.length() >= 20){
            db.collection("USER_DATA").document("REGISTER").collection("NORMAL_USER").document(dUserUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        String dAuthorName =documentSnapshot.getString("name");
                        String UserPhotoUrl =documentSnapshot.getString("photoURL");
                        holder.mReplyUserName.setText(dAuthorName);
                        if(UserPhotoUrl.equals("NO")){

                        }else if(UserPhotoUrl.equals(null)){

                        }else{
                            Picasso.get().load(UserPhotoUrl).into(holder.mReplyUserImg);
                        }
                    }else{
                        String dAuthorName = "NO";
                        holder.mReplyUserName.setText(dAuthorName);
                    }
                }
            });
        }else{
            holder.mReplyUserName.setText(dUserUID);
        }
    }
    private String TimeAgo(String dtime){
        try
        {
            Long currentTime = Long.parseLong(dtime);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
            Date date = new Date(currentTime);
            String time = simpleDateFormat.format(date);

            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
            Date past = format.parse(time);
            Date now = new Date();
            long seconds= TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes=TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours=TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days=TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

            if(seconds<60)
                dtime = seconds+" seconds ago";
            else if(minutes<60)
                dtime = minutes+" minutes ago";
            else if(hours<24)
                dtime = hours+" hours ago";
            else
                dtime = days+" days ago";


        }
        catch (Exception j){
            j.printStackTrace();
        }

        return dtime;
    }

    @NonNull
    @Override
    public DiscussionReplyAdapter.DiscussReplyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_per_reply_item, parent,false);
        return new DiscussReplyHolder(view);
    }

    class DiscussReplyHolder extends RecyclerView.ViewHolder {
        ImageView mReplyUserImg;
        ImageView mReplyLikeImg;
        TextView mReplyUserName, mReplyText, mReplyDate, mReplyLikeBtnText ,mReplyTotalLike, mReplyReplyBtnText,  mReplyViewMoreReplies;

        public DiscussReplyHolder(@NonNull View itemView) {
            super(itemView);
            mReplyUserImg = (ImageView)itemView.findViewById(R.id.reply_user_image);
            mReplyLikeImg = (ImageView)itemView.findViewById(R.id.reply_like_img);

            mReplyUserName = (TextView)itemView.findViewById(R.id.reply_user_name);
            mReplyText = (TextView)itemView.findViewById(R.id.reply_text);
            mReplyDate = (TextView)itemView.findViewById(R.id.reply_date);
            mReplyLikeBtnText = (TextView)itemView.findViewById(R.id.reply_like_txt_btn);
            mReplyTotalLike = (TextView)itemView.findViewById(R.id.reply_total_like);
            mReplyReplyBtnText = (TextView)itemView.findViewById(R.id.reply_reply_text_btn);
            mReplyViewMoreReplies = (TextView)itemView.findViewById(R.id.reply_view_more_reply_btn);

            mReplyLikeBtnText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int postion = getAdapterPosition();
                    if(postion != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(postion), postion);

                    }
                }
            });
        }
    }

    private ClickListenerPackage listener;
    public interface ClickListenerPackage {
        void onItemClick(DocumentSnapshot documentSnapshot, int postion);
    }
}
