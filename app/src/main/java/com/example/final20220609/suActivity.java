package com.example.final20220609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.final20220609.databinding.ActivityLoginBinding;
import com.example.final20220609.databinding.ActivityMapsBinding;
import com.example.final20220609.databinding.ActivitySuBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class suActivity extends AppCompatActivity {

    private ActivitySuBinding binding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private StorageReference storageReference;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String page = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_su);

        storageReference = FirebaseStorage.getInstance().getReference();

        binding = ActivitySuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadUser();
        page = "Apply";
        loadActivity(page);

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String click = adapterView.getItemAtPosition(i).toString();
                Log.d("Demo",click);

                if(page=="Apply"){
                    db.collection("state")
                            .document(click)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if(task.isSuccessful()){

                                        DocumentSnapshot doc = task.getResult();

                                        new AlertDialog.Builder(suActivity.this)
                                                .setTitle(click)
                                                .setMessage("借用者："+doc.getString("account")+"\n"+"申請時間："+doc.getString("date"))
                                                .setPositiveButton("允許", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        db.collection("state")
                                                                .document(click)
                                                                .update("situation","已申請通過")
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {

                                                                        db.collection("loan")
                                                                                .document(click)
                                                                                .update("situation",true)
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {
                                                                                        Toast.makeText(suActivity.this,"已接受申請",Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                })
                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        Log.d("Demo","接受申請失敗");
                                                                                    }
                                                                                });

                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(suActivity.this,"接受申請失敗",Toast.LENGTH_SHORT).show();
                                                                        Log.d("Demo",e.getMessage());
                                                                    }
                                                                });
                                                        loadActivity("Apply");
                                                    }
                                                })
                                                .setNegativeButton("拒絕", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        db.collection("state")
                                                                .document(click)
                                                                .update("situation","拒絕要求")
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Toast.makeText(suActivity.this,"已拒絕要求",Toast.LENGTH_SHORT).show();
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(suActivity.this,"拒絕要求失敗",Toast.LENGTH_SHORT).show();
                                                                        Log.d("Demo",e.getMessage());
                                                                    }
                                                                });
                                                        loadActivity("Apply");
                                                    }
                                                })
                                                .show();

                                    }

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Demo",e.getMessage());
                                }
                            });
                }
                else if (page=="Return"){
                    db.collection("state")
                            .document(click)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if(task.isSuccessful()){

                                        DocumentSnapshot doc = task.getResult();

                                        new AlertDialog.Builder(suActivity.this)
                                                .setTitle(click)
                                                .setMessage("借用者："+doc.getString("account")+"\n"+"申請時間："+doc.getString("date")+"\n"+"歸還狀況："+doc.getString("situation"))
                                                .setPositiveButton("已歸還", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        db.collection("state")
                                                                .document(click)
                                                                .update("situation","已歸還")
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Toast.makeText(suActivity.this,"已完成歸還",Toast.LENGTH_SHORT).show();
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.d("Demo",e.getMessage());
                                                                    }
                                                                });

                                                        db.collection("loan")
                                                                .document(click)
                                                                .update("situation",false)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {

                                                                        Log.d("Demo","已開啟借用");

                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.d("Demo",e.getMessage());
                                                                    }
                                                                });

                                                        loadActivity("Return");
                                                    }
                                                })
                                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                    }
                                                })
                                                .show();

                                    }

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Demo",e.getMessage());
                                }
                            });
                }

            }

        });


        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                binding.drawerLayout.closeDrawer(GravityCompat.START);

                int id = item.getItemId();

                if(id==R.id.itemUser) {
                    Intent it = new Intent(suActivity.this,modifyActivity.class);
                    startActivity(it);
                }else if(id==R.id.itemApply) {
                    page = "Apply";
                    binding.tvType.setText("申請列表");
                    loadActivity(page);
                }else if(id==R.id.itemReturn) {
                    page = "Return";
                    binding.tvType.setText("歸還列表");
                    loadActivity(page);
                } else if(id==R.id.itemLogout){
                    mAuth.signOut();
                    Intent it = new Intent(suActivity.this,loginActivity.class);
                    startActivity(it);
                    finish();
                }

                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUser();
    }

    void loadActivity(String page){

        if(page=="Apply"){
            db.collection("state")
                    .whereEqualTo("situation","申請中")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if(task.isSuccessful()){

                                ArrayAdapter adapter = new ArrayAdapter(suActivity.this, android.R.layout.simple_list_item_1);

                                for(QueryDocumentSnapshot doc: task.getResult()){

                                    String place = doc.getString("place");
                                    Log.d("Demo",place);

                                    adapter.add(place);

                                }

                                binding.listView.setAdapter(adapter);
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Demo",e.getMessage());
                        }
                    });

        }else if(page=="Return"){

            db.collection("state")
                    .whereEqualTo("situation","已申請通過")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if(task.isSuccessful()){

                                ArrayAdapter adapter = new ArrayAdapter(suActivity.this, android.R.layout.simple_list_item_1);

                                for(QueryDocumentSnapshot doc: task.getResult()){

                                    String place = doc.getString("place");
                                    Log.d("Demo",place);

                                    adapter.add(place);

                                }

                                binding.listView.setAdapter(adapter);
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Demo",e.getMessage());
                        }
                    });
        }

    }

    void loadUser(){
        String email = mAuth.getCurrentUser().getEmail();

        View headerView = binding.navView.getHeaderView(0);
        TextView navUserName = (TextView) headerView.findViewById(R.id.txtName);
        CircleImageView navImgView = (CircleImageView) headerView.findViewById(R.id.profile_image);

        Log.d("Demo","Email："+email);

        db.collection("UserData")
                .document(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            DocumentSnapshot doc = task.getResult();

                            Log.d("Demo",doc.getString("name"));

                            if(doc.getString("imgUri")!=null){
                                storageReference.child("profileImg").child(doc.getString("imgUri")).getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                navImgView.setImageURI(uri);
                                                Glide.with(suActivity.this)
                                                        .load(uri)
                                                        .into(navImgView);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("Demo",e.getMessage());
                                            }
                                        });
                            }

                            navUserName.setText("管理者_"+doc.getString("name"));

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Demo",e.getMessage());
                    }
                });


    }



}