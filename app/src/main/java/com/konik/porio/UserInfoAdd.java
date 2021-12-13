package com.konik.porio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserInfoAdd extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private ImageView mUserProfilePic;
    private EditText mUserInfoName, mUserInfoPhoneNo, mUserInfoBio;
    private RadioGroup mRadioGenderGroup;
    private RadioButton mRadioGenderMale, mRadioGenderFemale;
    private TextView mBirthdate;
    private Button mUserInfoUpdateBtn;
    //private static final String NO = "NO";
    private long dBirthDate = 0;
    private String dUserName = "NO", dUserBio = "NO", dUserPhone = "NO", dGender = "NO";
    private String dUserUID = "NO",dUserEmail = "NO", dUserRegistrationDate = "NO", dUserLastActivity = "NO";
    private String dExtra = "NO"; int diSize = 0, diGender = 0;
    //Photo Selecting and Croping
    private final int CODE_IMG_GALLERY = 1;
    private final String SAMPLE_CROPPED_IMG_NAME = "SampleCropIng";
    Uri imageUri_storage;
    Uri imageUriResultCrop;

    //Firebase Storage
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();;
    StorageReference ref;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page

    private DocumentReference category_ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_add);

        mUserProfilePic = (ImageView)findViewById(R.id.image_add_user_name);
        mUserInfoName = (EditText)findViewById(R.id.edit_add_user_name);
        mUserInfoBio = (EditText)findViewById(R.id.edit_add_user_bio);
        mUserInfoPhoneNo = (EditText)findViewById(R.id.edit_add_user_phone);
        mRadioGenderGroup =(RadioGroup)findViewById(R.id.user_add_info_radio_group);
        mRadioGenderMale =(RadioButton)findViewById(R.id.radio_male);
        mRadioGenderFemale =(RadioButton) findViewById(R.id.radio_female);
        mBirthdate = (TextView)findViewById(R.id.edit_add_user_birthdate);
        mUserInfoUpdateBtn = (Button)findViewById(R.id.user_infor_update_btn);

        mBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
        mUserInfoUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!mRadioGenderMale.isChecked() && !mRadioGenderFemale.isChecked()){
                    dGender = "N0";
                    Toast.makeText(UserInfoAdd.this,"Please Select Gender", Toast.LENGTH_SHORT).show();;
                }else if(mRadioGenderMale.isChecked()){
                    dGender = mRadioGenderMale.getText().toString();
                }else if(mRadioGenderFemale.isChecked()){
                    dGender = mRadioGenderFemale.getText().toString();
                }
                dUserName = mUserInfoName.getText().toString();
                dUserBio = mUserInfoBio.getText().toString();
                dUserPhone = mUserInfoPhoneNo.getText().toString();
                if(imageUriResultCrop == null){
                    if(imageUri_storage == null){
                        Toast.makeText(UserInfoAdd.this,"Please Select Image", Toast.LENGTH_SHORT).show();;
                    }else{
                        Toast.makeText(UserInfoAdd.this,"Please Crop Image", Toast.LENGTH_SHORT).show();;
                    }

                }else if(dUserName.equals("") || dUserBio.equals("") || dGender.equals("") || dBirthDate == 0){
                    Toast.makeText(UserInfoAdd.this,"Please Fill Up All", Toast.LENGTH_SHORT).show();;
                }else{
                    String date = String.valueOf(System.currentTimeMillis());
                    dUserRegistrationDate = date;
                    dUserLastActivity = date;
                    if(dUserPhone.equals("")){
                        dUserPhone = "123";
                    }
                    if(dGender.equals("Female")){
                        diGender = 2;   //FEMALE
                    }else{
                        diGender = 1;   //MALE
                    }
                    UploadCropedImageFunction(imageUriResultCrop);
                }
            }
        });

        mUserProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent() //Image Selecting
                        .setAction(Intent.ACTION_GET_CONTENT)
                        .setType("image/*"), CODE_IMG_GALLERY);
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    dUserUID = mAuth.getUid();
                    dUserEmail = mAuth.getCurrentUser().getEmail();
                }else{
                    Toast.makeText(getApplicationContext(),"Not Logged in",Toast.LENGTH_SHORT).show();
                    //signOut();
                    finish();
                    Intent intent = new Intent(UserInfoAdd.this, MainActivity.class);
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
            dUserUID = FirebaseAuth.getInstance().getUid();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
                                                                         //System.currentTimeMills();
            ref = storageReference.child("PorioApp/Bangla/Users_Profile_Pic/"+ dUserUID +"."+getFileExtention(filePath));
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
                                    Toast.makeText(UserInfoAdd.this, "Photo Uploaded", Toast.LENGTH_SHORT).show();
                                    String dTotal = methodSetUserTotalData(  9,diGender,150,121,0,0,
                                            diSize,0,0,0,0,
                                            0,0,0,0);

                                    Map<String, Object> note = new HashMap<>();
                                    note.put("name", dUserName);
                                    note.put("email", dUserEmail); //map is done
                                    note.put("birth_reg", String.valueOf(dBirthDate)+"B"+dUserRegistrationDate+"R");
                                    note.put("uid",dUserUID);
                                    note.put("bio",dUserBio);
                                    note.put("photoURL",dPhotoURL);
                                    note.put("lastActivity",dUserLastActivity);
                                    note.put("phone_no",dUserPhone);;
                                    note.put("total",dTotal);       //String
                                    //note.put("gender", dGender);
                                    //note.put("type","NORMAL");
                                    //note.put("follower","0");
                                    //note.put("post","0");
                                    //note.put("reg_date",dUserRegistrationDate);
                                    db.collection("USER_DATA").document("REGISTER").collection("NORMAL_USER").document(dUserUID).set(note)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(UserInfoAdd.this,"Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                    mUserInfoUpdateBtn.setText("UPDATED");
                                                    mUserInfoName.setText("");
                                                    mUserInfoBio.setText("");
                                                    mUserInfoPhoneNo.setText("");
                                                    finish();
                                                    Intent intent = new Intent(UserInfoAdd.this, MainActivity.class);
                                                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                    startActivity(intent);

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(UserInfoAdd.this,"Failed Please Try Again", Toast.LENGTH_SHORT).show();
                                            mUserInfoUpdateBtn.setText("FAILED Information Sent");
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
                            mUserInfoUpdateBtn.setText("Failed Photo Upload");
                            Toast.makeText(UserInfoAdd.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(UserInfoAdd.this, "Upload Failed Photo Not Found ", Toast.LENGTH_SHORT).show();
        }
    }

    private String methodSetUserTotalData(int AdminLevel,      int GenderType,     int Follower,       int TotalPost,      int TotalDiscussion,
                                   int TotalTaka,       int TotalMegaByte,  int TotalFileView,  int TotalFileDownload,
                                   int TotalFileUplaod, int TotalReview,    int TotalLike,      int TotalComment,   int ExtraE,   int ExtraF){
        String sAdminLevel = String.valueOf(AdminLevel) + "A";
        String sGenderType = String.valueOf(GenderType) + "G";
        String sFollower = String.valueOf(Follower) + "F";
        String sTotalPost = String.valueOf(TotalPost) + "P";
        String sTotalDiscussion= String.valueOf(TotalDiscussion) + "D";
        String sTotalTaka = String.valueOf(TotalTaka) + "T";
        String sTotalMegaByte = String.valueOf(TotalMegaByte) + "M";
        String sTotalFileView = String.valueOf(TotalFileView) + "V";
        String sTotalFileDownload = String.valueOf(TotalFileDownload) + "D";
        String sTotalFileUpload = String.valueOf(TotalFileUplaod) + "U";

        String sTotalReview = String.valueOf(TotalReview) + "R";
        String sTotalLike = String.valueOf(TotalLike) + "L";
        String sTotalComment = String.valueOf(TotalComment) + "C";
        String sExtraE = String.valueOf(ExtraE) + "E";
        String sExtraF = String.valueOf(ExtraF) + "F";

        //"AGF PDT MVD URL CEF";
        String dTotalString = sAdminLevel+sGenderType+sFollower+sTotalPost+sTotalDiscussion+sTotalTaka+sTotalMegaByte+sTotalFileView+sTotalFileDownload+sTotalFileUpload+sTotalReview+sTotalLike+sTotalComment+sExtraE+sExtraF;
        int len = dTotalString.length();
        String target =  "AGFPDTMVDURLCEF";

        int i = 0; int j = 0;
        for(i = 0; i<len; i++){
            if(dTotalString.charAt(i) == target.charAt(j)){
                j++;
            }
        }
        if(j != 15){
            dTotalString = "0A0G0F0P0D0T0M0V0D0U0R0L0C0E0F";
        }
        return dTotalString;
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
                Picasso.get().load(imageUri_storage).resize(200, 200).centerCrop().into(mUserProfilePic);
                Toast.makeText(UserInfoAdd.this,"Selected",Toast.LENGTH_SHORT).show();
                startCrop(imageUri_storage);
            }else{
                Toast.makeText(this, "Failed! (File is Larger Than 5MB)",Toast.LENGTH_SHORT).show();
            }


        }else if(requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK){
            //Photo Cropped
            imageUriResultCrop = UCrop.getOutput(data);
            if(imageUriResultCrop != null){
                Picasso.get().load(imageUriResultCrop).into(mUserProfilePic);
                /*String dFileSizeAfterCrop = getSize(imageUriResultCrop);
                double  dFileSizeDouble = Double.parseDouble(dFileSizeAfterCrop);
                int dMB = 1000;
                dFileSizeDouble =  dFileSizeDouble/dMB; Toast.makeText(this, "Size = "+dFileSizeDouble,Toast.LENGTH_SHORT).show();
                */

                /*File file = FileUtils.getFile(Photo.this, imageUri);
                InputStream inputStream = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);*/

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
            ucrop.withAspectRatio(1,1);
            ucrop.withOptions(getCropOptions());
            ucrop.start(UserInfoAdd.this);

        }else{
            Toast.makeText(UserInfoAdd.this, "image URI NULL ", Toast.LENGTH_SHORT).show();
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
    //Date Picker
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        dBirthDate  = c.getTimeInMillis();

        mBirthdate.setText(currentDateString);
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