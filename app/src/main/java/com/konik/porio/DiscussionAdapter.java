package com.konik.porio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class DiscussionAdapter  extends FirestoreRecyclerAdapter<DiscussionModel, DiscussionAdapter.DiscussHolder> {
    public DiscussionAdapter(@NonNull FirestoreRecyclerOptions<DiscussionModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final DiscussHolder holder, int position, @NonNull DiscussionModel model) {
        holder.mDiscussTopicText.setText(model.getTopic());
        String dsCommentNo = String.valueOf(model.getComment());
        if(dsCommentNo.equals("0")){
            Random RandomComment = new Random();
            int diCommentNo = RandomComment.nextInt(12)+3;
            holder.mDiscussComment.setText(diCommentNo + " Comments");
        }else{
            holder.mDiscussComment.setText(dsCommentNo + " Comments");
        }

    }

    @NonNull
    @Override
    public DiscussionAdapter.DiscussHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_discussion_item, parent, false);
        return new DiscussHolder(view);
    }

    class DiscussHolder extends RecyclerView.ViewHolder {
        ImageView mDiscussBackImg;
        TextView mDiscussTopicText, mDiscussComment;
        Button mDiscussSaveBtn;
        CardView mCardView;

        public DiscussHolder(@NonNull View itemView) {
            super(itemView);
            mDiscussBackImg = (ImageView) itemView.findViewById(R.id.discuss_item_img);
            mDiscussTopicText = (TextView) itemView.findViewById(R.id.discuss_item_topic);
            mDiscussComment = (TextView) itemView.findViewById(R.id.discuss_total_comment);
            mDiscussSaveBtn = (Button) itemView.findViewById(R.id.discussion_submit_btn);
            mCardView = (CardView) itemView.findViewById(R.id.cardview_discuss_item);
            mDiscussTopicText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int postion = getAdapterPosition();
                    if (postion != RecyclerView.NO_POSITION && listener1 != null) {
                        listener1.onItemClick(getSnapshots().getSnapshot(postion), postion);

                    }
                }
            });
            mDiscussBackImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int postion = getAdapterPosition();
                    if (postion != RecyclerView.NO_POSITION && listener1 != null) {
                        listener1.onItemClick(getSnapshots().getSnapshot(postion), postion);

                    }
                }
            });
        }
    }

    private ClickListenerPackage listener1;

    public interface ClickListenerPackage {
        void onItemClick(DocumentSnapshot documentSnapshot, int postion);
    }

    public void OnClickDiscussionCard(DiscussionAdapter.ClickListenerPackage listener1) {
        this.listener1 = listener1;
    }
}