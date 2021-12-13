package com.konik.porio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.transition.TransitionManager;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static com.google.firebase.crashlytics.internal.Logger.TAG;

public class Fragment_Four extends Fragment {

    //Layout
    private boolean isOpen = false ;
    private ConstraintSet layout1,layout2;
    private ConstraintLayout constraintLayout ;
    /////////////////////////////////// INITIALIZING
    private TextView mLoginHeadText;
    private Button mLoginBtn, mLogoutBtn, mCreatePostBtn;
    private ImageView mUserProfileCoverImg;
    private ImageView mUserProfileImageView;
    private TextView mCheckUserName, mCheckUserActivity, mCheckUTotalFollowers, mCheckUserTotalBooks, mCheckUserBio;
    private TextView mCheckUserBirthdate, mCheckUserPhoneNumber, mCheckUserRegistrationDate;
    private Button mCheckUserEditProfileBtn, mSentMsgToCheckUserBtn;
    private TextView mUserProfileFollwerText;
    private TextView mUserProfileBookText;
    private TextView mUserProfileAboutText;
    private LinearLayout mLinerLayoutBarOne;
    private LinearLayout mLinerLayoutBarTwo;
    private ProgressBar mUserProfileProgressBar;
    //Firebase AUth
    private FirebaseUser user;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    ///EMAIL
    private static final String TAGO = "LoginRegisterActivity";
    int AUTHUI_REQUEST_CODE = 10001;
    //FIREBASE AUTH FINISH
    /////////FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page

    //Firebase Checking User Data Saved or Not
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference user_data_ref = db.collection("USER_DATA").document("REGISTER");

