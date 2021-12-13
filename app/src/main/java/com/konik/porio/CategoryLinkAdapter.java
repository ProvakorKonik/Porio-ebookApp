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

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryLinkAdapter extends FirestoreRecyclerAdapter<CategoryLinkModel, CategoryLinkAdapter.CategoryHolder> {
    public CategoryLinkAdapter(@NonNull FirestoreRecyclerOptions<CategoryLinkModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final CategoryLinkAdapter.CategoryHolder holder, int position, @NonNull CategoryLinkModel model) {
        holder.mCategoryBtn.setText(model.getName());

    }

    @NonNull
    @Override
    public CategoryLinkAdapter.CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_category_all_item, parent, false);
        return new CategoryLinkAdapter.CategoryHolder(view);
    }

    class CategoryHolder extends RecyclerView.ViewHolder {

        Button mCategoryBtn;

        public CategoryHolder(@NonNull View itemView) {
            super(itemView);

            mCategoryBtn = (Button) itemView.findViewById(R.id.card_category_btn);

            mCategoryBtn.setOnClickListener(new View.OnClickListener() {
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

    private CategoryLinkAdapter.ClickListenerPackage listener1;

    public interface ClickListenerPackage {
        void onItemClick(DocumentSnapshot documentSnapshot, int postion);
    }

    public void OnClickDiscussionCard(CategoryLinkAdapter.ClickListenerPackage listener1) {
        this.listener1 = listener1;
    }
}