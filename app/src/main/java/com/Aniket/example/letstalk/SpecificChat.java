package com.Aniket.example.letstalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SpecificChat extends AppCompatActivity {

    EditText mgetmessage;
    ImageButton msendmessagebutton;
    CardView msendmessagecardview;
    androidx.appcompat.widget.Toolbar mtoolbarofspecificchat;
    ImageView mimageviewofspecificuser;
    TextView mnameofspecificuser;
    private String enteredmessage;
    String mrecievername,sendername,mrecieveruid,msenderuid;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseFirestore firebaseFirestore;
    String senderroom,recieverroom;
    RecyclerView mmessagerecyclerview;

    String currenttime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    MessagesAdapter messagesAdapter;
    ArrayList<Messages> messagesArrayList;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_chat);
        getSupportActionBar().hide();

        mgetmessage = findViewById(R.id.getmessage);
        msendmessagecardview = findViewById(R.id.cardviewofsendmessage);
        msendmessagebutton = findViewById(R.id.imageViewofSendMessage);
        mtoolbarofspecificchat = findViewById(R.id.toolbarofspecificchat);
        mnameofspecificuser = findViewById(R.id.nameofspecificuser);
        mimageviewofspecificuser = findViewById(R.id.specificUserImageInImageView);
        messagesArrayList = new ArrayList<>();
        mmessagerecyclerview = findViewById(R.id.recyclerviewofspecificchat);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mmessagerecyclerview.setLayoutManager(linearLayoutManager);
        messagesAdapter = new MessagesAdapter(SpecificChat.this,messagesArrayList);

        mmessagerecyclerview.setAdapter(messagesAdapter);


        mtoolbarofspecificchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Toolbar is clicked",Toast.LENGTH_SHORT).show();

            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm a");

        msenderuid = firebaseAuth.getUid();
        mrecieveruid = getIntent().getStringExtra("receiveruid");
        mrecievername = getIntent().getStringExtra("name");

        senderroom = msenderuid+mrecieveruid;
        recieverroom = mrecieveruid+msenderuid;

        DatabaseReference databaseReference=firebaseDatabase.getReference().child("chats").child(senderroom).child("messages");
        messagesAdapter=new MessagesAdapter(SpecificChat.this,messagesArrayList);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Messages messages = snapshot1.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        mnameofspecificuser.setText(mrecievername);
        String uri = getIntent().getStringExtra("imageuri");

        if(uri.isEmpty()){
            Toast.makeText(getApplicationContext(),"Profile image not received",Toast.LENGTH_SHORT).show();
        }else{
            Picasso.get().load(uri).into(mimageviewofspecificuser);
        }

        msendmessagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredmessage = mgetmessage.getText().toString();
                if(enteredmessage.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Message is Empty",Toast.LENGTH_SHORT).show();
                }else{
                    Date date = new Date();
                    currenttime = simpleDateFormat.format(calendar.getTime());
                    Messages messages = new Messages(enteredmessage,firebaseAuth.getUid(), date.getTime(),currenttime);
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    firebaseDatabase.getReference().child("chats")
                            .child(senderroom)
                            .child("messages")
                            .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            firebaseDatabase.getReference().child("chats")
                                    .child(recieverroom)
                                    .child("messages")
                                    .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(),"Message sent",Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    });

                    mgetmessage.setText(null);
                }
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        messagesAdapter.notifyDataSetChanged();
//        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
//        documentReference.update("status","online").addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                Toast.makeText(getApplicationContext(),"Now user is online",Toast.LENGTH_SHORT).show();
//
//            }
//        });

    }

    @Override
    public void onStop() {
        super.onStop();

        if(messagesAdapter!=null){
            messagesAdapter.notifyDataSetChanged();
        }
//        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
//        documentReference.update("status","Offline").addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                Toast.makeText(getApplicationContext(),"Now user is Offline",Toast.LENGTH_SHORT).show();
//
//            }
//        });
    }
}