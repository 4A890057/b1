package com.example.final20220609;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.final20220609.databinding.ActivityModifyBinding;
import com.example.final20220609.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class modifyActivity extends AppCompatActivity {

    private ActivityModifyBinding binding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private String email = mAuth.getCurrentUser().getEmail().toString();
    private Uri selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        storageReference = FirebaseStorage.getInstance().getReference();

        binding = ActivityModifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db.collection("UserData")
                .document(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful()){

                            DocumentSnapshot doc = task.getResult();

                            if(doc.getString("imgUri")!=null){
                                storageReference.child("profileImg").child(doc.getString("imgUri")).getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                binding.imageView.setImageURI(uri);
                                                Glide.with(modifyActivity.this)
                                                        .load(uri)
                                                        .into(binding.imageView);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("Demo",e.getMessage());
                                            }
                                        });
                            }

                            binding.etName.setText(doc.getString("name"));
                            binding.etPhone.setText(doc.getString("phone"));

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Demo",e.getMessage());
                    }
                });

        binding.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                String[] photo = {"從相簿選擇","拍照"};

                new AlertDialog.Builder(modifyActivity.this)
                        .setTitle("請選擇照片")
                        .setItems(photo, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i){
                                    case 0:

                                        Intent it = new Intent();
                                        it.setType("image/*");
                                        it.setAction(Intent.ACTION_GET_CONTENT);
                                        startActivityForResult(Intent.createChooser(it,"選擇照片"),101);

                                        break;
                                    case 1:

                                        break;
                                };
                            }
                        }).show();

                return false;
            }
        });



        binding.btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = binding.etName.getText().toString();
                String phone = binding.etPhone.getText().toString();


                if(name!=null && phone!=null){

                    Map<String,Object> user = new HashMap<>();

                    user.put("name",name);
                    user.put("phone",phone);

                    if(selectedImageUri!=null){
                        storageReference.child("profileImg").child(selectedImageUri.getLastPathSegment())
                                .putFile(selectedImageUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Toast.makeText(modifyActivity.this,"上傳成功："+selectedImageUri.getLastPathSegment(),Toast.LENGTH_SHORT).show();


                                        db.collection("UserData")
                                                .document(email)
                                                .update("imgUri",selectedImageUri.getLastPathSegment())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Log.d("Demo","上傳資料庫成功");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d("Demo","上傳資料庫失敗");
                                                    }
                                                });

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(modifyActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }

                    db.collection("UserData")
                            .document(email)
                            .update(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Log.d("Demo","資料更新成功 Email："+email);

                                    Toast.makeText(modifyActivity.this,"資料更新成功",Toast.LENGTH_SHORT).show();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Log.d("Demo",e.getMessage());

                                }
                            });


                }else {
                    Toast.makeText(modifyActivity.this,"資料不可為空值",Toast.LENGTH_SHORT).show();
                }


            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == 101){
            selectedImageUri = data.getData();
            Glide.with(modifyActivity.this)
                    .load(selectedImageUri)
                    .into(binding.imageView);


//            if(null!=selectedImageUri)
//                binding.imageView.setImageURI(selectedImageUri);
        }

    }
}