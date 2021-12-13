package com.konik.porio;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class S_DBCFilesAddListAdapter extends RecyclerView.Adapter<S_DBCFilesAddListAdapter.FileHolder> {

    public List<String> fileNameList;
    public List<String> fileDoneList;
    public List<String> fileTypeList;

    public S_DBCFilesAddListAdapter(List<String>fileNameList, List<String>fileDoneList, List<String>fileTypeList){
        this.fileDoneList =fileDoneList;
        this.fileNameList = fileNameList;
        this.fileTypeList = fileTypeList;
    }


    @NonNull
    @Override
    public S_DBCFilesAddListAdapter.FileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.s_file_upload_single_item, parent,false);
        return new FileHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull S_DBCFilesAddListAdapter.FileHolder holder, int position) {
        String dFileName = fileNameList.get(position);
        holder.mFileNameText.setText(dFileName);
        String dFileDone = fileDoneList.get(position);
        String dFileType = fileTypeList.get(position);
        holder.mFileTypeText.setText("File Type " +dFileType);
        if(dFileDone.equals("uploading")){
            holder.mFileProgressText.setText("Progress : uploading...");
        }else if(dFileDone.equals("done")){
            holder.mFileProgressText.setText("Progress : done");
            holder.mFileCloudImg.setImageResource(R.drawable.ic_baseline_cloud_done_24);
        }else{
            holder.mFileProgressText.setText("Progress : wait");
        }
    }

    @Override
    public int getItemCount() {
        return fileNameList.size();
    }

    public class FileHolder extends  RecyclerView.ViewHolder{
        View mView;
        TextView mFileNameText, mFileProgressText, mFileTypeText, mFileSizeText;
        ImageView mFileCloudImg;
        public FileHolder(View itemview){
            super(itemview);
            mView = itemview;
            mFileNameText = (TextView)itemview.findViewById(R.id.sdbcf_add_filename);
            mFileProgressText = (TextView)itemview.findViewById(R.id.sdbcf_add_progress);
            mFileCloudImg = (ImageView)itemview.findViewById(R.id.sdbcf_add_file_cloud_icon);
            mFileTypeText = (TextView)itemview.findViewById(R.id.sdbcf_add_file_type);
            mFileSizeText = (TextView)itemview.findViewById(R.id.sdbcf_add_file_size);
        }
    }
}
