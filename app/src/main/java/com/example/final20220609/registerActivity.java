package com.example.final20220609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.final20220609.databinding.ActivityLoginBinding;
import com.example.final20220609.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class registerActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = binding.etEmail.getText().toString();
                String pwd = binding.etPwd.getText().toString();
                String name = binding.etName.getText().toString();
                String phone = binding.etPhone.getText().toString();

                if(email!=null && pwd!=null && name!=null && phone!=null){
                    Map<String,Object> user = new HashMap<>();

                    user.put("email",email);
                    user.put("name",name);
                    user.put("phone",phone);
                    user.put("pwd",pwd);

                    db.collection("UserData")
                            .document(email)
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("Demo","已輸入user email："+email);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Demo","輸入失敗"+e.getMessage());
                                }
                            });


                    mAuth.createUserWithEmailAndPassword(email,pwd)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    String email = authResult.getUser().getEmail();
                                    Log.d("Demo",email+"建立成功");

                                }
                            });

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAuth.getCurrentUser().sendEmailVerification()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            Toast.makeText(registerActivity.this,"已送出Email認證信件",Toast.LENGTH_SHORT).show();
                                            Log.d("Demo","已送出Email認證信件");



                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Log.d("Demo",e.getMessage());

                                        }
                                    });

                            Intent it = new Intent(registerActivity.this,emailActivity.class);
                            it.putExtra("email",email);
                            it.putExtra("pwd",pwd);
                            startActivity(it);

                            finish();
                        }
                    }, 2000);

                }else{
                    Toast.makeText(registerActivity.this,"資料不可為空值",Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}