package com.example.firebase_crud;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

public class Add_Product_Activity extends AppCompatActivity {

    EditText et_proName, et_proPrice, et_proDes;
    ImageView proImage;
    Button addProduct;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private Uri imgUri, resultUri;
    Bitmap bitmap;
    FirebaseStorage storage;
    StorageReference storageReference;
    String imageName;
    private Dialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        et_proName = findViewById(R.id.et_proName);
        et_proPrice = findViewById(R.id.et_proPrice);
        et_proDes = findViewById(R.id.et_proDes);
        proImage = findViewById(R.id.pro_img);
        addProduct = findViewById(R.id.btn_addProduct);
        progressDialog = new ProgressDialog(Add_Product_Activity.this);
//        proImage.setOnClickListener(view -> CropImage.activity()
//                .setGuidelines(CropImageView.Guidelines.ON)
//                .start(Add_Product_Activity.this));
        proImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //testCode1();
                testCode2();
            }
        });

    }


    private void chooseImage() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(Add_Product_Activity.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                proImage.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
//        if (requestCode == 100 && resultCode == RESULT_OK
//                && data != null && data.getData() != null) {
//            imgUri = data.getData();
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
//                proImage.setImageURI(imgUri);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }

    }


//    void testCode1() {
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//
//        StorageReference storageRef = storage.getReference();
//        String imagename = "Image" + new Random().nextInt(10000) + ".jpg";
//
//        StorageReference spaceRef = storageRef.child("MyData/" + imagename);
//        //spaceRef.getName().equals(spaceRef.getName());    // true
//        //spaceRef.getPath().equals(spaceRef.getPath());
//
//        // Get the data from an ImageView as bytes
//        proImage.setDrawingCacheEnabled(true);
//        proImage.buildDrawingCache();
//        Bitmap bitmap = ((BitmapDrawable) proImage.getDrawable()).getBitmap();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] data = baos.toByteArray();
//
//        UploadTask uploadTask = spaceRef.putBytes(data);
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle unsuccessful uploads
//                int errorCode = ((StorageException) exception).getErrorCode();
//                String errorMessage = exception.getMessage();
//
//                Log.e("FFF", "Error Code= " +errorCode +"Error Msg="+((StorageException) exception).getIsRecoverableException());
//                Toast.makeText(Add_Product_Activity.this, "Failed=>"+exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
//                // ...
//                Toast.makeText(Add_Product_Activity.this, "onSuccess", Toast.LENGTH_SHORT).show();
//
//                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                    @Override
//                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                        if (!task.isSuccessful()) {
//                            throw task.getException();
//                        }
//
//                        // Continue with the task to get the download URL
//                        return spaceRef.getDownloadUrl();
//                    }
//                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Uri> task) {
//                        if (task.isSuccessful()) {
//                            Uri downloadUri = task.getResult();
//                            String imageurl = String.valueOf(downloadUri);
//                            Log.e("SSS", "onComplete: " + imageurl);
//
//                            // Write a message to the database
//                            FirebaseDatabase database = FirebaseDatabase.getInstance();
//                            DatabaseReference myRef = database.getReference("MyData").push();
//
//                            String id = myRef.getKey();
//                            String name = "H.zsdfgesagfesr";
//                            String number = "H.Haresh";
//
//                            Producs_Data producs_data = new Producs_Data(id, name, number, imageurl);
//
//                            myRef.setValue(producs_data);
//                            Toast.makeText(Add_Product_Activity.this, "Success", Toast.LENGTH_SHORT).show();
//                        } else {
//                            // Handle failures
//                            // ...
//                        }
//                    }
//                });
//
//            }
//        });
//    }

    void testCode2() {
        // Write a message to the database

        String imageName = "Img" + new Random().nextInt(10000) + ".jpg";

        storageReference = FirebaseStorage.getInstance().getReference();
        //storageReference=FirebaseStorage.getInstance().getReference("images"+imagename);
        StorageReference imgRef = storageReference.child("Images/"+imageName);

        // While the file names are the same, the references point to different files
        imgRef.getName().equals(imgRef.getName());    // true
        imgRef.getPath().equals(imgRef.getPath());    // false

        // Get the data from an ImageView as bytes
        proImage.setDrawingCacheEnabled(true);
        proImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) proImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imgRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(Add_Product_Activity.this, "Failed..", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Toast.makeText(Add_Product_Activity.this, "Success", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return imgRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            String imgUrl= String.valueOf(downloadUri);

                            database = FirebaseDatabase.getInstance(); // initializing object of database
                            myRef = database.getReference("Products").push(); // Creating main (parent) reference
                            Log.d("TTT", "testCode2: "+myRef);
                            String id = myRef.getKey();

                            Producs_Data dataModel = new Producs_Data(id,et_proName.getText().toString(), et_proPrice.getText().toString(), et_proDes.getText().toString(),imgUrl);
                            myRef.setValue(dataModel);

                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });

            }
        });

    }
}