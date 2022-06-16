package com.example.final20220609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.final20220609.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class loginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = binding.etEmail.getText().toString();
                String pwd = binding.etPwd.getText().toString();

                if(email!=null && pwd!=null){

                    mAuth.signInWithEmailAndPassword(email,pwd)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {

                                    String loginEmail = authResult.getUser().getEmail();
                                    Toast.makeText(loginActivity.this,"登入成功"+loginEmail,Toast.LENGTH_SHORT).show();
                                    Log.d("Demo","登入成功"+loginEmail);
                                    checkUser();
                                    finish();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(loginActivity.this,"登入失敗"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                    Log.d("Demo","登入失敗"+e.getMessage());

                                }
                            });

                }else {
                    Toast.makeText(loginActivity.this,"帳號或密碼不可為空值",Toast.LENGTH_SHORT).show();
                }

            }

        });


        binding.tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent it = new Intent(loginActivity.this,registerActivity.class);
                startActivity(it);

            }
        });

    }




    private void checkUser() {

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String email = mAuth.getCurrentUser().getEmail();
            db.collection("UserData")
                    .document(email)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {

                                DocumentSnapshot doc = task.getResult();
                                Log.d("Demo",doc.getString("email")+"   "+doc.getString("type"));

                                if (doc.getString("type") != null) {

                                    Log.d("Demo","管理者Email"+email);

                                    Intent it = new Intent(loginActivity.this, suActivity.class);
                                    startActivity(it);

                                }else{

                                    Log.d("Demo","使用者 Email："+email);

                                    Intent it = new Intent(loginActivity.this,MapsActivity.class);
                                    startActivity(it);

                                }

                            }else {
                                Log.d("Demo","失敗");
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Demo", e.getMessage());
                        }
                    });

        } else {

        }
    }

}