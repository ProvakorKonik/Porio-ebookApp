package com.konik.porio;

import android.content.Context;
import android.graphics.Typeface;
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
import java.util.Random;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class DiscussionCommentAdapter extends FirestoreRecyclerAdapter<DiscussionCommentModel, DiscussionCommentAdapter.DiscussCommentHolder> {
    public DiscussionCommentAdapter(@NonNull FirestoreRecyclerOptions<DiscussionCommentModel> options) {
        super(options);
    }
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference user_data_ref = db.collection("USER_DATA").document("REGISTER");

    String dUserUID = "NO";
    String dAuthorName = "NO";
    String UserPhotoUrl = "NO";

    @Override
    protected void onBindViewHolder(@NonNull final DiscussCommentHolder holder, int position, @NonNull DiscussionCommentModel model) {
        holder.mCommentText.setText(model.getC_topic());
        String dTime = model.getC_time();
        holder.mCommentDate.setText(TimeAgo(dTime));
        //holder.mCommentTotalLike.setText(model.getLike()+" ");
        //int dTotalComment = model.getComment();
        /*if(dTotalComment>1){
            holder.mCommentViewMoreReplies.setText("View "+String.valueOf(dTotalComment)+" more replies");
        }else if(dTotalComment == 1){
            holder.mCommentViewMoreReplies.setText("View one reply");
        }else{
            holder.mCommentViewMoreReplies.setVisibility(View.GONE);
        }*/
        dUserUID = model.getC_author();
        if(dUserUID.length() >= 20){
            db.collection("USER_DATA").document("REGISTER").collection("NORMAL_USER").document(dUserUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                         dAuthorName =documentSnapshot.getString("name");
                         UserPhotoUrl =documentSnapshot.getString("photoURL");
                        holder.mCommentUserName.setText(dAuthorName);
                        if(UserPhotoUrl.equals("NO")){

                        }else if(UserPhotoUrl.equals(null)){

                        }else{
                            Picasso.get().load(UserPhotoUrl).into(holder.mCommentUserImg);
                        }
                    }else{
                        String dAuthorName = "NO";
                        holder.mCommentUserName.setText(dAuthorName);
                    }
                }
            });
        }else{
            holder.mCommentUserName.setText(dUserUID);
        }

        Random random = new Random(); //Random number from 0 to 900 but its start from 100 to 999 because of adding 100
        int dLikeCount = random.nextInt(5) + 40;
        int dCommentCount = random.nextInt(5);
        holder.mCommentTotalLike.setText(String.valueOf(dLikeCount));
        if(dCommentCount == 0){
            holder.mCommentViewMoreReplies.setVisibility(View.GONE);
        }else if(dCommentCount == 1){
            holder.mCommentViewMoreReplies.setText("View "+String.valueOf(dCommentCount)+" more reply");
        }else {
            holder.mCommentViewMoreReplies.setText("View "+String.valueOf(dCommentCount)+" more replies");
        }

    }

    @NonNull
    @Override
    public DiscussCommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_per_comment, parent,false);
        return new DiscussCommentHolder(view);
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
    private boolean dLikedPress = false;
    class DiscussCommentHolder extends RecyclerView.ViewHolder {
        ImageView mCommentUserImg;
        ImageView mCommentLikeImg;
        TextView mCommentUserName, mCommentText, mCommentDate, mCommentLikeBtnText ,mCommentTotalLike, mCommentReplyBtnText,  mCommentViewMoreReplies;

        public DiscussCommentHolder(@NonNull View itemView) {
            super(itemView);
            mCommentUserImg = (ImageView)itemView.findViewById(R.id.comment_user_image);
            mCommentLikeImg = (ImageView)itemView.findViewById(R.id.comment_like_img);

            mCommentUserName = (TextView)itemView.findViewById(R.id.comment_user_name);
            mCommentText = (TextView)itemView.findViewById(R.id.comment_text);
            mCommentDate = (TextView)itemView.findViewById(R.id.comment_date);
            mCommentLikeBtnText = (TextView)itemView.findViewById(R.id.comment_like_txt_btn);
            mCommentTotalLike = (TextView)itemView.findViewById(R.id.comment_total_like);
            mCommentReplyBtnText = (TextView)itemView.findViewById(R.id.comment_reply_text_btn);
            mCommentViewMoreReplies = (TextView)itemView.findViewById(R.id.comment_view_more_reply_btn);

            mCommentLikeImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(dLikedPress == false){
                        dLikedPress = true;
                        mCommentLikeImg.setColorFilter(mCommentLikeImg.getContext().getResources().getColor(R.color.colorPrimary));
                        mCommentLikeBtnText.setTypeface(mCommentLikeBtnText.getTypeface(), Typeface.BOLD);
                        mCommentLikeBtnText.setText("LIKED");
                        String dTotalLike = mCommentTotalLike.getText().toString();
                        int diTotalLike= Integer.parseInt(dTotalLike) ; diTotalLike = diTotalLike + 1;
                        mCommentTotalLike.setText(String.valueOf(diTotalLike));
                    }else{
                        dLikedPress = false;
                        //mCommentLikeImg.getContext().getResources().getColor(R.color.colorPrimaryDarkDEEP);
                        mCommentLikeImg.setColorFilter(mCommentLikeImg.getContext().getResources().getColor(R.color.colorDeepGrayBlue));
                        mCommentLikeBtnText.setTypeface(mCommentLikeBtnText.getTypeface(), Typeface.BOLD);
                        mCommentLikeBtnText.setText("LIKE");
                        String dTotalLike = mCommentTotalLike.getText().toString();
                        int diTotalLike= Integer.parseInt(dTotalLike); diTotalLike = diTotalLike - 1;
                        mCommentTotalLike.setText(String.valueOf(diTotalLike));
                    }

                }
            });
            mCommentLikeBtnText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(dLikedPress == false){
                        dLikedPress = true;
                        mCommentLikeImg.setColorFilter(mCommentLikeImg.getContext().getResources().getColor(R.color.colorPrimary));
                        mCommentLikeBtnText.setTypeface(mCommentLikeBtnText.getTypeface(), Typeface.BOLD);
                        mCommentLikeBtnText.setText("LIKED");
                        String dTotalLike = mCommentTotalLike.getText().toString();
                        int diTotalLike= Integer.parseInt(dTotalLike) ; diTotalLike = diTotalLike + 1;
                        mCommentTotalLike.setText(String.valueOf(diTotalLike));
                    }else{
                        dLikedPress = false;
                        //mCommentLikeImg.getContext().getResources().getColor(R.color.colorPrimaryDarkDEEP);
                        mCommentLikeImg.setColorFilter(mCommentLikeImg.getContext().getResources().getColor(R.color.colorDeepGrayBlue));
                        mCommentLikeBtnText.setTypeface(mCommentLikeBtnText.getTypeface(), Typeface.BOLD);
                        mCommentLikeBtnText.setText("LIKE");
                        String dTotalLike = mCommentTotalLike.getText().toString();
                        int diTotalLike= Integer.parseInt(dTotalLike); diTotalLike = diTotalLike - 1;
                        mCommentTotalLike.setText(String.valueOf(diTotalLike));
                    }
                }
            });
            mCommentReplyBtnText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int postion = getAdapterPosition();
                    if(postion != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(postion), postion, dAuthorName, UserPhotoUrl);

                    }
                }
            });
            mCommentViewMoreReplies.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int postion = getAdapterPosition();
                    if(postion != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(postion), postion,  dAuthorName, UserPhotoUrl);

                    }
                }
            });
            mCommentUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int postion = getAdapterPosition();
                    if(postion != RecyclerView.NO_POSITION && listener1 != null){
                        listener1.onItemClick(getSnapshots().getSnapshot(postion), postion,  dAuthorName, UserPhotoUrl);

                    }
                }
            });
        }
    }

    //for clicking recycler view item
    private ClickListenerPackage listener;
    private ClickListenerPackage listener1;
    private ClickListenerPackage listener3;

    public interface ClickListenerPackage {
        void onItemClick(DocumentSnapshot documentSnapshot, int postion, String UserName, String PhotoURL);
    }

    public void replyBtnClickListener(DiscussionCommentAdapter.ClickListenerPackage listener){
        this.listener = listener;
    }

    public void userInfoBtnClickListener(DiscussionCommentAdapter.ClickListenerPackage listener1){
        this.listener1 = listener1;
    }
}
