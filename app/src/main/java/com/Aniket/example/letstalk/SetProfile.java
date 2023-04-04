package com.Aniket.example.letstalk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SetProfile extends AppCompatActivity {

    private CardView getUserImage;
    private ImageView getUserImageInImageView;
    private static int PICK_IMAGE = 123;
    private Uri imagepath;

    private EditText getUserName;
    android.widget.Button saveProfileBtn;

    private FirebaseAuth firebaseAuth;
    private String name;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private String ImageUriAcessToken;
    private FirebaseFirestore firebaseFirestore;
    ProgressBar progressbarofsetprofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);
        setTitle("Set Up Your Profile");
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0088cc")));

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        getUserName = findViewById(R.id.getUserName);
        getUserImage = findViewById(R.id.getUserImage);
        getUserImageInImageView = findViewById(R.id.getUserImageInImageView);
        saveProfileBtn = findViewById(R.id.saveProfileBtn);
        progressbarofsetprofile = findViewById(R.id.progressbarofsetprofile);

        getUserImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        saveProfileBtn.setOnClickListener(view -> {
            name = getUserName.getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Enter Name First", Toast.LENGTH_SHORT).show();
            } else if (imagepath == null) {
                Toast.makeText(getApplicationContext(), "Image is Empty", Toast.LENGTH_SHORT).show();
            } else {
                progressbarofsetprofile.setVisibility(View.VISIBLE);
                sendDataForNewUser();
                progressbarofsetprofile.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imagepath = data.getData();
            getUserImageInImageView.setImageURI(imagepath);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendDataForNewUser() {
        sendDataToRealTimeDatabase();
    }

    private void sendDataToRealTimeDatabase() {
        name = getUserName.getText().toString().trim();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());

        UserProfile muserprofile = new UserProfile(name, firebaseAuth.getUid());
        databaseReference.setValue(muserprofile);
        Toast.makeText(getApplicationContext(), "User Profile added successfully", Toast.LENGTH_SHORT).show();
        sendImagetoStorage();
    }

    private void sendImagetoStorage() {
        StorageReference imageref = storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Pic");

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagepath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        UploadTask uploadTask = imageref.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ImageUriAcessToken = uri.toString();
                        Toast.makeText(getApplicationContext(), "Uri get success", Toast.LENGTH_SHORT).show();
                        sendDataToCloudFireStore();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Uri get failed", Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(getApplicationContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Image Not Uploaded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendDataToCloudFireStore() {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        Map<String, Object> userdata = new HashMap<>();
        userdata.put("name", name);
        userdata.put("image", ImageUriAcessToken);
        userdata.put("uid", firebaseAuth.getUid());
        userdata.put("status", "online");

        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Data on cloud Uploaded", Toast.LENGTH_SHORT).show();
            }
        });
    }
}