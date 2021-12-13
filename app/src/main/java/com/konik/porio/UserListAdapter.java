package com.konik.porio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.sql.Time;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class UserListAdapter extends FirestoreRecyclerAdapter<UserListModel, UserListAdapter.AllUserHolder> {
    public UserListAdapter(@NonNull FirestoreRecyclerOptions<UserListModel> options) {
        super(options);
    }
    /*private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String dCategoryUID = "USER";
    String dCategroyName = "NO";*/

    @Override
    protected void onBindViewHolder(@NonNull final UserListAdapter.AllUserHolder holder, int position, @NonNull UserListModel model) {
        holder.mUserNameTxt.setText(model.getName());
        String dUserLastActivity = model.getLastActivity();
        String dTotalData = model.getTotal();
        String dImageUrl = model.getPhotoURL();
        holder.mUserActiveTxt.setText(TimeAgo(dUserLastActivity));

        Picasso.get().load(dImageUrl).into(holder.mUserProfilePic);
        Random konik = new Random();
        float test = 5 - konik.nextFloat() * (5 - 3);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        holder.mUserTotalViewText.setText(konik.nextInt(2300)+"+");
        holder.mUserRatingTxt.setText(df.format(test)+" ");

        if(model.getName().equals("The Mentor")){
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            //for Visible the itemview
            //holder.itemView.setVisibility(View.VISIBLE);
            //holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    @NonNull
    @Override
    public UserListAdapter.AllUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_all_user_per_item, parent, false);
        return new UserListAdapter.AllUserHolder(view);
    }

    class AllUserHolder extends RecyclerView.ViewHolder {
        CardView mUserCard;
        ImageView mUserProfilePic;
        ImageView mUserStarImg, mUserEyeImg;
        TextView mUserActiveTxt, mUserNameTxt, mUserRatingTxt, mUserTotalViewText;

        ImageButton mUserProfileBtn ;

        public AllUserHolder(@NonNull View itemView) {
            super(itemView);
            mUserCard = (CardView)itemView.findViewById(R.id.cardview_user_info) ;
            mUserProfilePic = (ImageView)itemView.findViewById(R.id.all_user_profile_pic);
            mUserNameTxt = (TextView)itemView.findViewById(R.id.all_user_name);
            mUserActiveTxt = (TextView)itemView.findViewById(R.id.all_user_active_time_text);
            mUserRatingTxt = (TextView)itemView.findViewById(R.id.all_user_rating_text);
            mUserEyeImg = (ImageView)itemView.findViewById(R.id.all_user_eye_img);
            mUserTotalViewText = (TextView)itemView.findViewById(R.id.all_user_total_view_text);
            mUserProfileBtn = (ImageButton)itemView.findViewById(R.id.all_user_profile_btn);

            //Declared Button one
            mUserProfileBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int postion = getAdapterPosition();
                    if(postion != RecyclerView.NO_POSITION && listener2 != null){
                        listener2.onItemClick(getSnapshots().getSnapshot(postion), postion);
                    }
                }
            });
            /*mViewBookBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int postion = getAdapterPosition();
                    if(postion != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(postion), postion);
                    }
                }
            });*/

        }
    }

    //for clicking recycler view item
    private UserListAdapter.ClickListenerPackage listener;
    private UserListAdapter.ClickListenerPackage listener2;
    private UserListAdapter.ClickListenerPackage listener3;

    public interface ClickListenerPackage {
        void onItemClick(DocumentSnapshot documentSnapshot, int postion);
    }

    public void viewBtnClickListener(UserListAdapter.ClickListenerPackage listener){
        this.listener = listener;
    }

    public void addBtnClickListener(UserListAdapter.ClickListenerPackage listener){
        this.listener2 = listener;
    }







    ////EXTRAAA
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
                dtime = "Active Now";//seconds+" seconds ago";
            else if(minutes<60)
                dtime = "Active "+ minutes+" minutes ago";
            else if(hours<24)
                dtime = "Active "+ hours+" hours ago";
            else
                dtime = "Active "+days+" days ago";


        }
        catch (Exception j){
            j.printStackTrace();
        }

        return dtime;
    }
}