    ///////////////////////////
    private String dTotal_books = "0";
    private String dTotal_Followers = "0";
    private String dTotal_Payment = "0";
    private String dTotal_MB = "0";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_four, container, false);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // do your variables initialisations here except Views!!!
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    mUserProfileProgressBar.setVisibility(View.VISIBLE);
                    checkVerfication();
                }else{
                    login_mode();
                    mUserProfileProgressBar.setVisibility(View.GONE);
                }
            }
        };



    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLoginHeadText = (TextView)view.findViewById(R.id.login_head_text);
        mLoginBtn = (Button)view.findViewById(R.id.login_btn);
        mLogoutBtn = (Button)view.findViewById(R.id.userprofile_logout_btn);
        mCreatePostBtn = (Button)view.findViewById(R.id.userprofile_create_post_btn);
        mCheckUserName = (TextView) view.findViewById(R.id.user_profile_name_text);
        mCheckUserActivity = (TextView)view.findViewById(R.id.user_profile_acitivity_time);
        mCheckUTotalFollowers = (TextView)view.findViewById(R.id.user_profile_followers_count);
        mCheckUserTotalBooks = (TextView)view.findViewById(R.id.user_profile_books_count);
        mCheckUserBio = (TextView)view.findViewById(R.id.user_profile_bio);
        mCheckUserBirthdate = (TextView)view.findViewById(R.id.user_profile_birthdate);
        mCheckUserPhoneNumber = (TextView)view.findViewById(R.id.user_profile_contact_no);
        mCheckUserRegistrationDate = (TextView)view.findViewById(R.id.user_profile_registration);
        mCheckUserEditProfileBtn = (Button)view.findViewById(R.id.user_profile_edit_btn);
        mSentMsgToCheckUserBtn = (Button)view.findViewById(R.id.user_profile_message);

        mUserProfileCoverImg = (ImageView)view.findViewById(R.id.user_profile_cover_img);
        mUserProfileImageView = (ImageView)view.findViewById(R.id.user_profile_propic_img);
        mUserProfileFollwerText = (TextView)view.findViewById(R.id.textView2);
        mUserProfileBookText = (TextView)view.findViewById(R.id.textView4);
        mUserProfileAboutText = (TextView)view.findViewById(R.id.user_profile_about);
        mLinerLayoutBarOne = (LinearLayout)view.findViewById(R.id.linearlayout_bar_one);
        mLinerLayoutBarTwo = (LinearLayout) view.findViewById(R.id.linearlayout_bar_two);
        mUserProfileProgressBar = (ProgressBar) view.findViewById(R.id.user_profile_progressBar);

        ///////Layout Changing Code
        //Layout Change Code Start
        //changing the status bar color to transparent
        //Window w = getWindow();
        //w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        layout1 = new ConstraintSet();
        layout2 = new ConstraintSet();

        constraintLayout = view.findViewById(R.id.constraint_layout);
        layout2.clone(getContext(), R.layout.zoom_pro_pic_layout);
        layout1.clone(constraintLayout);
        //LAYOUT CHANGE CODE FINISH
        //CircleImageView imageView = (CircleImageView) findViewById(R.id.image);
        mUserProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile_pic_zoom_function();
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserProfileProgressBar.setVisibility(View.VISIBLE);
                handleLoginRegister();
                
            }
        });
        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();

            }
        });
        mSentMsgToCheckUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(),"User Inforamtion 404", Toast.LENGTH_SHORT).show();;
                Intent intent = new Intent(getContext(),CategoryAddInfo.class);
                //intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
        mCreatePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dUserUID.equals("NO")){
                    Toast.makeText(getContext(),"Please Login to Add Books",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(getContext(), BookAdd.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void profile_pic_zoom_function() {
        if (!isOpen) {
            TransitionManager.beginDelayedTransition(constraintLayout);
            layout2.applyTo(constraintLayout);
            isOpen = !isOpen;
        } else {
            TransitionManager.beginDelayedTransition(constraintLayout);
            layout1.applyTo(constraintLayout);
            isOpen = !isOpen;
            logout_mode();
            mUserProfileProgressBar.setVisibility(GONE);
        }
    }

    //EMAIL LOGIN
    public void handleLoginRegister(){     //EMAIL

        List<AuthUI.IdpConfig> provider = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
        );

        //.setAlwaysShowSignInMethodScreen(true)
        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(provider)
                .setTosAndPrivacyPolicyUrls("https://google.com", "https://facebook.com")
                .build();

        startActivityForResult(intent,AUTHUI_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {   //EMAIL
        if(requestCode == AUTHUI_REQUEST_CODE){     //EMAIL
            if(resultCode == RESULT_OK){
                //We have signed in the user or new user
                user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(TAGO,"onActivityResult:" + user.getEmail());
                if(user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp()){
                    Toast.makeText(getContext(),"Welcome New User", Toast.LENGTH_SHORT).show();;
                    checkVerfication();
                }else{
                    Toast.makeText(getContext(),"Welcome back Again", Toast.LENGTH_SHORT).show();
                    checkVerfication();
                }
            }else{
                // Signing in Failed
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if(response == null){
                    Toast.makeText(getContext(),"user cancelled", Toast.LENGTH_SHORT).show();
                    Log.d(TAGO, "onActivityResult: the user has cancelled the sign in request");
                }else{
                    Toast.makeText(getContext(),"ERROR "+response.getError(), Toast.LENGTH_SHORT).show();
                    Log.e(TAGO,"onActivityResult: ",response.getError());
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void checkVerfication() {
        if(user.isEmailVerified()){
            //EMAIL IS VERIFIED
            checkUserData();
            Toast.makeText(getContext(),"Verified Email", Toast.LENGTH_SHORT).show();;
        }else{  //EMAIL IS NOT VERIFIED
            //verfication_email();
            checkUserData();
            Toast.makeText(getContext(),"Passing unverified Email", Toast.LENGTH_SHORT).show();;
        }
    }
    private String dUserUID = "NO";
    private void checkUserData() {
        dUserUID = FirebaseAuth.getInstance().getUid();
        if(dUserUID.equals("")){
            Toast.makeText(getContext(),"Logged in but UID 404", Toast.LENGTH_SHORT).show();;
        }else{
            //Please Modify Database Auth READ WRITE Condition if its not connect to database
            Toast.makeText(getContext(), "Checking Database", Toast.LENGTH_SHORT).show();;
            user_data_ref.collection("NORMAL_USER").document(dUserUID).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        String UserUID = documentSnapshot.getString("uid");
                        String dUserName = documentSnapshot.getString("name");
                        String dUserBio = documentSnapshot.getString("bio");
                        String dUserPhoneNo = documentSnapshot.getString("phone_no");
                        String dTotal = documentSnapshot.getString("total");
                        String dUserPhotoURL = documentSnapshot.getString("photoURL");
                        String dBirthRegTime = documentSnapshot.getString("birth_reg");
                        String dLastActivity = documentSnapshot.getString("lastActivity");
                        if(UserUID.equals(dUserUID)){
                            mUserProfileProgressBar.setVisibility(View.GONE);
                            logout_mode();
                            mCheckUserName.setText(dUserName);
                            mCheckUserBio.setText(dUserBio);
                            mCheckUserPhoneNumber.setText("Phone no : "+dUserPhoneNo);
                            Picasso.get().load(dUserPhotoURL).into(mUserProfileImageView);
                            methodGetUserTotalData(dTotal);
                            methodGetBirthRegDate(dBirthRegTime);
                            savedUserDataPhoneMemory(dUserName, UserUID, dTotal, dUserPhotoURL);
                        }else{
                            Toast.makeText(getContext(),"User Inforamtion Not Matched", Toast.LENGTH_SHORT).show();;
                        }

                    }else{
                        //User has no data saved
                        //Toast.makeText(getContext(),"User Inforamtion 404", Toast.LENGTH_SHORT).show();;
                        Intent intent = new Intent(getContext(),UserInfoAdd.class);
                        //intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
            });
        }
    }
    //SAVING DATA TO PHONE MEMORY
    private void savedUserDataPhoneMemory(String dUserName, String userUID, String dTotal, String dUserPhotoURL) {
        int dAdminLevel = dTotal.charAt(0)-48;
        SharedPreferences.Editor editor = this.getActivity().getSharedPreferences("PorioApp", MODE_PRIVATE).edit();
        editor.putString("user_name", dUserName);
        editor.putString("user_uid", userUID);
        editor.putString("total_code", dTotal);
        editor.putString("photo_url", dUserPhotoURL);
        editor.putInt("admin_level", dAdminLevel);
        editor.apply();
    }

    private static final String sZero = "0";
    final private int iZero = 0;
    String     sAdminLevel = sZero,   sGenderType =  sZero,      sFollower =   sZero,          sTotalPost =   sZero,        sTotalDiscussion=   sZero, sTotalTaka =   sZero;
    String sTotalMegaByte  = sZero,   sTotalFileView =   sZero,  sTotalFileDownload =   sZero, sTotalFileUpload =   sZero,  sTotalReview = sZero;
    String sTotalLike = sZero,        sTotalComment = sZero,     sExtraE =   sZero,            sExtraF =   sZero;
    int iAdminLevel = iZero, iGenderType =  iZero, iFollower =   iZero, iTotalPost =   iZero, iTotalDiscussion=   iZero, iTotalTaka =   iZero, iTotalMegaByte  = iZero;
    int iTotalFileView =   iZero,  iTotalFileDownload =   iZero, iTotalFileUpload =   iZero, iTotalReview = iZero, iTotalLike = iZero, iTotalComment = iZero, iExtraE =   iZero, iExtraF =  iZero;

    private void methodGetBirthRegDate(String dBrithRegDate){
        int len = dBrithRegDate.length();
        Vector<String>vec = new Vector<>();
        String target = "BR";
        String new_word = "";
        int i = 0, j = 0;
        for(i = 0; i<len; i++){
            if(dBrithRegDate.charAt(i) == target.charAt(j)){
                vec.add(new_word);
                new_word = "";
                j++;
            }else{
                new_word += dBrithRegDate.charAt(i);
            }
        }
        String dBirthDateMiniSeconds = vec.elementAt(0);
        String dRegistrationMiniSeconds = vec.elementAt(1);

        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM");
        SimpleDateFormat dateFormatWithYear = new SimpleDateFormat("d/MMM/yyyy");
        Long birthTime = Long.parseLong(dBirthDateMiniSeconds);
        Long regTime = Long.parseLong(dRegistrationMiniSeconds);
        Date date1 = new Date(birthTime);
        Date date2 = new Date(regTime);
        String dBirthTime = dateFormat.format(date1);
        String dRegTime = dateFormatWithYear.format(date2);

        mCheckUserRegistrationDate.setText("Registerted on : "+ dRegTime);
        if(iGenderType == 1){
            mCheckUserBirthdate.setText("Wish Him : "+dBirthTime);  //Gender MALE
        }else if(iGenderType == 2){
            mCheckUserBirthdate.setText("Wish Her : "+dBirthTime);
        }else{
            mCheckUserBirthdate.setText("Wish on : "+dBirthTime);
        }

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
            mCheckUserTotalBooks.setText(sTotalPost);
            mCheckUTotalFollowers.setText(sFollower);
        }else{
            Toast.makeText(getContext(),"Failed to fetch TotalData", Toast.LENGTH_SHORT).show();;
        }

        if(iAdminLevel >= 5){
            mSentMsgToCheckUserBtn.setVisibility(View.VISIBLE);
        }else{
            mSentMsgToCheckUserBtn.setVisibility(GONE);
        }
    }

    boolean verification_email_sent = false;
    private void verfication_email() {
        user.sendEmailVerification()
                .addOnCompleteListener(getActivity(), new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        /* Check Success */
                        if (task.isSuccessful()) {
                            mUserProfileProgressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Verification Email Sent To: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                            mLoginHeadText.setText("Please  verify your email\n("+ user.getEmail()+")\nand Login again");
                            mLoginHeadText.setTextSize(16);
                            mLoginBtn.setVisibility(GONE);
                            verification_email_sent = true;
                            mAuth.signOut();
                            login_mode();
                        } else {
                            if(verification_email_sent == false){
                                mUserProfileProgressBar.setVisibility(View.GONE);
                                mLoginHeadText.setText("Verify your Email");
                                mAuth.signOut();
                                login_mode();
                                Log.e(TAG, "sendEmailVerification", task.getException());
                                //Toast.makeText(getApplicationContext(), "Failed. Try After Few Minutes", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void logout_mode() {
        mLoginHeadText.setVisibility(View.GONE);
        mLoginBtn.setVisibility(View.GONE);


        mLogoutBtn.setVisibility(View.VISIBLE);
        mCreatePostBtn.setVisibility(View.VISIBLE);
        mCheckUserName.setVisibility(View.VISIBLE);
        mCheckUserActivity.setVisibility(View.VISIBLE);
        mCheckUTotalFollowers.setVisibility(View.VISIBLE);
        mCheckUserTotalBooks.setVisibility(View.VISIBLE);
        mCheckUserBio.setVisibility(View.VISIBLE);
        mCheckUserBirthdate.setVisibility(View.VISIBLE);
        mCheckUserPhoneNumber.setVisibility(View.VISIBLE);
        mCheckUserRegistrationDate.setVisibility(View.VISIBLE);
        mCheckUserEditProfileBtn.setVisibility(View.VISIBLE);
        mSentMsgToCheckUserBtn.setVisibility(View.VISIBLE);
        mUserProfileImageView.setVisibility(View.VISIBLE);
        mUserProfileCoverImg.setVisibility(View.VISIBLE);
        mUserProfileFollwerText.setVisibility(View.VISIBLE);
        mUserProfileBookText.setVisibility(View.VISIBLE);
        mUserProfileAboutText.setVisibility(View.VISIBLE);
        mLinerLayoutBarOne.setVisibility(View.VISIBLE);
        mLinerLayoutBarTwo.setVisibility(View.VISIBLE);
    }

    private void login_mode() {
        mLoginHeadText.setVisibility(View.VISIBLE);
        mLoginBtn.setVisibility(View.VISIBLE);


        mLogoutBtn.setVisibility(View.GONE);
        mCreatePostBtn.setVisibility(View.GONE);
        mCheckUserName.setVisibility(View.GONE);
        mCheckUserActivity.setVisibility(View.GONE);
        mCheckUTotalFollowers.setVisibility(View.GONE);
        mCheckUserTotalBooks.setVisibility(View.GONE);
        mCheckUserBio.setVisibility(View.GONE);
        mCheckUserBirthdate.setVisibility(View.GONE);
        mCheckUserPhoneNumber.setVisibility(View.GONE);
        mCheckUserRegistrationDate.setVisibility(View.GONE);
        mCheckUserEditProfileBtn.setVisibility(View.GONE);
        mSentMsgToCheckUserBtn.setVisibility(View.GONE);
        mUserProfileImageView.setVisibility(View.GONE);
        mUserProfileCoverImg.setVisibility(View.GONE);
        mUserProfileFollwerText.setVisibility(View.GONE);
        mUserProfileBookText.setVisibility(View.GONE);
        mUserProfileAboutText.setVisibility(View.GONE);
        mLinerLayoutBarOne.setVisibility(View.GONE);
        mLinerLayoutBarTwo.setVisibility(View.GONE);
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //mFirebaseAuth.addAuthStateListener(authStateListener);
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}