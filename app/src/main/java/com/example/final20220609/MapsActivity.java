package com.example.final20220609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.final20220609.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storageReference = FirebaseStorage.getInstance().getReference();

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadUser();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        db.collection("building")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful()){
                            LatLng lastLating = null;

                            for(QueryDocumentSnapshot doc: task.getResult()){
                                String title = doc.getString("title");
                                GeoPoint gps = doc.getGeoPoint("gps");

                                Log.d("Demo","title："+title+"gps："+gps);

                                LatLng latLng = new LatLng(gps.getLatitude(),gps.getLongitude());

                                mMap.addMarker(new MarkerOptions().position(latLng).title(title));

                                lastLating = latLng;
                            }

                            CameraPosition cameraPosition = new CameraPosition.Builder().target(lastLating).zoom(17).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d("Demo",e.getMessage());

                    }
                });

        binding.btnClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mMap.clear();

                db.collection("building")
                        .whereEqualTo("category","教室")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if(task.isSuccessful()){
                                    LatLng lastLating = null;

                                    for(QueryDocumentSnapshot doc: task.getResult()){
                                        String title = doc.getString("title");
                                        GeoPoint gps = doc.getGeoPoint("gps");

                                        Log.d("Demo","title："+title+"gps："+gps);

                                        LatLng latLng = new LatLng(gps.getLatitude(),gps.getLongitude());

                                        mMap.addMarker(new MarkerOptions().position(latLng).title(title));

                                        lastLating = latLng;
                                    }

                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(lastLating).zoom(17).build();
                                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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

        binding.btnSport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mMap.clear();

                db.collection("building")
                        .whereEqualTo("category","運動")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if(task.isSuccessful()){
                                    LatLng lastLating = null;

                                    for(QueryDocumentSnapshot doc: task.getResult()){
                                        String title = doc.getString("title");
                                        GeoPoint gps = doc.getGeoPoint("gps");

                                        Log.d("Demo","title："+title+"gps："+gps);

                                        LatLng latLng = new LatLng(gps.getLatitude(),gps.getLongitude());

                                        mMap.addMarker(new MarkerOptions().position(latLng).title(title));

                                        lastLating = latLng;
                                    }

                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(lastLating).zoom(17).build();
                                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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

        binding.btnActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mMap.clear();

                db.collection("building")
                        .whereEqualTo("category","活動")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if(task.isSuccessful()){
                                    LatLng lastLating = null;

                                    for(QueryDocumentSnapshot doc: task.getResult()){
                                        String title = doc.getString("title");
                                        GeoPoint gps = doc.getGeoPoint("gps");

                                        Log.d("Demo","title："+title+"gps："+gps);

                                        LatLng latLng = new LatLng(gps.getLatitude(),gps.getLongitude());

                                        mMap.addMarker(new MarkerOptions().position(latLng).title(title));

                                        lastLating = latLng;
                                    }

                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(lastLating).zoom(17).build();
                                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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
                    Intent it = new Intent(MapsActivity.this,modifyActivity.class);
                    startActivity(it);
                }
                else if(id==R.id.itemMap){

                }
                else if(id==R.id.itemLoan){
                    Intent it = new Intent(MapsActivity.this,loanActivity.class);
                    startActivity(it);
                    finish();
                }
                else if(id==R.id.itemLogout){
                    mAuth.signOut();
                    Intent it = new Intent(MapsActivity.this,loginActivity.class);
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

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadUser();
            }
        }, 3000);


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
                                                Glide.with(MapsActivity.this)
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




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng stust = new LatLng(23.02570163678237, 120.22647786889739);
//        mMap.addMarker(new MarkerOptions().position(stust).title("南台科技大學"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(stust));
//
//        CameraPosition cameraPosition = new CameraPosition.Builder().target(stust).zoom(17).build();
//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {

                Log.d("Demo",marker.getTitle());

                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle(marker.getTitle())
                        .setMessage("查看教室或場地請點選「詳細資料」")
                        .setPositiveButton("詳細資料", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent it = new Intent(MapsActivity.this,MainActivity.class);
                                it.putExtra("building",marker.getTitle());
                                startActivity(it);
                            }
                        })
                        .show();

                return false;
            }
        });
    }
}