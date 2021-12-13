package com.konik.porio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import static com.konik.porio.R.drawable.ic_baseline_add_shopping_cart_24;
import static com.konik.porio.R.drawable.ic_baseline_shopping_cart_24;

public class BookInfo extends AppCompatActivity {
    private ImageView mBookImage;
    private ImageView mBookCartImage;
    private TextView mBookName, mBookAuthorName, mBookCategoryName, mBookPublishDate, mBookView;
    private TextView mBookRatingHeadText;
    private RatingBar mBookRatingLevel;
    private Button mBookPrice;
    private Button  mBookReadBtn;
    private Button mBookWriteReviewBtn;
    private EditText mBookWriteEditText;
    private RatingBar mBookWriteReviewRatingbar;
    private TextView mSummaryText;
    private Button mReadMoreBtn,mReadChapter;
    private Button mDocumentShowBtn;
    private RecyclerView  recyclerView;
    String dCategoryUID = "NO", dCategoryNAME = "NO", dBookUID = "NO", dSectorType = "NO";
    String dUserUID = "NO",dBookAuthorUID = "NO"; boolean dUserIsAuthor = false; //for checking the user is author or not
    String dSummary = "NO", dPdfLink = "NO";float dfBookRate = 0;
    boolean dAddedToCart = false;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page
    //FIREBASE
    private FirebaseFirestore db = FirebaseFirestore.getInstance();;
    private DocumentReference single_book_db_ref;
    //Firebase Review Recycler
    private CollectionReference book_all_review_ref ;
    private BookInfoReviewAdapter book_review_adapter;

