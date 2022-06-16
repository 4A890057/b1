package com.example.final20220609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.final20220609.databinding.ActivityEmailBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class emailActivity extends AppCompatActivity {

    private ActivityEmailBinding binding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        binding = ActivityEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent it = getIntent();
        String email = it.getStringExtra("email");
        String pwd = it.getStringExtra("pwd");

        binding.btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.getCurrentUser().sendEmailVerification()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                Toast.makeText(emailActivity.this,"已重新發送Email認證信件",Toast.LENGTH_SHORT).show();
                                Log.d("Demo","已重新發送Email認證信件");

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Log.d("Demo",e.getMessage());

                            }
                        });

            }
        });

        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.signOut();

                mAuth.signInWithEmailAndPassword(email,pwd)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                String loginEmail = authResult.getUser().getEmail();
                                Log.d("Demo","登入成功"+loginEmail);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Log.d("Demo","登入失敗"+e.getMessage());

                            }
                        });

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if(mAuth.getCurrentUser().isEmailVerified()){
                            Toast.makeText(emailActivity.this,"已完成Email認證",Toast.LENGTH_SHORT).show();
                            Log.d("Demo","已完成認證");
                            finish();
                        }else {
                            Toast.makeText(emailActivity.this,"尚未完成Email認證",Toast.LENGTH_SHORT).show();
                            Log.d("Demo","尚未完成Email認證");
                        }

                    }
                }, 1000);

            }
        });



    }
}