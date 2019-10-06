package com.example.chattingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends AppCompatActivity {

    private CircleImageView profilePic;
    private EditText mUserName;
    private Button setDetailsBtn;

    private int PICK_IMAGE = 1;

    private Uri filePath;
    private Bitmap selectedImage;

    private ProgressDialog progressBar;

    private String mCurrentUID;

    private DatabaseReference mRootRef;
    private HashMap<String, String> detailMap = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        
        profilePic = findViewById(R.id.profilePic);
        mUserName = findViewById(R.id.userName);
        setDetailsBtn = findViewById(R.id.setDetailsBtn);

        progressBar = new ProgressDialog(this);

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mCurrentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        
        setDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDetails();
            }
        });
        
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageFromGallery();
            }
        });
        
    }

    private void pickImageFromGallery() {

        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);

        getIntent.setType("image/*");



        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        pickIntent.setType("image/*");



        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});



        startActivityForResult(chooserIntent, PICK_IMAGE);

    }

    private void setDetails() {

        if(mUserName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter your Name first!", Toast.LENGTH_SHORT).show();
        } else {

            progressBar.setTitle("Setting Up Your Account");
            progressBar.setMessage("Please wait while we upload the data!");
            progressBar.setCanceledOnTouchOutside(false);
            progressBar.show();


            if(filePath != null) {
                uploadImage(filePath);
            } else {
                detailMap.put("UID",mCurrentUID);
                detailMap.put("name", mUserName.getText().toString());

                mRootRef.child("Users").child(mCurrentUID).setValue(detailMap);
                Intent i = new Intent(UserInfoActivity.this, StartActivity.class);
                startActivity(i);
                progressBar.dismiss();
            }


        }
        
    }

    private void uploadImage(final Uri imagePath) {

        final StorageReference imageStorageRef = FirebaseStorage.getInstance().getReference().child("Images").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Posts");

        final StorageReference compressedImageRef = imageStorageRef.child("/" + System.currentTimeMillis() + " - Compressed Pic.jpg");

        final StorageReference imageRef = imageStorageRef.child("/" + System.currentTimeMillis() + " - Pic.jpg");


        try {

            final InputStream imageStream = getContentResolver().openInputStream(imagePath);

            selectedImage = BitmapFactory.decodeStream(imageStream);

        } catch (FileNotFoundException e) {

            e.printStackTrace();

            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();

        }



        Bitmap bmpCompressed = selectedImage;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bmpCompressed.compress(Bitmap.CompressFormat.JPEG, 25, baos);

        byte[] data = baos.toByteArray();


        //uploading the image

        UploadTask compressedUploadTask = compressedImageRef.putBytes(data);

        compressedUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override

            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {



                compressedImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                    @Override

                    public void onSuccess(Uri uri) {


                        final HashMap<String, String> postMap = new HashMap();

                        postMap.put("imageComp",uri.toString());

                        UploadTask uploadTask = imageRef.putFile(imagePath);

                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                            @Override

                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {



                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                                    @Override

                                    public void onSuccess(Uri uri) {

                                        postMap.put("image",uri.toString());
                                        postMap.put("UID",mCurrentUID);
                                        postMap.put("name", mUserName.getText().toString());

                                        mRootRef.child("Users").child(mCurrentUID).setValue(detailMap);
                                        mRootRef.child("Users").child(mCurrentUID).setValue(postMap);

                                    }

                                });

                                Toast.makeText(UserInfoActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                                progressBar.dismiss();
                                Intent i = new Intent(UserInfoActivity.this, StartActivity.class);
                                startActivity(i);

                            }

                        }).addOnFailureListener(new OnFailureListener() {

                            @Override

                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(UserInfoActivity.this, "Upload Failed -> " + e, Toast.LENGTH_LONG).show();

                            }

                        });

                    }

                });

            }

        }).addOnFailureListener(new OnFailureListener() {

            @Override

            public void onFailure(@NonNull Exception e) {

                Toast.makeText(UserInfoActivity.this, "Upload Failed -> " + e, Toast.LENGTH_LONG).show();

            }

        });

    }


    @Override

    public void onActivityResult(int reqCode, int resultCode, Intent data) {

        super.onActivityResult(reqCode, resultCode, data);





        if (resultCode == RESULT_OK) {



            if(data.getData() != null) {

                filePath = data.getData();

                profilePic.setImageURI(filePath);

            }



        }else {

            Toast.makeText(UserInfoActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();

        }

    }



}
