package com.konik.porio;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class S_DocsBatchAdapter extends RecyclerView.Adapter<S_DocsBatchAdapter.DocsBatchHolder>  {
    private Context mContext;
    private List<S_DocsBatchModel> mData;
    private RecylerviewClickInterface recylerviewClickInterface;
    public S_DocsBatchAdapter(Context mContext, List<S_DocsBatchModel> mData, RecylerviewClickInterface recylerviewClickInterface) {
        this.mContext = mContext;
        this.mData = mData;
        this.recylerviewClickInterface = recylerviewClickInterface;
    }

    @NonNull
    @Override
    public S_DocsBatchAdapter.DocsBatchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.s_batch_single_item,parent,false); //connecting to cardview
        return new S_DocsBatchAdapter.DocsBatchHolder(view);
    }
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference user_data_ref = db.collection("USER_DATA").document("REGISTER");

    @Override
    public void onBindViewHolder(@NonNull final S_DocsBatchAdapter.DocsBatchHolder holder, int position) {
        String dBatchName = mData.get(position).getBatch_name();
        dBatchName = dBatchName.replace('#', ' ');
        holder.mBatchName.setText(dBatchName);
        holder.mBatchCourseTeacherName.setText("Course Teacher: "+mData.get(position).getTeacher());

        Random random = new Random(); //Random number from 0 to 900 but its start from 100 to 999 because of adding 100
        int dLikeCount = random.nextInt(60) + 40;
        int dCommentCount = random.nextInt(10);
        holder.mBatchLikeCount.setText(String.valueOf(dLikeCount)) ;
        holder.mBatchCommentCount.setText(String.valueOf(dCommentCount));



        String dUploadedbyUserUID = mData.get(position).getUploaded_by();
        dUploadedbyUserUID = dUploadedbyUserUID.substring(1);
        if(dUploadedbyUserUID.length() >= 20){
            db.collection("USER_DATA").document("REGISTER").collection("NORMAL_USER").document(dUploadedbyUserUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        String dAuthorName =documentSnapshot.getString("name");
                        //UserPhotoUrl =documentSnapshot.getString("photoURL");
                        holder.mBatchUploadedBy.setText("by "+dAuthorName);
                        /*if(UserPhotoUrl.equals("NO")){

                        }else if(UserPhotoUrl.equals(null)){

                        }else{
                            Picasso.get().load(UserPhotoUrl).into(holder.mCommentUserImg);
                        }*/
                    }else{
                        String dAuthorName = "NO";
                        holder.mBatchUploadedBy.setText("by "+dAuthorName);
                    }
                }
            });
        }else{
            holder.mBatchUploadedBy.setText("by "+dUploadedbyUserUID);
        }
    }
    private boolean  dLikedPress = false;
    @Override
    public int getItemCount() {
        return mData.size();
    }
    public class DocsBatchHolder extends  RecyclerView.ViewHolder{
        ImageView mBatchLikeImg, mBatchCommentImg;
        TextView mBatchName, mBatchCourseStartFrom, mBatchCourseTeacherName;
        TextView mBatchUploadedBy, mBatchUploadedTime;
        TextView mBatchLikeCount, mBatchCommentCount;
        public DocsBatchHolder(@NonNull View itemView){
            super(itemView);
            mBatchLikeImg = (ImageView)itemView.findViewById(R.id.batch_like_img);
            mBatchCommentImg = (ImageView)itemView.findViewById(R.id.batch_comment_img);

            mBatchName = (TextView)itemView.findViewById(R.id.batch_name);
            mBatchCourseTeacherName = (TextView)itemView.findViewById(R.id.batch_course_teacher);
            mBatchCourseStartFrom = (TextView)itemView.findViewById(R.id.batch_start_from);
            mBatchUploadedBy = (TextView)itemView.findViewById(R.id.batch_uploaded_by);
            mBatchUploadedTime = (TextView)itemView.findViewById(R.id.batch_uploaded_time);
            mBatchLikeCount = (TextView)itemView.findViewById(R.id.batch_like_count);
            mBatchCommentCount = (TextView)itemView.findViewById(R.id.batch_comment_count);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recylerviewClickInterface .onItemClick(getAdapterPosition());
                }
            });

            mBatchLikeImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //recylerviewClickInterface.onLikeImgCLick(getAdapterPosition());

                    if(dLikedPress == false){
                        dLikedPress = true;
                        int dLikeCount  = Integer.parseInt(mBatchLikeCount.getText().toString());
                        mBatchLikeCount.setText(String.valueOf(dLikeCount+1));
                        mBatchLikeImg.setColorFilter(mContext.getResources().getColor(R.color.colorPrimaryDark));
                        mBatchLikeCount.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
                    }else{
                        dLikedPress = false;
                        //mCommentLikeImg.getContext().getResources().getColor(R.color.colorPrimaryDarkDEEP);
                        int dLikeCount  = Integer.parseInt(mBatchLikeCount.getText().toString());
                        mBatchLikeCount.setText(String.valueOf(dLikeCount-1));
                        mBatchLikeImg.setColorFilter(mContext.getResources().getColor(R.color.colorTextDeep));
                        mBatchLikeCount.setTextColor(mContext.getResources().getColor(R.color.colorTextDeep));
                    }

                    /*String dColorTag = mBatchLikeImg.getTag().toString();
                    if(dColorTag.equals("")){
                        mBatchLikeImg.setTag("LIKE");
                        int dLikeCount  = Integer.parseInt(mBatchLikeCount.getText().toString());
                        mBatchLikeCount.setText(String.valueOf(dLikeCount+1));
                        mBatchLikeImg.setColorFilter(mContext.getResources().getColor(R.color.colorSweetRed));
                    }else if(dColorTag.equals("LIKE")){
                        mBatchLikeImg.setTag("UNLIKE");
                        int dLikeCount  = Integer.parseInt(mBatchLikeCount.getText().toString());
                        mBatchLikeCount.setText(String.valueOf(dLikeCount-1));
                        mBatchLikeImg.setColorFilter(mContext.getResources().getColor(R.color.colorOffWhite));
                    }else{

                    }

                    String word = mBatchLikeImg.getTag().toString();
                    mBatchName.setText(word+ " ");*/

                }
            });
            mBatchLikeCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //recylerviewClickInterface .onItemClick(getAdapterPosition());
                    int dLikeCount  = Integer.parseInt(mBatchLikeCount.getText().toString());
                    mBatchLikeCount.setText(String.valueOf(dLikeCount+1));
                }
            });

        }
    }
}
