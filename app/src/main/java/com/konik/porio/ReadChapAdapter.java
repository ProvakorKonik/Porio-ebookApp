package com.konik.porio;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class ReadChapAdapter extends FirestoreRecyclerAdapter<ReadChapModel,ReadChapAdapter.ReadChapHolder> {

    public ReadChapAdapter(@NonNull FirestoreRecyclerOptions<ReadChapModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ReadChapHolder holder, int position, @NonNull ReadChapModel model) {
        holder.mReadStoryBtn.setText(model.getChapterNmae());

    }

    @NonNull
    @Override
    public ReadChapHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chapter_item_single, parent,false);
        return new ReadChapHolder(view);
    }
    class ReadChapHolder extends RecyclerView.ViewHolder{
        Button mReadStoryBtn;
        public ReadChapHolder(@NonNull View itemView){
            super(itemView);
            mReadStoryBtn = (Button)itemView.findViewById(R.id.chapter_item_btn);
            mReadStoryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }
    View itemv;
    // Add this

    @Override
    public void onDataChanged() {
        // do your thing
        if(getItemCount() == 0){
            /*Intent intent = new Intent(getApplicationContext(),ReadChap.class);//replace activity2 with ur intended activity
            intent.putExtra("sadf","sadf");//put whatever data you need to send
            itemv.getContext().startActivity(intent)*/
            Log.d("CHAP", "I am logging something informational!"+getItemCount());
        }else{
            Log.d("CHAP", "I am logging something informational!"+getItemCount());
        }

       // Toast.makeText(itemv.getContext(), "Clicked Laugh Vote", Toast.LENGTH_SHORT).show();
    }



    //for clicking recycler view item
    private ClickListenerPackageReadStory listener;

    public interface ClickListenerPackageReadStory {
        void onItemClick(DocumentSnapshot documentSnapshot, int postion);
    }
    public void chapterBtnClickListener(ClickListenerPackageReadStory listener){
        this.listener = listener;
    }
}