    ///PDF Uploading
    private static final int RESULT_LOAD_IMAGE1 = 1;
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    private StorageReference FileToUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);
        mBookReadBtn = (Button)findViewById(R.id.book_info_read_btn);
        mBookImage =(ImageView)findViewById(R.id.book_info_image);
        mBookName = (TextView)findViewById(R.id.book_info_name);
        mBookAuthorName = (TextView)findViewById(R.id.book_info_author_name);
        mBookCategoryName = (TextView)findViewById(R.id.book_info_cat);
        mBookPublishDate = (TextView)findViewById(R.id.book_info_publish_date);
        mBookPrice = (Button)findViewById(R.id.book_info_price);
        mBookCartImage = (ImageView) findViewById(R.id.book_info_cart_image);
        mBookView = (TextView)findViewById(R.id.book_info_views);
        mBookRatingLevel = (RatingBar) findViewById(R.id.book_info_rating_bar);


        mBookWriteReviewBtn = (Button) findViewById(R.id.book_info_write_review_btn);
        mBookWriteEditText = (EditText) findViewById(R.id.edit_write_review);
        mBookWriteReviewRatingbar = (RatingBar)findViewById(R.id.write_rating);
        mBookRatingHeadText = (TextView)findViewById(R.id.book_info_review_rating_text);
        recyclerView = findViewById(R.id.recycler_review_n_ratings);
        //////////////GET INTENT DATA
        intentCollectionFunc();
        mAuth = FirebaseAuth.getInstance();     //AUTH CHECKING
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    dUserUID = FirebaseAuth.getInstance().getUid();
                    //Toast.makeText(BookInfo.this,"USER LOGGED IN",Toast.LENGTH_SHORT).show();
                }else{
                    dUserUID = "NO";
                    //Toast.makeText(BookInfo.this,"USER NOT LOGGED IN",Toast.LENGTH_SHORT).show();
                }
            }
        };

        /////////READ MORE BTN
        mBookReadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        BookInfo.this,R.style.BottomSheetDialogeTheme
                );
                View bottomsheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.single_book_bottom_sheet,
                                (LinearLayout)findViewById(R.id.singlebook_bottom_sheet)
                        );

                mSummaryText = (TextView) bottomsheetView.findViewById(R.id.single_book_summery_txt);
                mReadMoreBtn = (Button) bottomsheetView.findViewById(R.id.single_book_readmorebtn);
                mReadChapter = (Button) bottomsheetView.findViewById(R.id.single_book_read_chap);
                mDocumentShowBtn = (Button) bottomsheetView.findViewById(R.id.single_book_document);
                mSummaryText.setText(dSummary);
                if(dPdfLink.equals("NO")){
                    mReadMoreBtn.setText("LOADING...");
                }else if(dPdfLink.equals("0")){
                    mReadMoreBtn.setText("Add PDF");
                }else{
                    mReadMoreBtn.setText("Read More");
                }

                    mReadMoreBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(mReadMoreBtn.getText().toString().equals("Add PDF")){
                                AddPdfLinkFun();
                            }else if(mReadMoreBtn.getText().toString().equals("Read More")){
                                    if(TextUtils.isEmpty(dBookUID)){
                                        Toast.makeText(BookInfo.this,"BOOK UID NULL",Toast.LENGTH_SHORT).show();
                                    }else if(dBookUID.equals("")){
                                        Toast.makeText(BookInfo.this,"BOOK UID 404",Toast.LENGTH_SHORT).show();
                                    }else{
                                        Intent intent = new Intent(BookInfo.this,ReadPdf.class);
                                        intent.putExtra("Book_UID",dBookUID);
                                        intent.putExtra("Category_UID",dCategoryUID);
                                        intent.putExtra("PDF_LINK",dPdfLink);
                                        intent.putExtra("SECTOR",dSectorType);
                                        //intent.putExtra("BOOK_IMAGE_URL",dBookImageUrl);*/
                                        startActivity(intent);
                                    }
                                    bottomSheetDialog.dismiss();;
                            }else{

                            }

                        }
                    });

                if(dSectorType.equals("ACADEMIC")){
                    mReadMoreBtn.setVisibility(View.VISIBLE);
                    mDocumentShowBtn.setVisibility(View.VISIBLE);
                    mReadChapter.setVisibility(View.GONE);
                }else if(dSectorType.equals("USER")){
                    mReadMoreBtn.setVisibility(View.GONE);
                    mDocumentShowBtn.setVisibility(View.GONE);
                    mReadChapter.setVisibility(View.VISIBLE);
                }else{
                    mReadMoreBtn.setVisibility(View.GONE);
                    mDocumentShowBtn.setVisibility(View.GONE);
                }


                //Go to Chapter List and Show a Chapter
                mReadChapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(TextUtils.isEmpty(dBookUID)){
                            Toast.makeText(BookInfo.this,"BOOK UID NULL",Toast.LENGTH_SHORT).show();
                        }else if(dBookUID.equals("")){
                            Toast.makeText(BookInfo.this,"BOOK UID 404",Toast.LENGTH_SHORT).show();
                        }else{

                            Intent intent = new Intent(BookInfo.this,ReadChap.class);
                            intent.putExtra("Book_UID",dBookUID);
                            intent.putExtra("Category_UID",dCategoryUID);
                            intent.putExtra("SECTOR",dSectorType);
                            startActivity(intent);
                        }



                        bottomSheetDialog.dismiss();;
                    }
                });

                //GO TO DOCUMENT's by ALL BATCH LIST
                mDocumentShowBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(TextUtils.isEmpty(dBookUID)){
                            Toast.makeText(BookInfo.this,"BOOK UID NULL",Toast.LENGTH_SHORT).show();
                        }else if(dBookUID.equals("")){
                            Toast.makeText(BookInfo.this,"BOOK UID 404",Toast.LENGTH_SHORT).show();
                        }else{
                            //Toast.makeText(SingleBookInfo.this, "BOOK image "+dBookImageUrl , Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(BookInfo.this,S_DocsBatch.class);
                            intent.putExtra("Book_UID",dBookUID);
                            intent.putExtra("Category_UID",dCategoryUID);
                            intent.putExtra("SECTOR",dSectorType);
                            /*intent.putExtra("BOOK_NAME",dBookName);
                            intent.putExtra("BOOK_IMAGE_URL",dBookImageUrl);*/
                            startActivity(intent);
                        }



                        bottomSheetDialog.dismiss();;
                    }
                });

                bottomSheetDialog.setContentView(bottomsheetView);
                bottomSheetDialog.show();
            }
        });
        check_review_writed();
        setup_review_recycler_view();

        mBookPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dCartCommand = "NO";
                if(dAddedToCart == false){
                    dAddedToCart = true;
                    dCartCommand = "Book Added to Cart";
                    mBookCartImage.setImageDrawable(getResources().getDrawable(ic_baseline_add_shopping_cart_24));
                }else{
                    dAddedToCart = false;
                    dCartCommand = "Book Removed From Cart";
                    mBookCartImage.setImageDrawable(getResources().getDrawable(ic_baseline_shopping_cart_24));
                }
                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(parentLayout, dCartCommand, Snackbar.LENGTH_SHORT)
                        .setAction("VIEW CART", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        })
                        .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                        .show();

            }
        });

        mBookWriteReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getBtnName = mBookWriteReviewBtn.getText().toString();
                if(getBtnName.equals("WRITE")){
                    mBookWriteReviewBtn.setText("SEND");
                    mBookWriteEditText.setVisibility(View.VISIBLE);
                    mBookWriteReviewRatingbar.setVisibility(View.VISIBLE);
                    mBookReadBtn.setVisibility(View.GONE);
                    mBookWriteEditText.setShowSoftInputOnFocus(true);
                    mBookWriteEditText.setFocusable(true);
                }else if(getBtnName.equals("SEND") || getBtnName.equals("TRY AGAIN")){
                    String dReview = mBookWriteEditText.getText().toString();
                    float dRate = mBookWriteReviewRatingbar.getRating();

                    if(dRate == 0 || dReview.equals("")){
                        Toast.makeText(BookInfo.this,"Fillup Rating and Text", Toast.LENGTH_SHORT).show();
                    }else{
                        sent_review_to_database(dReview, dRate);
                        mBookWriteEditText.setShowSoftInputOnFocus(false);
                        mBookWriteEditText.setFocusable(false);

                    };
                }else{
                    check_review_writed();
                }
            }
        });

    }

    private void intentCollectionFunc() {
        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {
            dCategoryUID = intent.getExtras().getString("Category_UID");
            dCategoryNAME = intent.getExtras().getString("Category_Name");
            dBookUID = intent.getExtras().getString("Book_UID");
            dSectorType = intent.getExtras().getString("SECTOR");

            dBookAuthorUID = intent.getExtras().getString("Book_AUTHOR_UID");
            dUserUID = intent.getExtras().getString("UserUID");
            if (TextUtils.isEmpty(dCategoryUID)) {
                dCategoryUID= "NO";
                Toast.makeText(BookInfo.this, "Category_UID NULL  " , Toast.LENGTH_SHORT).show();
            }if (dCategoryUID.equals("")){
                dCategoryUID= "NO";
                Toast.makeText(BookInfo.this, "BOOK UID 404" , Toast.LENGTH_SHORT).show();
            }if (TextUtils.isEmpty(dSectorType)) {
                dSectorType= "NO";
                Toast.makeText(BookInfo.this, "SectorType NULL  " , Toast.LENGTH_SHORT).show();
            }else if (dSectorType.equals("")){
                dSectorType= "NO";
                Toast.makeText(BookInfo.this, "dSectorType 404" , Toast.LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(dBookAuthorUID)) {
                dBookAuthorUID= "NO";
                Toast.makeText(BookInfo.this, "dBookAuthorUID NULL  " , Toast.LENGTH_SHORT).show();
            }else if (dBookAuthorUID.equals("")){
                dBookAuthorUID= "NO";
                Toast.makeText(BookInfo.this, "dBookAuthorUID 404" , Toast.LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(dUserUID)) {
                dUserUID= "NO";
                Toast.makeText(BookInfo.this, "dUserUID NULL  " , Toast.LENGTH_SHORT).show();
            }else if (dUserUID.equals("")){
                dUserUID= "NO";
                Toast.makeText(BookInfo.this, "dSectorType 404" , Toast.LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(dBookUID)) {
                dBookUID= "NO";
                Toast.makeText(BookInfo.this, "BOOK_UID NULL  " , Toast.LENGTH_SHORT).show();
            }if (dBookUID.equals("")){
                dBookUID= "NO";
                Toast.makeText(BookInfo.this, "Book UID 404" , Toast.LENGTH_SHORT).show();
            }else{
                /////////////////////////INITIALIZE
                dUserIsAuthor = checkingUserisAuthor();
                fetch_books_data(dCategoryUID, dBookUID);
            }
        }else{
            dCategoryUID = "NO";
            dCategoryNAME = "NO";
            dBookUID= "NO";
            dSectorType= "NO";
            Toast.makeText(BookInfo.this, "Intent Not Found" , Toast.LENGTH_SHORT).show();
        }



    }


    private void sent_review_to_database(String dReview, float dRate) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        if(FirebaseAuth.getInstance().getCurrentUser() != null && !dBookUID.equals("NO")){
            dUserUID = FirebaseAuth.getInstance().getUid();
            String dDate = String.valueOf(System.currentTimeMillis());

            Map<String, Object> review_data = new HashMap<>();
            review_data.put("user_uid", dUserUID);
            review_data.put("user_review",dReview ); //map is done
            review_data.put("user_rating", dRate); //map is done
            review_data.put("user_time", dDate);
            review_data.put("admin_reply", "NO");
            review_data.put("admin_id", "NO");


            // db.document("Notebook/My First Note")
            db.collection("All_Type").document("REVIEWS").collection(dSectorType).document(dCategoryUID).collection(dBookUID).document(dUserUID).set(review_data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            mBookWriteReviewBtn.setText("DONE");
                            mBookWriteReviewBtn.setVisibility(View.GONE);
                            mBookWriteEditText.setVisibility(View.GONE);
                            mBookWriteReviewRatingbar.setVisibility(View.GONE);
                            mBookReadBtn.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(BookInfo.this,"Failed to Connect Database", Toast.LENGTH_SHORT).show();
                    mBookReadBtn.setVisibility(View.VISIBLE);
                    mBookWriteReviewBtn.setText("TRY AGAIN");
                    progressDialog.dismiss();
                }
            });

        }else{
            //ERROR 404 if auth not found then go to login page
            dUserUID = "NO";
            progressDialog.dismiss();
            mBookWriteReviewBtn.setText("TRY AGAIN");
            mBookReadBtn.setVisibility(View.VISIBLE);
            Toast.makeText(BookInfo.this,"Please LOGIN",Toast.LENGTH_SHORT).show();
        }
    }
    private void check_review_writed() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null && !dBookUID.equals("NO")) {
            dUserUID = FirebaseAuth.getInstance().getUid();
            db.collection("All_Type").document("REVIEWS").collection(dSectorType).document(dCategoryUID).collection(dBookUID).document(dUserUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String dUserid = documentSnapshot.getString("user_uid");
                        if (dUserid.equals(dUserUID)) {
                            mBookWriteReviewBtn.setVisibility(View.GONE);
                            mBookWriteReviewRatingbar.setVisibility(View.GONE);
                        } else {
                            mBookWriteReviewBtn.setVisibility(View.VISIBLE);
                            mBookWriteReviewBtn.setText("WRITE");
                        }
                    } else {
                        mBookWriteReviewBtn.setVisibility(View.VISIBLE);
                        mBookWriteReviewBtn.setText("WRITE");
                    }
                }
            });
        }
    }
    private void setup_review_recycler_view() {

        if(dBookUID.equals("NO") || dSectorType.equals("NO") || dCategoryUID.equals("NO") )
            intentCollectionFunc();

        if(dBookUID.equals("NO") || dSectorType.equals("NO") || dCategoryUID.equals("NO") ){
            intentCollectionFunc();
        }else{
            //Toast.makeText(BookInfo.this,"ALL REVIEW START",Toast.LENGTH_SHORT).show();;
            book_all_review_ref = db.collection("All_Type").document("REVIEWS").collection(dSectorType).document(dCategoryUID).collection(dBookUID);
            Query query = book_all_review_ref.orderBy("user_time", Query.Direction.ASCENDING);
            FirestoreRecyclerOptions<BookInfoReviewModel> options = new FirestoreRecyclerOptions.Builder<BookInfoReviewModel>()
                    .setQuery(query,BookInfoReviewModel.class)
                    .build();

            book_review_adapter = new BookInfoReviewAdapter(options, dUserIsAuthor);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(book_review_adapter);

            book_review_adapter.replyBtnClickListener(new BookInfoReviewAdapter.ClickListenerPackage(){
                @Override
                public void onItemClick(DocumentSnapshot documentSnapshot, int postion, String dReplyText) {
                    if(documentSnapshot.exists()){
                        if(dReplyText.equals("")){
                            Toast.makeText(BookInfo.this,"Please fillup reply box",Toast.LENGTH_SHORT).show();
                        }else if(dUserUID.equals("NO")){
                            Toast.makeText(BookInfo.this,"USER NOT LOGIN",Toast.LENGTH_SHORT).show();
                        }else{
                            String dReviewUID = documentSnapshot.getId();
                            db.collection("All_Type").document("REVIEWS").collection(dSectorType).document(dCategoryUID).collection(dBookUID).document(dReviewUID).update("admin_id", dUserUID).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(BookInfo.this,"Failed to sent Reply",Toast.LENGTH_SHORT).show();
                                }
                            });
                            db.collection("All_Type").document("REVIEWS").collection(dSectorType).document(dCategoryUID).collection(dBookUID).document(dReviewUID).update("admin_reply", dReplyText);
                        }
                    }else{
                        Toast.makeText(BookInfo.this,"Review Server not found",Toast.LENGTH_SHORT).show();
                    }

                 }
            });
        }


    }

    private void AddPdfLinkFun() {
        if(checkingUserisAuthor()){
            Toast.makeText(BookInfo.this,"Select PDF Files",Toast.LENGTH_SHORT).show();;
            Intent intent = new Intent();
            intent.setType("application/pdf");
            //intent.putExtra(Intent.w, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
        }else{
            Toast.makeText(BookInfo.this,"You are not The Author",Toast.LENGTH_SHORT).show();;
        }
    }
    @Override   //pdf upload
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE1 && resultCode == RESULT_OK){
            if(data.getData() != null){
                //selected single file
                Uri FileUri = data.getData();
                final String dFileNmae = getFileName(FileUri);
                final String dDate = String.valueOf(System.currentTimeMillis());
                Toast.makeText(BookInfo.this,"UPLOADING",Toast.LENGTH_SHORT).show();;
                //ERORR 404                     OFFICIAL BOOKS leka ta modify hobe Sector name diye
                FileToUpload = mStorage.child("PorioApp/Bangla/eBooks/"+dSectorType+"/"+dCategoryUID+"/"+dBookUID).child(dFileNmae+dDate);
                FileToUpload.putFile(FileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(BookInfo.this, dFileNmae+" Uploaded File" , Toast.LENGTH_SHORT).show();
                        FileToUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String dFileUrl = uri.toString();
                                db.collection("All_Type").document("BOOKS").collection(dCategoryUID).document(dBookUID).update("pdf",dFileUrl);
                                dPdfLink = dFileUrl;
                                mReadMoreBtn.setText("Read More");
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BookInfo.this, dFileNmae+"Failed to Uplaod !" , Toast.LENGTH_SHORT).show();
                    }
                });

            }else{
                Toast.makeText(BookInfo.this, "File Not Selected" , Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileName(Uri uri) {
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

    private void fetch_books_data(String CategoryUID, String BookUID) {
        single_book_db_ref = db.collection("All_Type").document("BOOKS").collection(CategoryUID).document(BookUID);

        single_book_db_ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                BookInfoModel bookInfoModel = documentSnapshot.toObject(BookInfoModel.class);
                String dBookName = bookInfoModel.getName();
                String dBookAuthorName = bookInfoModel.getAuthor();
                String dBookCategory ;
                dSummary = bookInfoModel.getSummary();
                String dBookPhotoURL = bookInfoModel.getPhotoURL();
                String dBookTotalData = bookInfoModel.getTotal_data();
                dPdfLink = bookInfoModel.getPdf();

                Picasso.get().load(dBookPhotoURL).into(mBookImage);
                mBookName.setText(dBookName);
                mBookCategoryName.setText(dCategoryNAME);
                methodGetBookTotalData(dBookTotalData);
                methodAuthorNameSetup(dBookAuthorName);
            }
        });
    }

    private void methodAuthorNameSetup(String dBookAuthorName) {
        int len = dBookAuthorName.length();
        boolean author_uid = true;
        for(int i =0; i<len; i++){
            if(dBookAuthorName.charAt(i) == ' '){
                author_uid = false;
                break;
            }
        }
        if(author_uid == false){
            mBookAuthorName.setText(dBookAuthorName);
        }else{
            db.collection("USER_DATA").document("REGISTER").collection("NORMAL_USER").document(dBookAuthorName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String AuthorName = documentSnapshot.getString("name");
                    mBookAuthorName.setText(AuthorName);
                }
            });
        }
    }

    private void methodGetBookTotalData(String dTotal) {
        int len = dTotal.length();
        Vector<String> vec = new Vector<String>();

        String target = "TERPLPDVST";
        String new_word = "";
        int i = 0;
        int j = 0;
        for (i = 0; i < len; i++) {
            if (dTotal.charAt(i) == target.charAt(j)) {
                vec.add(new_word);
                new_word = "";
                j++;
            } else {
                new_word += dTotal.charAt(i);
            }
        }
        if(vec.size() == 10) {
            String dsBookType = vec.elementAt(0); //ACTIVE OR BLOCKED
            String dsBookEditNo = vec.elementAt(1); //How Much Time Book ediited
            String dsBookRating = vec.elementAt(2); //Book Total Rating
            String dsBookPriority = vec.elementAt(3); //Book Priority
            String dsBookLevel = vec.elementAt(4); //Book Level
            String dsBookPrice = vec.elementAt(5); //BOokPrice;
            String dsBookDiscount = vec.elementAt(6); //Book Discount;
            String dsBookViews = vec.elementAt(7); //Book Total Viewed;
            String dsBookInStock = vec.elementAt(8); //Book in Stock;
            String dsBookPublishTime = vec.elementAt(9); //Book Publish Time;

            dfBookRate = Float.parseFloat(dsBookRating);
            if(dsBookRating.equals("0")){
                Random konik = new Random();
                dfBookRate = konik.nextInt(2)+3;
                mBookRatingLevel.setRating(dfBookRate);
            }else{
                mBookRatingLevel.setRating(dfBookRate);
            }
            mBookPrice.setText(""+dsBookPrice+"TK");
            mBookView.setText(dsBookViews+" views");

            if(isNumericString(dsBookPublishTime)){
                Long currentTime = Long.parseLong(dsBookPublishTime);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mma (d MMMM yyyy)");
                //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM yyyy");
                Date date = new Date(currentTime);
                String time = simpleDateFormat.format(date);
                mBookPublishDate.setText(""+time);
            }else
                mBookPublishDate.setText(" ");
        }else{
            Toast.makeText(BookInfo.this, "vector size crossed = "+vec.size(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNumericString(String dsBookPublishTime) {
        if (dsBookPublishTime.matches("[0-9]+") && dsBookPublishTime.length() > 0) {
            return true;
        }else{
             return false;   //integer not found
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        dUserIsAuthor = checkingUserisAuthor();
        book_review_adapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
   public boolean checkingUserisAuthor(){   //checking user is author or not
        //if(dUserIsAuthor == false){
                if(dUserUID.equals("NO")){
                    dUserIsAuthor = false;
                }else if(dBookAuthorUID.equals("NO")){
                    dUserIsAuthor = false;
                }else if(dBookAuthorUID.equals(dUserUID)){
                    dUserIsAuthor = true;
                }else{
                    dUserIsAuthor = false;
                }
            /*db.collection("All_Type").document("BOOKS").collection(dCategoryUID).document(dBookUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        dBookAuthorUID = documentSnapshot.getString("author");
                        if(dBookAuthorUID.equals(dUserUID)){
                            dUserIsAuthor = true;
                        }else{
                            dUserIsAuthor = false;
                        }
                    }else{
                        dUserIsAuthor = false;
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dUserIsAuthor = false;
                }
            });*/
        //}
        //Toast.makeText(BookInfo.this,"AUTHOR IS "+dUserIsAuthor,Toast.LENGTH_SHORT).show();;
        return dUserIsAuthor;
    }


}