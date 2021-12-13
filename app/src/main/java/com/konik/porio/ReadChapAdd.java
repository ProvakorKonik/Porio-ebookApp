package com.konik.porio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

public class ReadChapAdd extends AppCompatActivity {
    String dCategoryUID = "NO",dBookUID = "NO", dSectorType = "NO", dUserUID = "NO";
    ////////////////////////////////////////////////////////////
    private Button mBtnSelectImage, mUploadStoryBtn;
    private ImageView mChapterPhoto;
    private EditText mChapterName, mChapterText, mChapterPrioriy;

    private String dChapterNmae = "", dChapterText = "", dChapterPhotoUrl = "NO", dChapterPriority = "";

    //PHOTO SELECTED
    String dPhotoURL = "",dRandomUID = "";
    Uri imageUri_storage;
    Uri imageUriResultCrop;
    private final int CODE_IMG_GALLERY = 1;
    private final String SAMPLE_CROPPED_IMG_NAME = "SampleCropIng";

    StorageReference storageReference = FirebaseStorage.getInstance().getReference();;
    StorageReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_chap_add);
        mBtnSelectImage = (Button)findViewById(R.id.add_chap_photo_select_btn);
        mUploadStoryBtn = (Button)findViewById(R.id.add_chap_upload_btn);
        mChapterPhoto = (ImageView) findViewById(R.id.add_chap_select_photo);
        mChapterName = (EditText)findViewById(R.id.edit_add_chapter_name);
        mChapterText = (EditText)findViewById(R.id.edit_add_chap_text);
        mChapterPrioriy = (EditText)findViewById(R.id.edit_add_chap_priority_no);
        intentCollectionFunc();

        mBtnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn_name = mBtnSelectImage.getText().toString();
                if(btn_name.equals("Add Image") ){
                    startActivityForResult(new Intent()
                            .setAction(Intent.ACTION_GET_CONTENT)
                            .setType("image/*"), CODE_IMG_GALLERY);
                }else if(btn_name.equals("CROP")){
                    startCrop(imageUri_storage);
                }else if(btn_name.equals("UPLOAD") || btn_name.equals("UPLOAD AGAIN")){
                    if(imageUriResultCrop != null){
                        UploadCropedImageFunction(imageUriResultCrop);
                    }else{
                        UploadCropedImageFunction(imageUri_storage);
                    }

                }
            }
        });
        mUploadStoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUriResultCrop != null){
                    UploadCropedImageFunction(imageUriResultCrop);
                }else{
                    UploadCropedImageFunction(imageUri_storage);
                }
            }
        });
    }

    @Override   //Selecting Image
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CODE_IMG_GALLERY && resultCode == RESULT_OK &&  data.getData() != null && data != null){
            //Photo Successfully Selected
            imageUri_storage = data.getData();
            Picasso.get().load(imageUri_storage).into(mChapterPhoto);
            mBtnSelectImage.setText("CROP");
            //Toast.makeText(getApplicationContext(),"Selected",Toast.LENGTH_SHORT).show();
        }else if(requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK){
            //Photo Cropped
            imageUriResultCrop = UCrop.getOutput(data);
            if(imageUriResultCrop != null){
                //mSelectPhoto.setImageURI(imageUriResultCrop);
                Picasso.get().load(imageUriResultCrop).into(mChapterPhoto);
                /*File file = FileUtils.getFile(Photo.this, imageUri);
                InputStream inputStream = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);*/
                //Toast.makeText(this, imageUriResultCrop.getPath(),Toast.LENGTH_SHORT).show();
                mBtnSelectImage.setText("UPLOAD");
            }else{
                Toast.makeText(getApplicationContext(),"Not Croped",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void UploadChapterData(String URL){

        dChapterPhotoUrl = URL;
        if(TextUtils.isEmpty(dChapterPhotoUrl)){
            dChapterPhotoUrl = "NO";
            Toast.makeText(ReadChapAdd.this,"Uploading without Image",Toast.LENGTH_SHORT).show();
        }else if(dChapterPhotoUrl.equals("NO")){
            dChapterPhotoUrl = "NO";
            Toast.makeText(ReadChapAdd.this,"Uploading without Image",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(ReadChapAdd.this,"Uploading with Image",Toast.LENGTH_SHORT).show();
        }
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference storyRef_db = db.collection("All_Type").document("Chapter").collection(dSectorType).document(dCategoryUID).collection(dBookUID);  //for quires of data
            //ERROR 404 GETID
            ReadChapModel singleBookAddStoriesModel = new ReadChapModel(dChapterNmae,dChapterText,dChapterPhotoUrl,dChapterPriority);
            storyRef_db.add(singleBookAddStoriesModel).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    mChapterName.setText("");
                    mChapterText.setText("");
                    mChapterPrioriy.setText("");
                    dChapterPhotoUrl = "";
                    mBtnSelectImage.setText("UPLOADING WITHOUT IMAGE");
                    mBtnSelectImage.setEnabled(true);
                    mUploadStoryBtn.setText("UPLOAD AGAIN");

                    dChapterPriority = "";
                    dChapterNmae = "";
                    dChapterPhotoUrl = "";
                    Toast.makeText(ReadChapAdd.this,"Successfully Uploaded All",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ReadChapAdd.this,"Failed to Upload Data ! "+e,Toast.LENGTH_SHORT).show();
                }
            });


    }
    private void UploadCropedImageFunction(Uri filePath) {
        //Uplaoding Photo to FireStorage
        dChapterNmae = mChapterName.getText().toString();
        dChapterText = mChapterText.getText().toString();
        dChapterPriority = mChapterPrioriy.getText().toString();
        if(dChapterNmae.equals("") || dChapterText.equals("") || dChapterPriority.equals("") || dBookUID.equals("NO") ||  TextUtils.isEmpty(dBookUID) ){
            Toast.makeText(ReadChapAdd.this,"Fill Up ALL",Toast.LENGTH_SHORT).show();
        }else if(filePath != null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            dRandomUID = UUID.randomUUID().toString();//System.currentTimeMills();


            ref = storageReference.child("ChapterImages/"+dSectorType+"/"+dCategoryUID+"/"+dBookUID+"/"+ dRandomUID +"."+getFileExtention(filePath));
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            short_delay(1000);
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    dPhotoURL = uri.toString();
                                    mBtnSelectImage.setText("UPLOADED");
                                    UploadChapterData(dPhotoURL);
                                    Toast.makeText(ReadChapAdd.this, "Image UPLOADED", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            mBtnSelectImage.setText("UPLOAD AGAIN");
                            Toast.makeText(ReadChapAdd.this, "Image Upload Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }else{
            UploadChapterData("NO");
            Toast.makeText(ReadChapAdd.this, "Photo URI Null", Toast.LENGTH_SHORT).show();
        }
    }
    private void startCrop(@NonNull Uri uri){
        if(imageUri_storage != null){
            String destinationFileName = SAMPLE_CROPPED_IMG_NAME;
            destinationFileName += "jpg";

            UCrop ucrop = UCrop.of(uri,Uri.fromFile(new File(getCacheDir(),destinationFileName)));
            ucrop.withAspectRatio(1,1);
            //ucrop.withAspectRatio(2,3);
            //ucrop.useSourceImageAspectRatio();
            //ucrop.withAspectRatio(2,4);
            //ucrop.withAspectRatio(570,245);
            //ucrop.withMaxResultSize(266,401);
            ucrop.withOptions(getCropOptions());
            ucrop.start(ReadChapAdd.this);
        }else{
            Toast.makeText(ReadChapAdd.this, "image URI NULL ", Toast.LENGTH_SHORT).show();
        }

    }
    private UCrop.Options getCropOptions(){
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(100);

        //Compress Type
        //options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        //options.setCompressionFormat(Bitmap.CompressFormat.JPEG);

        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);
        //Colors
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        options.setToolbarTitle("Konik Crop");

        return options;
    }
    private String getFileExtention(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        //Not worked in Croped File so i constant it
        return "JPEG";
    }
    private void short_delay(int delay){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //mPrograsbar.setProgress(0);
            }
        }, delay); //delay for 5 seconds
    }
    private void intentCollectionFunc() {
        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {
            dCategoryUID = intent.getExtras().getString("Category_UID");
            dBookUID = intent.getExtras().getString("Book_UID");
            dSectorType = intent.getExtras().getString("SECTOR");
            if (TextUtils.isEmpty(dCategoryUID)) {
                dCategoryUID= "NO";
                Toast.makeText(ReadChapAdd.this, "Category_UID NULL  " , Toast.LENGTH_SHORT).show();
            }
            if (dCategoryUID.equals("")){
                dCategoryUID= "NO";
                Toast.makeText(ReadChapAdd.this, "BOOK UID 404" , Toast.LENGTH_SHORT).show();
            }if (TextUtils.isEmpty(dSectorType)) {
            dSectorType= "NO";
            Toast.makeText(ReadChapAdd.this, "SectorType NULL  " , Toast.LENGTH_SHORT).show();
        }else if (dSectorType.equals("")){
            dSectorType= "NO";
            Toast.makeText(ReadChapAdd.this, "dSectorType 404" , Toast.LENGTH_SHORT).show();
        }
            if (TextUtils.isEmpty(dBookUID)) {
                dBookUID= "NO";
                Toast.makeText(ReadChapAdd.this, "BOOK_UID NULL  " , Toast.LENGTH_SHORT).show();
            }else if (dBookUID.equals("")){
                dBookUID= "NO";
                Toast.makeText(ReadChapAdd.this, "Book UID 404" , Toast.LENGTH_SHORT).show();
            }

        }else{
            dCategoryUID = "NO";
            dBookUID= "NO";
            dSectorType= "NO";
            Toast.makeText(ReadChapAdd.this, "Intent Not Found" , Toast.LENGTH_SHORT).show();
        }
    }
}