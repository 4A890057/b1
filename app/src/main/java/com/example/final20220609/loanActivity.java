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
import com.example.final20220609.databinding.ActivityLoanBinding;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class loanActivity extends AppCompatActivity {

    private ActivityLoanBinding binding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private StorageReference storageReference;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan);

        storageReference = FirebaseStorage.getInstance().getReference();

        binding = ActivityLoanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadUser();

        loadActivity();

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String click = adapterView.getItemAtPosition(i).toString();
                Log.d("Demo",click);

                db.collection("state")
                        .document(click)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if(task.isSuccessful()){

                                    DocumentSnapshot doc = task.getResult();

                                    new AlertDialog.Builder(loanActivity.this)
                                            .setTitle(click)
                                            .setMessage("借用者："+doc.getString("account")+"\n"+"申請時間："+doc.getString("date")+"\n"+"申請狀況："+doc.getString("situation"))
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
        });

        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                binding.drawerLayout.closeDrawer(GravityCompat.START);

                int id = item.getItemId();

                if(id==R.id.itemUser) {
                    Intent it = new Intent(loanActivity.this,modifyActivity.class);
                    startActivity(it);
                }
                else if(id==R.id.itemMap){
                    Intent it = new Intent(loanActivity.this,MapsActivity.class);
                    startActivity(it);
                    finish();
                }
                else if(id==R.id.itemLoan){

                }
                else if(id==R.id.itemLogout){
                    mAuth.signOut();
                    Intent it = new Intent(loanActivity.this,loginActivity.class);
                    startActivity(it);
                    finish();
                }

                return false;
            }
        });

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

                            storageReference.child("profileImg").child(doc.getString("imgUri")).getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            navImgView.setImageURI(uri);
                                            Glide.with(loanActivity.this)
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

                            navUserName.setText(doc.getString("name"));

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

    void loadActivity(){

        String account = mAuth.getCurrentUser().getEmail();

        db.collection("state")
                .whereEqualTo("account",account)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful()){

                            ArrayAdapter adapter = new ArrayAdapter(loanActivity.this, android.R.layout.simple_list_item_1);

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