package com.konik.porio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.protobuf.DescriptorProtos;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CategoryAddInfo extends AppCompatActivity {
    private ImageView mCategorAddImg;
    private EditText mCategoryName, mCategoryType, mCategoryPriority, mCategoryTotalData;
    private RadioButton mCatRadioAcademic, mCatRadioUsers, mCatRadioPublish;
    private Button mCategoryUpdateBtn;

    //Photo Selecting and Croping
    private final int CODE_IMG_GALLERY = 1;
    private final String SAMPLE_CROPPED_IMG_NAME = "SampleCropIng";
    Uri imageUri_storage;
    Uri imageUriResultCrop;

    //Firebase Storage
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();;
    StorageReference ref;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference category_ref, categoryLinkref ;
    ///////////////////STRING
    //String dUserUID = "NO";
    String dRadioResult = "NO",dRandomUID = "NO";
    int diSize = 0, diRadioResult = 0;
    String dCategoryName = "NO", dCategoryType = "NO", dCategoryPriority = "NO", dCategoryTotalData = "NO", dSectorType ="NO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_add_info);

        mCategorAddImg = (ImageView)findViewById(R.id.cat_add_image);
        mCategoryName = (EditText)findViewById(R.id.edit_cat_name);
        mCategoryType = (EditText)findViewById(R.id.edit_cat_add_type);
        mCategoryPriority = (EditText)findViewById(R.id.edit_cat_add_priority);
        mCategoryTotalData = (EditText)findViewById(R.id.edit_cat_add_total_data);
        mCategoryUpdateBtn = (Button)findViewById(R.id.cat_update_btn);
        mCatRadioAcademic = (RadioButton)findViewById(R.id.radio_academic);
        mCatRadioUsers = (RadioButton)findViewById(R.id.radio_users);
        mCatRadioPublish = (RadioButton)findViewById(R.id.radio_published);

        mCategorAddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent() //Image Selecting
                        .setAction(Intent.ACTION_GET_CONTENT)
                        .setType("image/*"), CODE_IMG_GALLERY);
            }
        });
        mCategoryUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 dCategoryName = mCategoryName.getText().toString();
                 dCategoryType = mCategoryType.getText().toString();
                 dCategoryPriority = mCategoryPriority.getText().toString();
                 dCategoryTotalData = mCategoryTotalData.getText().toString();

                if(!mCatRadioAcademic.isChecked() && !mCatRadioUsers.isChecked() && !mCatRadioPublish.isChecked()){
                    Toast.makeText(getApplicationContext(),"Select Academic or User or Publish",Toast.LENGTH_SHORT).show();
                }else if(dCategoryName.equals("") || dCategoryType.equals("") || dCategoryPriority.equals("") || dCategoryTotalData.equals("")){
                    Toast.makeText(getApplicationContext(),"Please fillup all",Toast.LENGTH_SHORT).show();
                }else if(imageUriResultCrop == null){
                    if(imageUri_storage == null){
                        Toast.makeText(CategoryAddInfo.this,"Please Select Image", Toast.LENGTH_SHORT).show();;
                    }else{
                        Toast.makeText(CategoryAddInfo.this,"Please Crop Image", Toast.LENGTH_SHORT).show();;
                    }
                }else{
                    UploadCropedImageFunction(imageUriResultCrop);
                }

            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){

                }else{
                    Toast.makeText(getApplicationContext(),"Not Logged in",Toast.LENGTH_SHORT).show();
                    //signOut();
                    finish();
                    Intent intent = new Intent(CategoryAddInfo.this, MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        };
    }
    //Uplaoding Photo to FireStorage
    private void UploadCropedImageFunction(Uri filePath) {
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            //System.currentTimeMills();
            dRandomUID = UUID.randomUUID().toString();
            if(mCatRadioAcademic.isChecked()){
                diRadioResult = 0;
                dRadioResult = mCatRadioAcademic.getText().toString();
                ref = storageReference.child("PorioApp/Bangla/Sector/Category_Academic/"+ dRandomUID +"."+getFileExtention(filePath));
                category_ref = db.collection("All_Type").document("CATEGORY").collection("ACADEMIC");
                dSectorType = "ACADEMIC";
            }else if(mCatRadioUsers.isChecked()){
                diRadioResult = 1;
                dRadioResult = mCatRadioUsers.getText().toString();
                ref = storageReference.child("PorioApp/Bangla/Sector/Category_User/"+ dRandomUID +"."+getFileExtention(filePath));
                category_ref = db.collection("All_Type").document("CATEGORY").collection("USER");
                dSectorType = "USER";
            }else if(mCatRadioPublish.isChecked()){
                diRadioResult = 2;
                dRadioResult = mCatRadioPublish.getText().toString();
                ref = storageReference.child("PorioApp/Bangla/Sector/Category_Publish/"+ dRandomUID +"."+getFileExtention(filePath));
                category_ref = db.collection("All_Type").document("CATEGORY").collection("PUBLISH");
                dSectorType = "PUBLISH";
            }
            categoryLinkref = db.collection("All_Type").document("CATEGORYLink").collection("AllCategoryInfo");
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        //Photo Uploaded now get the URL
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            float dProPicServerSize = taskSnapshot.getTotalByteCount() /1024 ;
                            diSize = (int)dProPicServerSize;
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String dPhotoURL = uri.toString();
                                    Toast.makeText(CategoryAddInfo.this, "Photo Uploaded", Toast.LENGTH_SHORT).show();
                                    Map<String, Object> note = new HashMap<>();
                                    note.put("name", dCategoryName);
                                    note.put("type", dCategoryType);
                                    note.put("priority", dCategoryPriority);
                                    note.put("photo_url", dPhotoURL);
                                    note.put("total_data", dCategoryTotalData);

                                    category_ref.add(note)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {

                                                    String dCategoryUID = documentReference.getId();
                                                    Map<String, Object> categoryLinkInfo = new HashMap<>();
                                                    categoryLinkInfo.put("name", dCategoryName);
                                                    categoryLinkInfo.put("sector", dSectorType);
                                                    categoryLinkInfo.put("catUID", dCategoryUID);
                                                    categoryLinkInfo.put("serial", 25);
                                                    categoryLinkref.add(categoryLinkInfo)
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                @Override
                                                                public void onSuccess(DocumentReference documentReference) {
                                                                    Toast.makeText(CategoryAddInfo.this,"Successfully Uploaded", Toast.LENGTH_SHORT).show();

                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(CategoryAddInfo.this,"Failed Please Try Again", Toast.LENGTH_SHORT).show();
                                                            mCategoryUpdateBtn.setText("FAILED Information Sent");
                                                        }
                                                    });

                                                    progressDialog.dismiss();
                                                    mCategoryUpdateBtn.setText("UPDATED");
                                                    mCategoryName.setText("");
                                                    mCategoryPriority.setText("");
                                                    imageUri_storage = null;
                                                    imageUriResultCrop = null;

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(CategoryAddInfo.this,"Failed Please Try Again", Toast.LENGTH_SHORT).show();
                                                    mCategoryUpdateBtn.setText("FAILED Information Sent");
                                                }
                                            });

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            mCategoryUpdateBtn.setText("Failed Photo Upload");
                            Toast.makeText(CategoryAddInfo.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(CategoryAddInfo.this, "Upload Failed Photo Not Found ", Toast.LENGTH_SHORT).show();
        }
    }
    @Override   //Selecting Image
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CODE_IMG_GALLERY && resultCode == RESULT_OK &&  data.getData() != null && data != null){
            //Photo Successfully Selected
            imageUri_storage = data.getData();
            String dFileSize = getSize(imageUri_storage);       //GETTING IMAGE FILE SIZE
            double  dFileSizeDouble = Double.parseDouble(dFileSize);
            int dMB = 1000;
            dFileSizeDouble =  dFileSizeDouble/dMB;
            //dFileSizeDouble =  dFileSizeDouble/dMB;

            if(dFileSizeDouble <= 5000){
                Picasso.get().load(imageUri_storage).resize(350, 150).centerCrop().into(mCategorAddImg);
                Toast.makeText(CategoryAddInfo.this,"Selected",Toast.LENGTH_SHORT).show();
                startCrop(imageUri_storage);
            }else{
                Toast.makeText(this, "Failed! (File is Larger Than 5MB)",Toast.LENGTH_SHORT).show();
            }


        }else if(requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK){
            //Photo Cropped
            imageUriResultCrop = UCrop.getOutput(data);
            if(imageUriResultCrop != null){
                Picasso.get().load(imageUriResultCrop).resize(350, 150).centerCrop().into(mCategorAddImg);
                Toast.makeText(this, "Croped "+imageUriResultCrop.getPath(),Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(this,"Not Croped",Toast.LENGTH_SHORT).show();
            }
        }
    }
    //Croping Function
    private void startCrop(@NonNull Uri uri){


        if(imageUri_storage != null){
            String destinationFileName = SAMPLE_CROPPED_IMG_NAME;
            destinationFileName += "jpg";

            UCrop ucrop = UCrop.of(uri,Uri.fromFile(new File(getCacheDir(),destinationFileName)));
            //ucrop.withAspectRatio(1,1);
            ucrop.withAspectRatio(350,150);
            ucrop.withOptions(getCropOptions());
            ucrop.start(CategoryAddInfo.this);

        }else{
            Toast.makeText(CategoryAddInfo.this, "image URI NULL ", Toast.LENGTH_SHORT).show();
        }
    }
    private UCrop.Options getCropOptions(){
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(100);

        //Compress Type
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);

        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false);
        //Colors
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        options.setToolbarTitle("Crop Image");

        return options;
    }
    private String getFileExtention(Uri uri){   //IMAGE
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        //Not worked in Croped File so i constant it
        return "JPEG";
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
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }
}