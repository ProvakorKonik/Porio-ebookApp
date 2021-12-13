package com.konik.porio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class Fragment_One  extends Fragment {

    private TextView mTestText;
    private ImageView mImageViewOne, mImageViewTwo, mImageViewThree, mImageViewFour;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_one, container, false);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // do your variables initialisations here except Views!!!
        //AUTH CHECK
       /* mAuth = FirebaseAuth.getInstance();
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
        };*/

    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTestText = (TextView) view.findViewById(R.id.text_one);
        mImageViewOne = (ImageView)view.findViewById(R.id.imageView1) ;
        mImageViewTwo = (ImageView)view.findViewById(R.id.imageView2) ;
        mImageViewThree = (ImageView)view.findViewById(R.id.imageView3) ;
        mImageViewFour = (ImageView)view.findViewById(R.id.imageView4) ;
        mTestText.setText(" ");



        mImageViewOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Category.class);
                intent.putExtra("SECTOR","ACADEMIC");
                startActivity(intent);
            }
        });

        mImageViewTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Category.class);
                intent.putExtra("SECTOR","PUBLISH"); //OFFICIAL
                startActivity(intent);
            }
        });
        mImageViewThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Category.class);
                intent.putExtra("SECTOR","USER");
                startActivity(intent);
            }
        });
        mImageViewFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UserList.class);
                //intent.putExtra("SECTOR","USER");
                startActivity(intent);
            }
        });


    }
}