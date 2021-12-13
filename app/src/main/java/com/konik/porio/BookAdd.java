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
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

public class BookAdd extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference category_ref ;
    private DocumentReference documentReference;
    //Firebase Storage
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();;
    StorageReference ref;

    /////////FIREBASE
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page
    private DocumentReference user_ref ;
    //

    private ProgressBar mProgressbar;
    private Spinner mCategoryListSpinner;
    private RadioButton mRadioAcademic, mRadioUser, mRadioOfficial;
    private RadioGroup mRadioGroup;
    private ImageView mBookImage;
    private EditText mBookName, mBookCourseCode, mBookSummary, mBookPrice, mBookAuthorName, mBooKDiscount, mBookTotalView, mBookInStock;
    private TextView mHeadText,mBookDate, mBookTextRating, mBookTextPriority, mBookTextLevel;
    private NumberPicker mBookRatingNumb, mBookPriorityNumb, mBookLevelNumb;
    private Button mBookUploadBtn;
    private String dBookName = "NO", dBookCourseCode = "NO", dBookSummary = "NO", dBookPrice = "NO", dBookAuthorName = "NO", dBooKDiscount = "NO", dBookTotalView = "NO", dInStock = "NO";
    private String dBookDate = "NO", dBookType  = "NO", dCategoryUID = "NO", dBookTotalData = "1";
    String dSector, dPhotoURL = "NO", dCategoryType = "NO";
    String dUserUID = "NO", dUserName = "NO", dUserType = "NO",dUserTotalData = "NO";
    int diRating, diPriority, diLevel;
    ///CODE
    List < String > categories_list = new ArrayList<>();
    Map<String,String> map=new HashMap<String,String>();

    //GET USER INFORMATION
    private static final String sZero = "0";
    final private int iZero = 0;
    String     sAdminLevel = sZero,   sGenderType =  sZero,      sFollower =   sZero,          sTotalPost =   sZero,        sTotalDiscussion=   sZero, sTotalTaka =   sZero;
    String sTotalMegaByte  = sZero,   sTotalFileView =   sZero,  sTotalFileDownload =   sZero, sTotalFileUpload =   sZero,  sTotalReview = sZero;
    String sTotalLike = sZero,        sTotalComment = sZero,     sExtraE =   sZero,            sExtraF =   sZero;
    int iAdminLevel = iZero, iGenderType =  iZero, iFollower =   iZero, iTotalPost =   iZero, iTotalDiscussion=   iZero, iTotalTaka =   iZero, iTotalMegaByte  = iZero;
    int iTotalFileView =   iZero,  iTotalFileDownload =   iZero, iTotalFileUpload =   iZero, iTotalReview = iZero, iTotalLike = iZero, iTotalComment = iZero, iExtraE =   iZero, iExtraF =  iZero;
    //

    //Photo Selecting and Croping
    private final int CODE_IMG_GALLERY = 1;
    //private final String SAMPLE_CROPPED_IMG_NAME = "BookCoverCropImg";
    Uri imageUri_storage;
    Uri imageUriResultCrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_add);

        mProgressbar = (ProgressBar)findViewById(R.id.book_add_progressbar) ;
        mCategoryListSpinner =(Spinner)findViewById(R.id.book_add_spinner_category);
        mRadioGroup = (RadioGroup)findViewById(R.id.book_add_radio_group);
        mRadioAcademic = (RadioButton)findViewById(R.id.book_add_radio_academic);
        mRadioUser = (RadioButton)findViewById(R.id.book_add_radio_users);
        mRadioOfficial = (RadioButton)findViewById(R.id.book_add_radio_official);
        ///////INITIALIZE
        mHeadText = (TextView)findViewById(R.id.book_add_head_text);
        mBookImage = (ImageView)findViewById(R.id.add_book_image);
        mBookTextRating = (TextView)findViewById(R.id.book_add_text_rating);
        mBookTextPriority = (TextView)findViewById(R.id.book_add_text_priority);
        mBookTextLevel = (TextView)findViewById(R.id.book_add_text_product_level);
        mBookDate = (TextView)findViewById(R.id.book_add_text_publish_date);
        mBookRatingNumb = (NumberPicker)findViewById(R.id.book_add_rating_numb_picker);
        mBookPriorityNumb = (NumberPicker)findViewById(R.id.book_add_priorities_numb_picker);
        mBookLevelNumb = (NumberPicker)findViewById(R.id.book_add_product_level_numb_picker);
        mBookName = (EditText)findViewById(R.id.edit_add_books_name);
        mBookCourseCode = (EditText)findViewById(R.id.edit_add_books_course_code);
        mBookSummary = (EditText)findViewById(R.id.edit_add_summary);
        mBookAuthorName = (EditText)findViewById(R.id.edit_add_author_name);
        mBookPrice = (EditText)findViewById(R.id.edit_add_price);
        mBooKDiscount = (EditText)findViewById(R.id.edit_add_discount);
        mBookTotalView = (EditText)findViewById(R.id.edit_add_viewed);
        mBookInStock = (EditText)findViewById(R.id.edit_add_instock);
        mBookUploadBtn = (Button)findViewById(R.id.book_add_upload_btn);
        methodHideMode();
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                methodCheckUserType(dUserUID);
                if(mRadioAcademic.isChecked()){
                    dSector = "ACADEMIC";
                    methodFetchCategoryItem("ACADEMIC");
                }else if(mRadioUser.isChecked()){
                    dSector = "USER";
                    methodFetchCategoryItem("USER");
                    dSector = "PUBLISH";
                }else if(mRadioOfficial.isChecked()){
                    methodFetchCategoryItem("PUBLISH");
                }
            }
        });
        methodCheckUserLogin();
        mBookUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methodSentDataToServer();
            }
        });
        mBookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBookImage.setPadding(0,0,0,0);
                startActivityForResult(new Intent() //Image Selecting
                        .setAction(Intent.ACTION_GET_CONTENT)
                        .setType("image/*"), CODE_IMG_GALLERY);
            }
        });


    }

    private void methodSentDataToServer() {

        //except number picker
        //dSector   //dCategoryType // dCategoryUID
        //dPhotoURL
        diRating = mBookRatingNumb.getValue();
        diPriority = mBookPriorityNumb.getValue();
        diLevel = mBookLevelNumb.getValue();
        dBookName = mBookName.getText().toString();
        dBookCourseCode = mBookCourseCode.getText().toString();
        dBookSummary = mBookSummary.getText().toString();
        dBookAuthorName = mBookAuthorName.getText().toString();
        //dSelect Date
        dBookDate = String.valueOf(System.currentTimeMillis()); //Current Time
        dBookPrice = mBookPrice.getText().toString();
        dBooKDiscount = mBooKDiscount.getText().toString();
        dBookTotalView = mBookTotalView.getText().toString();
        dInStock = mBookInStock.getText().toString();
        if( dBooKDiscount.equals("") || dBookTotalView.equals("") || dInStock.equals("")){
            Toast.makeText(getApplicationContext(),"Discount Views Stock Not Filled"+dCategoryType,Toast.LENGTH_SHORT).show();//if User is not ADMIN
            dBooKDiscount = "0";
            dBookTotalView = "4";
            dInStock = "3";
        }


        if(dSector.equals(null) || dCategoryType.equals("NO")|| dCategoryType.equals("Choose Category")|| dCategoryType.equals("") || dBookName.equals("") || dBookCourseCode.equals("") || dBookSummary.equals("") ||
                     dBookDate.equals("Select Date") || dBookPrice.equals("") ){
            Toast.makeText(getApplicationContext(),"Fillup All"+dCategoryType,Toast.LENGTH_SHORT).show();
        }else if(imageUri_storage == null){
            Toast.makeText(getApplicationContext(),"Photo Not Selected"+dCategoryType,Toast.LENGTH_SHORT).show();
        }else if(imageUriResultCrop == null){
            Toast.makeText(getApplicationContext(),"Photo Not Croped"+dCategoryType,Toast.LENGTH_SHORT).show();
        }else if(methodCheckIntger(dBookPrice,dBooKDiscount,dBookTotalView,dInStock,dBookDate)){//Checking integer to string
            //if true then sent data to server



            dBookTotalData = "1T1E"+String.valueOf(diRating)+"R"+String.valueOf(diPriority)+"P"+String.valueOf(diLevel)+"L";
            dBookTotalData = dBookTotalData+ dBookPrice+"P"+ dBooKDiscount+"D"+dBookTotalView+"V"+dInStock + "S" + dBookDate +"T";
            //TERPLPDVST = Typr, EditNo, Rating Priority Level, Price, Discount, Views, InStock, Time

            if(dBookAuthorName.equals("")){
                dBookAuthorName = dUserUID;
            }else{
                dBookAuthorName = dBookAuthorName + " ";
            }
            /////////////ADD TO SERVER///////////////////////////////////////
            String dRandomUID = UUID.randomUUID().toString();
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();
                //System.currentTimeMills();
                ref = storageReference.child("PorioApp/Bangla/Book_Cover_Pic/"+dCategoryUID+"/"+ dRandomUID +"."+getFileExtention(imageUriResultCrop));
                ref.putFile(imageUriResultCrop)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            //Photo Uploaded now get the URL
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                float dProPicServerSize = taskSnapshot.getTotalByteCount() /1024 ;
                                //diSize = (int)dProPicServerSize;
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        dPhotoURL = uri.toString();
                                        Toast.makeText(BookAdd.this, "Photo Uploaded", Toast.LENGTH_SHORT).show();

                                        Map<String, Object> note = new HashMap<>();
                                        note.put("name", dBookName);
                                        note.put("code", dBookCourseCode); //course code
                                        note.put("summary", dBookSummary);
                                        note.put("author",dBookAuthorName);
                                        note.put("total_data",dBookTotalData);
                                        note.put("photoURL",dPhotoURL); //intger
                                        note.put("pdf","0"); //intger

                                        db.collection("All_Type").document("BOOKS").collection(dCategoryUID).add(note)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        Toast.makeText(BookAdd.this,"Uploading", Toast.LENGTH_SHORT).show();

                                                        String dBookUID = documentReference.getId();
                                                        if(dBookAuthorName.equals(dUserUID)){
                                                                Map<String, Object> book = new HashMap<>();
                                                                book.put("name", dBookName);
                                                                book.put("total_data",dBookTotalData);
                                                                book.put("catUID",dCategoryUID); //CatUID
                                                                book.put("photoURL",dPhotoURL);
                                                                book.put("last_update",dBookDate);
                                                                book.put("create_date",dBookDate);
                                                                book.put("views",0); //intger
                                                                                                                                                      //AUTHOR UID
                                                                db.collection("USER_DATA").document("UserBOOKS").collection(dBookAuthorName).document(dBookUID).set(book).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(BookAdd.this,"Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                                                        progressDialog.dismiss();
                                                                        mBookUploadBtn.setText("UPDATED");
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(BookAdd.this,"Failed Please Try Again", Toast.LENGTH_SHORT).show();
                                                                        mBookUploadBtn.setText("FAILED Information Sent");
                                                                    }
                                                                });
                                                        }
                                                        //mUserInfoName.setText("");
                                                        //mUserInfoBio.setText("");
                                                        //mUserInfoPhoneNo.setText("");
                                                        //finish();
                                                        //Intent intent = new Intent(BookAdd.this, MainActivity.class);
                                                        //intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                        //startActivity(intent);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(BookAdd.this,"Failed Please Try Again", Toast.LENGTH_SHORT).show();
                                                    mBookUploadBtn.setText("FAILED Information Sent");
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
                                mBookUploadBtn.setText("Failed Photo Upload");
                                Toast.makeText(BookAdd.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(),"OTHERS PROBLEM",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean methodCheckIntger(String dBookPrice, String dBooKDiscount, String dBookTotalView, String dInStock, String dBookDate) {

        if (dBookPrice.matches("[0-9]+") && dBookPrice.length() > 0) {

        }else{
            Toast.makeText(getApplicationContext(),"Type only Number on Price",Toast.LENGTH_SHORT).show();
            mBookPrice.setTextColor((getResources().getColor(R.color.colorSweetRed)));
            return false;   //integer not found
        }

        if (dBooKDiscount.matches("[0-9]+") && dBooKDiscount.length() > 0) {

        }else{
            Toast.makeText(getApplicationContext(),"Type Number Only on Discount",Toast.LENGTH_SHORT).show();
            mBooKDiscount.setTextColor((getResources().getColor(R.color.colorSweetRed)));
            return false;   //integer not found
        }

        if (dBookTotalView.matches("[0-9]+") && dBookTotalView.length() > 0) {

        }else{
            Toast.makeText(getApplicationContext(),"Type Number Only on Total VIew",Toast.LENGTH_SHORT).show();
            mBookTotalView.setTextColor((getResources().getColor(R.color.colorSweetRed)));
            return false;   //integer not found
        }

        if (dInStock.matches("[0-9]+") && dInStock.length() > 0) {

        }else{
            Toast.makeText(getApplicationContext(),"Type Number Only on InStock",Toast.LENGTH_SHORT).show();
            mBookTotalView.setTextColor((getResources().getColor(R.color.colorSweetRed)));
            return false;   //integer not found
        }
        if (dBookDate.matches("[0-9]+") && dBookDate.length() > 0) {

        }else{
            Toast.makeText(getApplicationContext(),"Time is not number",Toast.LENGTH_SHORT).show();
            //mBookTotalView.setTextColor((getResources().getColor(R.color.colorSweetRed)));
            return false;   //integer not found
        }

        return true; // all off these are integer
    }

    private void methodCheckUserLogin(){
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    dUserUID = FirebaseAuth.getInstance().getUid();

                }else{
                    Toast.makeText(getApplicationContext(),"Not Logged in",Toast.LENGTH_SHORT).show();
                    //signOut();
                    finish();
                    Intent intent = new Intent(BookAdd.this, MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        };
    }
    private void methodCheckUserType(String UserUid) {
        user_ref =  db.collection("USER_DATA").document("REGISTER").collection("NORMAL_USER").document(UserUid);
        user_ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    dUserName = documentSnapshot.getString(("name"));
                    dUserTotalData = documentSnapshot.getString(("total"));
                    methodGetUserTotalData(dUserTotalData);
                }else{
                    Toast.makeText(getApplicationContext(),"User Data Missing",Toast.LENGTH_SHORT).show();
                    finish();//signOut();
                    Intent intent = new Intent(BookAdd.this, UserInfoAdd.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void methodFetchCategoryItem(String sector) {
        mHeadText.setText("Loading...");
        mProgressbar.setVisibility(View.VISIBLE);
        mCategoryListSpinner.setVisibility(View.VISIBLE);
        categories_list.clear();
        map.clear();
        categories_list.add(0,"Choose Category");
        db.collection("All_Type").document("CATEGORY").collection(sector).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            //messageModel.setDocumentID(documentSnapshot.getId());
                            String dCategoryUID = documentSnapshot.getId();
                            String dCategoryItemName = documentSnapshot.getString("name");
                            map.put(dCategoryUID,dCategoryItemName);
                            categories_list.add(dCategoryItemName);
                        }
                        SpinnerDataSetup();
                        mProgressbar.setVisibility(View.GONE);
                        mHeadText.setText("Please Fillup All");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void SpinnerDataSetup() {
        ArrayAdapter<String> SpinnerdataAdapter;
        SpinnerdataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,categories_list);
        mCategoryListSpinner.setAdapter(SpinnerdataAdapter);
        mCategoryListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int postion, long l) {
                if(parent.getItemAtPosition(postion).equals("Choose Category")){

                }else{
                    String spinner_item = parent.getItemAtPosition(postion).toString();
                    String dGetValue= "";
                    String dSelectedCategoryUID = "";
                    for(Map.Entry m:map.entrySet()){
                        dGetValue = m.getValue().toString();
                        if(dGetValue.equals(spinner_item)) {
                            dSelectedCategoryUID = m.getKey().toString();
                            break;
                        }
                    }
                    dCategoryType = spinner_item;
                    dCategoryUID = dSelectedCategoryUID;
                    Toast.makeText(getApplicationContext(),dSelectedCategoryUID,Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(),"S "+spinner_item,Toast.LENGTH_SHORT).show();
                    //dCategory = spinner_item;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    private void methodGetUserTotalData(String dTotal) {
        int len = dTotal.length();
        Vector<String> vec = new Vector<String>();

        String target =  "AGFPDTMVDURLCEF";
        String new_word = "";
        int i = 0; int j = 0;
        for(i = 0; i<len; i++){

            if(dTotal.charAt(i) == target.charAt(j)){
                vec.add(new_word);
                new_word = "";
                j++;
            }else{
                new_word += dTotal.charAt(i);
            }

        }
        if(vec.size() == 15){
            sAdminLevel = vec.elementAt(0);
            sGenderType = vec.elementAt(1);
            sFollower = vec.elementAt(2);
            sTotalPost = vec.elementAt(3);
            sTotalDiscussion= vec.elementAt(4);
            sTotalTaka = vec.elementAt(5);
            sTotalMegaByte = vec.elementAt(6);
            sTotalFileView = vec.elementAt(7);
            sTotalFileDownload = vec.elementAt(8);
            sTotalFileUpload = vec.elementAt(9);
            sTotalReview = vec.elementAt(10);
            sTotalLike = vec.elementAt(11);
            sTotalComment = vec.elementAt(12);
            sExtraE = vec.elementAt(13);
            sExtraF = vec.elementAt(14);
            ///String Finish, Intger ConvertStart
            iAdminLevel = Integer.parseInt(sAdminLevel);
            iGenderType = Integer.parseInt(sGenderType);
            iFollower = Integer.parseInt(sFollower);
            iTotalPost = Integer.parseInt(sTotalPost);
            iTotalDiscussion= Integer.parseInt(sTotalDiscussion);
            iTotalTaka = Integer.parseInt(sTotalTaka);
            iTotalMegaByte = Integer.parseInt(sTotalMegaByte);
            iTotalFileView = Integer.parseInt(sTotalFileView);
            iTotalFileDownload = Integer.parseInt(sTotalFileDownload);
            iTotalFileUpload = Integer.parseInt(sTotalFileUpload);
            iTotalReview =Integer.parseInt(sTotalReview);
            iTotalLike = Integer.parseInt(sTotalLike);
            iTotalComment = Integer.parseInt(sTotalComment);
            iExtraE = Integer.parseInt(sExtraE);
            iExtraF = Integer.parseInt(sExtraF);
            ///////////////////////FETCH FINISH
            //NOW INISITALIZE
            if(iAdminLevel == 1){
                methodAdminMode();
            }else{
                methodUserMode();

            }
            mProgressbar.setVisibility(View.GONE);
        }else{
            Toast.makeText(BookAdd.this,"Failed to fetch TotalData", Toast.LENGTH_SHORT).show();;
        }
    }
    private void methodNumbPickerSetup() {
        mBookRatingNumb.setMinValue(1);
        mBookRatingNumb.setMaxValue(100);
        mBookPriorityNumb.setMinValue(1);
        mBookPriorityNumb.setMaxValue(10);
        mBookLevelNumb.setMinValue(1);
        mBookLevelNumb.setMaxValue(10);

        mBookRatingNumb.setValue(5);
        mBookPriorityNumb.setValue(9);
        mBookLevelNumb.setValue(3);

        mBookPriorityNumb.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                //dPriority = newVal;
                //Toast.makeText(getApplicationContext(),dPriority+"",Toast.LENGTH_SHORT).show();
            }
        });

        mBookRatingNumb.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                //dRating = newVal;
                //Toast.makeText(getApplicationContext(),dRating+"",Toast.LENGTH_SHORT).show();
            }
        });

        mBookLevelNumb.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                //dProductLevel = newVal;
                //Toast.makeText(getApplicationContext(),dProductLevel+"",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void methodAdminMode() {
        methodNumbPickerSetup();
        mRadioOfficial.setVisibility(View.VISIBLE);
        mBookTextRating.setVisibility(View.VISIBLE);
        mBookTextPriority.setVisibility(View.VISIBLE);
        mBookTextLevel.setVisibility(View.VISIBLE);
        mBookRatingNumb.setVisibility(View.VISIBLE);
        mBookPriorityNumb.setVisibility(View.VISIBLE);
        mBookLevelNumb.setVisibility(View.VISIBLE);
        mBookAuthorName.setVisibility(View.VISIBLE);
        mBooKDiscount.setVisibility(View.VISIBLE);
        mBookTotalView.setVisibility(View.VISIBLE);
        mBookInStock.setVisibility(View.VISIBLE);

        mCategoryListSpinner.setVisibility(View.VISIBLE);
        mBookImage.setVisibility(View.VISIBLE);
        mBookName.setVisibility(View.VISIBLE);
        mBookDate.setVisibility(View.VISIBLE);
        mBookCourseCode.setVisibility(View.VISIBLE);
        mBookSummary.setVisibility(View.VISIBLE);
        mBookPrice.setVisibility(View.VISIBLE);
        mBookUploadBtn.setVisibility(View.VISIBLE);
    }
    private void methodUserMode() {

        mRadioOfficial.setVisibility(View.GONE);
        mBookTextRating.setVisibility(View.GONE);
        mBookTextPriority.setVisibility(View.GONE);
        mBookTextLevel.setVisibility(View.GONE);
        mBookRatingNumb.setVisibility(View.GONE);
        mBookPriorityNumb.setVisibility(View.GONE);
        mBookLevelNumb.setVisibility(View.GONE);
        mBookAuthorName.setVisibility(View.GONE);
        mBooKDiscount.setVisibility(View.GONE);
        mBookTotalView.setVisibility(View.GONE);
        mBookInStock.setVisibility(View.GONE);

        mCategoryListSpinner.setVisibility(View.VISIBLE);
        mBookImage.setVisibility(View.VISIBLE);
        mBookName.setVisibility(View.VISIBLE);
        mBookDate.setVisibility(View.VISIBLE);
        mBookCourseCode.setVisibility(View.VISIBLE);
        mBookSummary.setVisibility(View.VISIBLE);
        mBookPrice.setVisibility(View.VISIBLE);
        mBookUploadBtn.setVisibility(View.VISIBLE);
    }
    private void methodHideMode() {
        mProgressbar.setVisibility(View.GONE);
        mBookUploadBtn.setVisibility(View.GONE);
        mCategoryListSpinner.setVisibility(View.GONE);
        mRadioOfficial.setVisibility(View.GONE);
        mBookTextRating.setVisibility(View.GONE);
        mBookTextPriority.setVisibility(View.GONE);
        mBookTextLevel.setVisibility(View.GONE);
        mBookRatingNumb.setVisibility(View.GONE);
        mBookPriorityNumb.setVisibility(View.GONE);
        mBookLevelNumb.setVisibility(View.GONE);
        mBookAuthorName.setVisibility(View.GONE);
        mBooKDiscount.setVisibility(View.GONE);
        mBookTotalView.setVisibility(View.GONE);
        mBookInStock.setVisibility(View.GONE);

        mBookImage.setVisibility(View.GONE);
        mBookName.setVisibility(View.GONE);
        mBookDate.setVisibility(View.GONE);
        mBookCourseCode.setVisibility(View.GONE);
        mBookSummary.setVisibility(View.GONE);
        mBookPrice.setVisibility(View.GONE);
    }
    private String getFileExtention(Uri uri){   //IMAGE
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        //Not worked in Croped File so i constant it
        return "JPEG";
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }
    ///////////PHOTO CROP
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
                Picasso.get().load(imageUri_storage).into(mBookImage);
                Toast.makeText(BookAdd.this,"Selected",Toast.LENGTH_SHORT).show();
                startCrop(imageUri_storage);
            }else{
                Toast.makeText(this, "Failed! (File is Larger Than 5MB)",Toast.LENGTH_SHORT).show();
            }


        }else if(requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK){
            //Photo Cropped
            imageUriResultCrop = UCrop.getOutput(data);
            if(imageUriResultCrop != null){
                Picasso.get().load(imageUriResultCrop).into(mBookImage);
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
        //dRandomUID = UUID.randomUUID().toString();
        final String SAMPLE_CROPPED_IMG_NAME = UUID.randomUUID().toString();
        if(imageUri_storage != null){
            String destinationFileName = SAMPLE_CROPPED_IMG_NAME;
            destinationFileName += "jpg";

            UCrop ucrop = UCrop.of(uri,Uri.fromFile(new File(getCacheDir(),destinationFileName)));
            ucrop.withAspectRatio(11,16);
            ucrop.withOptions(getCropOptions());
            ucrop.start(BookAdd.this);

        }else{
            Toast.makeText(BookAdd.this, "image URI NULL ", Toast.LENGTH_SHORT).show();
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
}