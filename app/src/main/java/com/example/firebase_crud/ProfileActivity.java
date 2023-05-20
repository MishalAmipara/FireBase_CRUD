package com.example.firebase_crud;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ProfileActivity extends AppCompatActivity {

    Dialog dialog;
    FloatingActionButton fab;
    TextView textView;
    String signedUser;
    FirebaseAuth mAuth;
    Button signOut;
    FirebaseRecyclerAdapter adapter;
    RecyclerView recyclerView;
    DatabaseReference database;
    Query query;
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        fab=findViewById(R.id.fab);
        textView=findViewById(R.id.userName);
        signedUser=getIntent().getStringExtra("user");
        textView.setText(""+signedUser);
        recyclerView=findViewById(R.id.rec);
        mAuth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user.getPhoneNumber() != null)
        textView.setText(""+user.getPhoneNumber());
        else if(user.getEmail() != null)
            textView.setText(""+user.getEmail());
        else textView.setText("user not found!");

        signOut=findViewById(R.id.profile_signOut);

        query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Products")
                .limitToLast(50);
        FirebaseRecyclerOptions<Producs_Data> options
                = new FirebaseRecyclerOptions.Builder<Producs_Data>()
                .setQuery(query, Producs_Data.class)
                .build();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(options);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog=new Dialog(ProfileActivity.this);
                dialog.setContentView(R.layout.activity_add_product);
                dialog.show();
                Button addBtn=dialog.findViewById(R.id.btn_addProduct);
                EditText et_proName,et_proPrice,et_proDes;
                ImageView proImage;
                String imgUrl;
                et_proName=dialog.findViewById(R.id.et_proName);
                et_proPrice=dialog.findViewById(R.id.et_proPrice);
                proImage=dialog.findViewById(R.id.pro_img);
                proImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(ProfileActivity.this);
                    }
                });
                proImage.setImageURI(resultUri);

                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        FirebaseDatabase database = FirebaseDatabase.getInstance(); // initializing object of database
                        DatabaseReference myRef = database.getReference("Products").push(); // Creating main (parent) reference
                        String id = myRef.getKey();
                        System.out.println("ID="+id);
                        Log.d("TTT", "testCode2: id= "+id);

                        Producs_Data dataModel = new Producs_Data(id,et_proName.getText().toString(), et_proPrice.getText().toString(), "Device","https://firebasestorage.googleapis.com/v0/b/mishalsproject.appspot.com/o/Images%2FImg2579.jpg?alt=media&token=7b683721-b551-4aa2-ae14-4b3ffd34f6ff");
                        myRef.setValue(dataModel);
                        listProducts();
                    }
                });
            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(ProfileActivity.this,Authentication_Activity.class));
                SplashActivity.editor.putBoolean("isLoggedIn",false).commit();
                finish();
            }
        });

    }

    private void listProducts()
    {
//        FirebaseRecyclerOptions<Producs_Data> options
//                = new FirebaseRecyclerOptions.Builder<Producs_Data>()
//                .setQuery(query, Producs_Data.class)
//                .build();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //adapter = new MyAdapter(options);
        // Connecting Adapter class with the Recycler view*/
        recyclerView.setAdapter(adapter);

    }

    @Override protected void onStart()
    {
        super.onStart();
        adapter.startListening();
    }

    // Function to tell the app to stop getting
    // data from database on stopping of the activity
    @Override protected void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                //proImage.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }

    }
}