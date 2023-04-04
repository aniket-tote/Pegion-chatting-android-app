package com.Aniket.example.letstalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UpdateActivity extends AppCompatActivity {

    private CardView getnewUserImage;
    private ImageView getnewUserImageInImageView;
    private static int PICK_IMAGE = 123;
    private Uri imagepath;

    private EditText getnewUserName;
    android.widget.Button updateProfileBtn;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    private String newname;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private String ImageUriAcessToken;
    private FirebaseFirestore firebaseFirestore;
    ProgressBar progressbarofupdateprofile;
    //Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        setTitle(R.string.app_name);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0088cc")));

        getnewUserImageInImageView = findViewById(R.id.getnewUserImageInImageView);
        progressbarofupdateprofile = findViewById(R.id.progressbarofupdateprofile);
        getnewUserName = findViewById(R.id.getnewUserName);
        updateProfileBtn = findViewById(R.id.updateProfileBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //intent.getIntent();
        String name = getIntent().getStringExtra("nameofuser");
        getnewUserName.setText(name);

        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());

        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newname = getnewUserName.getText().toString();
                if(newname.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Name is empty",Toast.LENGTH_SHORT).show();
                }else if(imagepath!=null){
                    progressbarofupdateprofile.setVisibility(View.VISIBLE);
                    UserProfile muserprofile = new UserProfile(newname,firebaseAuth.getUid());
                    databaseReference.setValue(muserprofile);
                    updateimagetostorage();
                    Toast.makeText(getApplicationContext(),"Updating",Toast.LENGTH_SHORT).show();
                    progressbarofupdateprofile.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(UpdateActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    progressbarofupdateprofile.setVisibility(View.VISIBLE);
                    UserProfile muserprofile = new UserProfile(newname,firebaseAuth.getUid());
                    databaseReference.setValue(muserprofile);

                    updatenameoncloudfirestore();
                    Toast.makeText(getApplicationContext(),"Updating",Toast.LENGTH_SHORT).show();
                    progressbarofupdateprofile.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(UpdateActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        getnewUserImageInImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,PICK_IMAGE);
            }
        });

        storageReference = firebaseStorage.getReference();
        storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageUriAcessToken = uri.toString();
                Picasso.get().load(uri).into(getnewUserImageInImageView);
            }
        });
    }

    private void updatenameoncloudfirestore() {

        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        Map<String, Object> userdata = new HashMap<>();
        userdata.put("name", newname);
        userdata.put("image", ImageUriAcessToken);
        userdata.put("uid", firebaseAuth.getUid());
        userdata.put("status", "online");

        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateimagetostorage() {

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
                        updatenameoncloudfirestore();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Uri get failed", Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(getApplicationContext(), "Image Updated", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Image Not Uploaded", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imagepath = data.getData();
            getnewUserImageInImageView.setImageURI(imagepath);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status","Offline").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(),"Now user is offline",Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status","Online").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(),"Now user is online",Toast.LENGTH_SHORT).show();

            }
        });
    }
}