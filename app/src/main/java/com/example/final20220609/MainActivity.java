package com.example.final20220609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.final20220609.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String email = mAuth.getCurrentUser().getEmail();

        Intent it = getIntent();
        String building = it.getStringExtra("building");
        binding.tvBuilding.setText(building);

        db.collection("loan")
                .whereEqualTo("type",building)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful()){

                            ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1);

                            for(QueryDocumentSnapshot doc: task.getResult()){

                                String title = doc.getString("title");
                                Log.d("Demo",title);

                                adapter.add(title);

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

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String click = adapterView.getItemAtPosition(i).toString();
                Log.d("Demo",click);

                db.collection("loan")
                        .document(click)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if(task.isSuccessful()){

                                    DocumentSnapshot doc = task.getResult();

                                    if(doc.getBoolean("situation")){

                                        new AlertDialog.Builder(MainActivity.this)
                                                .setTitle(click.toString())
                                                .setMessage("此教室或場地已借出")
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                    }
                                                })
                                                .show();

                                    }else {

                                        new AlertDialog.Builder(MainActivity.this)
                                                .setTitle(click.toString())
                                                .setMessage("此教室尚未借出，請問您要借嗎？")
                                                .setPositiveButton("我要借", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        String nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                                                        Map<String,Object> state = new HashMap<>();
                                                        state.put("account", email);
                                                        state.put("place" ,click);
                                                        state.put("date" ,nowDate);
                                                        state.put("situation","申請中");

                                                        db.collection("state")
                                                                .document(click)
                                                                .set(state)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Toast.makeText(MainActivity.this,"借用成功！ 場地："+click,Toast.LENGTH_SHORT).show();
                                                                        finish();
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(MainActivity.this,"借用失敗",Toast.LENGTH_SHORT).show();
                                                                        Log.d("Demo",e.getMessage());
                                                                    }
                                                                });

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



    }



}