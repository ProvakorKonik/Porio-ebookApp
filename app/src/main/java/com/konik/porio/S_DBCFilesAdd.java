package com.konik.porio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class S_DBCFilesAdd extends AppCompatActivity {
    private TextView mFilesAddHeadText;
    private TextView mFilesAddCommandText;
    private Button mFilesAddBtn;
    private RadioGroup mFilesAddRadioGroup;
    private RadioButton mPDFRadioBtn, mImageRadioBtn, mTextRadioBtn, mExcelRadioBtn, mDocsRadioBtn, mPowerPointBtn, mYoutubeRadioBtn;
    private RecyclerView mFilesRecyclerview;

    private LinearLayout mYoutubeLayout;
    private EditText mYoutubeEditLink;
    private ImageView mYoutubePasteImg;
    private Button mYoutubeVerifyBtn;

    private String dFileType = "NO", dClassUID = "NO", dBatchUID = "NO", dUserUID = "NO";
    private String dFileTypeSent = "";
    //Youtube Video
    private String youtube_video_id = "NO";
    private final static String expression = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

    //FILE CODE
    private static final int RESULT_LOAD_IMAGE1 = 1;
    private List<String> dFileNameList;
    private List<String> dFileDoneList;
    private List<String> dFileTypeList;
    private S_DBCFilesAddListAdapter file_add_list_adapter;

    //Firebase
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    private StorageReference FileToUpload;
    //////////FIREBASE
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s__d_b_c_files_add);

        mFilesAddHeadText = (TextView)findViewById(R.id.sdbcf_add_head_text);
        mFilesAddCommandText = (TextView)findViewById(R.id.sdbcf_file_type_text);
        mFilesAddBtn = (Button)findViewById(R.id.sdbcf_file_add_btn);
        mFilesAddRadioGroup = (RadioGroup) findViewById(R.id.sdbcf_add_radio_group);
        mPDFRadioBtn = (RadioButton)findViewById(R.id.sdbcf_add_radio_pdf);
        mImageRadioBtn =(RadioButton)findViewById(R.id.sdbcf_add_radio_image);
        mTextRadioBtn =(RadioButton)findViewById(R.id.sdbcf_add_radio_text);
        mExcelRadioBtn =(RadioButton)findViewById(R.id.sdbcf_add_radio_excel);
        mDocsRadioBtn =(RadioButton)findViewById(R.id.sdbcf_add_radio_docs);
        mPowerPointBtn = (RadioButton)findViewById(R.id.sdbcf_add_radio_powerpoint);
        mYoutubeRadioBtn = (RadioButton)findViewById(R.id.sdbcf_add_radio_youtube);
        mFilesRecyclerview = (RecyclerView)findViewById(R.id.sdbcf_file_recyclerview);

        mYoutubeLayout = (LinearLayout)findViewById(R.id.sdbcf_layout_youtube);
        mYoutubeEditLink = (EditText)findViewById(R.id.sdbcf_add_edit_youtube_link);
        mYoutubePasteImg = (ImageView)findViewById(R.id.sdbcf_add_paste_btn);
        mYoutubeVerifyBtn = (Button)findViewById(R.id.sdbcf_add_verify_yt_btn);

        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {   dClassUID = intent.getExtras().getString("Class_UID");
            dBatchUID = intent.getExtras().getString("Batch_UID");
            if (TextUtils.isEmpty(dClassUID)) {
                dClassUID= "NO";
                Toast.makeText(S_DBCFilesAdd.this, "CLASS UID NULL  " , Toast.LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(dBatchUID)) {
                dBatchUID= "NO";
                Toast.makeText(S_DBCFilesAdd.this, "Batch UID NULL  " , Toast.LENGTH_SHORT).show();
            }
            if (dClassUID.equals("")){
                dClassUID= "NO";
                Toast.makeText(S_DBCFilesAdd.this, "CLASS UID 404" , Toast.LENGTH_SHORT).show();
            }if (dBatchUID.equals("")){
            dBatchUID= "NO";
            Toast.makeText(S_DBCFilesAdd.this, "BATCH UID 404" , Toast.LENGTH_SHORT).show();
            }
        }else{
            dClassUID= "NO";
            dBatchUID = "NO";
            Toast.makeText(S_DBCFilesAdd.this, "Intent Not Found" , Toast.LENGTH_SHORT).show();
        }

        ///////////////AUTH CHECK
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    dUserUID = mAuth.getUid();
                }else{
                    Toast.makeText(getApplicationContext(),"Not Logged in",Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(S_DBCFilesAdd.this, MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        };

        mFilesAddRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(mPDFRadioBtn.isChecked()){
                    mFilesAddCommandText.setText("Upload PDF Files");
                    dFileType = "pdf";
                }else if(mImageRadioBtn.isChecked()){
                    mFilesAddCommandText.setText("Upload Image Files");
                    dFileType = "img";
                }else if(mTextRadioBtn.isChecked()){
                    mFilesAddCommandText.setText("Upload Text Files");
                    dFileType = "txt";
                }else if(mExcelRadioBtn.isChecked()){
                    mFilesAddCommandText.setText("Upload Excel Files");
                    dFileType = "exl";  //excel
                }else if(mDocsRadioBtn.isChecked()){
                    mFilesAddCommandText.setText("Upload Docs Files");
                    dFileType = "doc";  //docs
                }else if(mPowerPointBtn.isChecked()){
                    mFilesAddCommandText.setText("Upload PowerPoint Files");
                    dFileType = "pow"; // powerpoint
                }

                if(mYoutubeRadioBtn.isChecked()){
                    dFileType = "ytb";
                    mFilesAddCommandText.setText("Paste Youtube Link");
                    mFilesAddBtn.setVisibility(View.GONE);
                    mFilesRecyclerview.setVisibility(View.GONE);
                    mYoutubeLayout.setVisibility(View.VISIBLE);
                }else{
                    mFilesAddBtn.setVisibility(View.VISIBLE);
                    mFilesRecyclerview.setVisibility(View.VISIBLE);
                    mYoutubeLayout.setVisibility(View.GONE);
                }
            }
        });

        mFilesAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(dClassUID)) {
                    dClassUID= "NO";
                    Toast.makeText(S_DBCFilesAdd.this, "CLASS UID NULL  " , Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(dBatchUID)) {
                    dBatchUID= "NO";
                    Toast.makeText(S_DBCFilesAdd.this, "Batch UID NULL  " , Toast.LENGTH_SHORT).show();
                }if(!dBatchUID.equals("NO") && !dClassUID.equals("NO")){
                    method_choose_file();
                }

            }
        });


        mYoutubeVerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String youtube_URL = mYoutubeEditLink.getText().toString();
                youtube_video_id = getVideoId(youtube_URL);
                if (TextUtils.isEmpty(youtube_video_id)){
                    Toast.makeText(S_DBCFilesAdd.this, "NO LINK" , Toast.LENGTH_SHORT).show();
                }else if(youtube_video_id.equals("")){
                    Toast.makeText(S_DBCFilesAdd.this, "Fill Up First" , Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(S_DBCFilesAdd.this, S_DBCFilesAddYT.class);
                    intent.putExtra("YoutubeID",youtube_video_id);
                    intent.putExtra("Class_UID",dClassUID);
                    intent.putExtra("Batch_UID",dBatchUID);
                    startActivity(intent);
                }
            }
        });

        ////////FILE ADD
        dFileNameList = new ArrayList<>();
        dFileDoneList = new ArrayList<>();
        dFileTypeList = new ArrayList<>();
        file_add_list_adapter = new S_DBCFilesAddListAdapter(dFileNameList,dFileDoneList,dFileTypeList);
        //Recycler View
        mFilesRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mFilesRecyclerview.setHasFixedSize(true);
        mFilesRecyclerview.setAdapter(file_add_list_adapter);
    }

    private void method_choose_file() {
        if(dFileType.equals("NO")){
            Toast.makeText(S_DBCFilesAdd.this,"Select File Type",Toast.LENGTH_SHORT).show();;
        }else if(dFileType.equals("pdf")){
            Toast.makeText(S_DBCFilesAdd.this,"Select PDF Files",Toast.LENGTH_SHORT).show();;
            Intent intent = new Intent();
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), RESULT_LOAD_IMAGE1);

        }else if(dFileType.equals("img")){
            Toast.makeText(S_DBCFilesAdd.this,"Select Image Files",Toast.LENGTH_SHORT).show();;
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), RESULT_LOAD_IMAGE1);
        }else if(dFileType.equals("txt")){
            Toast.makeText(S_DBCFilesAdd.this,"Select Text Files",Toast.LENGTH_SHORT).show();;
            Intent intent = new Intent();
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), RESULT_LOAD_IMAGE1);
        }else if(dFileType.equals("exl")){
            Toast.makeText(S_DBCFilesAdd.this,"Select Excel Files",Toast.LENGTH_SHORT).show();;
            Intent intent = new Intent();
            //intent.setType("application/vnd.ms-excel|application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            //intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String[] mimeTypes =
                    {"application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                    };

            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), RESULT_LOAD_IMAGE1);
        }else if(dFileType.equals("doc")){
            Toast.makeText(S_DBCFilesAdd.this,"Select Docs Files",Toast.LENGTH_SHORT).show();;
            Intent intent = new Intent();
            intent.setType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), RESULT_LOAD_IMAGE1);
        }else if(dFileType.equals("pow")){
            Toast.makeText(S_DBCFilesAdd.this,"Select PowerPoint Files",Toast.LENGTH_SHORT).show();;
            Intent intent = new Intent();
            //intent.setType("application/vnd.ms-powerpoint|application/vnd.openxmlformats-officedocument.presentationml.presentation");
            //intent.setType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
            String[] mimeTypes =
                    {"application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // power point // .ppt & .pptx
                    };
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), RESULT_LOAD_IMAGE1);
        }else if(dFileType.equals("ytb")){
            Toast.makeText(S_DBCFilesAdd.this,"Select Youtube Files",Toast.LENGTH_SHORT).show();;
        }else{
            dFileType = "NO";
            Toast.makeText(S_DBCFilesAdd.this,"Select Type Again",Toast.LENGTH_SHORT).show();;
        }
    }

   public static String getVideoId(String videoUrl) {
        if (videoUrl == null || videoUrl.trim().length() <= 0){
            return null;
        }
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(videoUrl);
        try {
            if (matcher.find())
                return matcher.group();
        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    //Date, File Name , File Size, File Type -- > Database
    public String getFileName(Uri uri) {    //File Name from URI METHOD
        String result = null;

        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public String getSize(Uri uri) {
        String fileSize = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {

                // get file size
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (!cursor.isNull(sizeIndex)) {
                    fileSize = cursor.getString(sizeIndex);
                }
            }
        } finally {
            cursor.close();
        }
        return fileSize;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE1 && resultCode == RESULT_OK){
            if(data.getClipData() != null){
                ///Multiple file selected

                int dTotalItemSelected = data.getClipData().getItemCount();
                for(int i = 0; i<dTotalItemSelected; i++){
                    Uri dFileUri = data.getClipData().getItemAt(i).getUri();
                    final String dFileNmae = getFileName(dFileUri);
                    String dFileTypeLastWord = dFileNmae.substring(dFileNmae.lastIndexOf("."));
                    final String dFileSize = getSize(dFileUri);
                    double  dFileSizeDouble = Double.parseDouble(dFileSize);
                    int dMB = 1000;
                    dFileSizeDouble =  dFileSizeDouble/dMB;
                    dFileSizeDouble =  dFileSizeDouble/dMB;
                    String dFileTypeShow= dFileTypeLastWord +"      "+ String.format("%.2f", dFileSizeDouble)+"MB";
                    dFileTypeSent = dFileType + "X"+dFileTypeLastWord;
                    //Recycler view
                    dFileTypeList.add(dFileTypeShow);
                    dFileNameList.add(dFileNmae);
                    dFileDoneList.add("uploading");
                    file_add_list_adapter.notifyDataSetChanged();
                    final int dFinali = i;
                    //Firebase
                    final String dDate = String.valueOf(System.currentTimeMillis());
                    //ERROR 404                    .............+ BOOKUID + .........
                    FileToUpload = mStorage.child("ACADEMIC_DOCS/"+dBatchUID+"/"+dClassUID).child(dFileNmae+dDate);
                    FileToUpload.putFile(dFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(S_DBCFilesAdd.this, dFileNmae+" Uploaded File" , Toast.LENGTH_SHORT).show();
                            dFileDoneList.remove(dFinali);
                            dFileDoneList.add(dFinali, "done"); //insted of uploading word we set done
                            file_add_list_adapter.notifyDataSetChanged();;
                            FileToUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String dFileUrl = uri.toString();
                                    //set this to firestore database
                                    /////////UPLOADING DATA TO FIRESTORE/////////////

                                    //Total FIle + Class Date + Uploaded Time
                                    Map<String, Object> file_info = new HashMap<>();
                                    file_info.put("name", dFileNmae);
                                    file_info.put("link", dFileUrl);
                                    file_info.put("size", dFileSize);
                                    file_info.put("type", dFileTypeSent);
                                    file_info.put("date", dDate);
                                    file_info.put("by", dUserUID);

                                    db.collection("All_Type").document("FILES").collection(dClassUID).add(file_info)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    //dRandomUID =  documentReference.getId().toString();
                                                    //db.collection("DOCUMENT").document("BATCH").collection(dBookUID).document(dRandomUID).update("batch_uid",dRandomUID);
                                                    Toast.makeText(S_DBCFilesAdd.this,"Successfully Generated",Toast.LENGTH_SHORT).show();
                                                    mFilesAddBtn.setText("UPLOADED");
                                                    mFilesAddBtn.setEnabled(false);

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(S_DBCFilesAdd.this,"FAILED to Add Data",Toast.LENGTH_SHORT).show();
                                                    mFilesAddBtn.setText("FAILED");
                                                    mFilesAddBtn.setEnabled(false);
                                                }
                                            });
                            }
                        });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dFileDoneList.remove(dFinali);
                            dFileDoneList.add(dFinali, "failed");
                            file_add_list_adapter.notifyDataSetChanged();;
                            Toast.makeText(S_DBCFilesAdd.this, dFileNmae+"Failed File Uplaod !" , Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }else if(data.getData() != null){
                //selected single file
                Toast.makeText(S_DBCFilesAdd.this, "Single File Selected but no uploading option" , Toast.LENGTH_SHORT).show();
                Toast.makeText(S_DBCFilesAdd.this, "Sharing Files" , Toast.LENGTH_SHORT).show();


               // share_file_to_another_app(FileUri);
                Uri dFileUri = data.getData();
                    final String dFileNmae = getFileName(dFileUri);
                    String dFileTypeLastWord = dFileNmae.substring(dFileNmae.lastIndexOf("."));
                    final String dFileSize = getSize(dFileUri);
                    double  dFileSizeDouble = Double.parseDouble(dFileSize);
                    int dMB = 1000;
                    dFileSizeDouble =  dFileSizeDouble/dMB;
                    dFileSizeDouble =  dFileSizeDouble/dMB;
                    String dFileTypeShow= dFileTypeLastWord +"      "+ String.format("%.2f", dFileSizeDouble)+"MB";
                    dFileTypeSent = dFileType + "X"+dFileTypeLastWord;
                    //Recycler view
                    dFileTypeList.add(dFileTypeShow);
                    dFileNameList.add(dFileNmae);
                    dFileDoneList.add("uploading");
                    file_add_list_adapter.notifyDataSetChanged();

                    //Firebase
                    final String dDate = String.valueOf(System.currentTimeMillis());
                    //ERROR 404                    .............+ BOOKUID + .........
                    FileToUpload = mStorage.child("ACADEMIC_DOCS/"+dBatchUID+"/"+dClassUID).child(dFileNmae+dDate);
                    FileToUpload.putFile(dFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(S_DBCFilesAdd.this, dFileNmae+" Uploaded File" , Toast.LENGTH_SHORT).show();
                            dFileDoneList.remove(0);
                            dFileDoneList.add(0, "done"); //insted of uploading word we set done
                            file_add_list_adapter.notifyDataSetChanged();;
                            FileToUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String dFileUrl = uri.toString();
                                    //set this to firestore database
                                    /////////UPLOADING DATA TO FIRESTORE/////////////

                                    //Total FIle + Class Date + Uploaded Time
                                    Map<String, Object> file_info = new HashMap<>();
                                    file_info.put("name", dFileNmae);
                                    file_info.put("link", dFileUrl);
                                    file_info.put("size", dFileSize);
                                    file_info.put("type", dFileTypeSent);
                                    file_info.put("date", dDate);
                                    file_info.put("by", dUserUID);

                                    db.collection("All_Type").document("FILES").collection(dClassUID).add(file_info)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    //dRandomUID =  documentReference.getId().toString();
                                                    //db.collection("DOCUMENT").document("BATCH").collection(dBookUID).document(dRandomUID).update("batch_uid",dRandomUID);
                                                    Toast.makeText(S_DBCFilesAdd.this,"Successfully Generated",Toast.LENGTH_SHORT).show();
                                                    mFilesAddBtn.setText("UPLOADED");
                                                    mFilesAddBtn.setEnabled(false);

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(S_DBCFilesAdd.this,"FAILED to Add Data",Toast.LENGTH_SHORT).show();
                                                    mFilesAddBtn.setText("FAILED");
                                                    mFilesAddBtn.setEnabled(false);
                                                }
                                            });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dFileDoneList.remove(0);
                            dFileDoneList.add(0, "failed");
                            file_add_list_adapter.notifyDataSetChanged();;
                            Toast.makeText(S_DBCFilesAdd.this, dFileNmae+"Failed File Uplaod !" , Toast.LENGTH_SHORT).show();
                        }
                    });


            }else{
                Toast.makeText(S_DBCFilesAdd.this, "File Not Selected" , Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void share_file_to_another_app(Uri xFileUri) {
        /*Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(xFileUri,"pdf/*");
        i.putExtra(Intent.EXTRA_STREAM,xFileUri);
        startActivity(i);*/
        String mime = getContentResolver().getType(xFileUri);

        // Open file with user selected app
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(xFileUri, mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }
}