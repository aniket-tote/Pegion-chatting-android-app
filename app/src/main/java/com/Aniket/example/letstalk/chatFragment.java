package com.Aniket.example.letstalk;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class chatFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    LinearLayoutManager linearLayoutManager;
    FirebaseAuth firebaseAuth;

    ImageView mimageviewofuser;

    FirestoreRecyclerAdapter<FirebaseModel,NoteViewHolder> chatAdapter;

    RecyclerView mrecyclerview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chatfragment,container,false);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        mrecyclerview = v.findViewById(R.id.recyclerview);

        Query query = firebaseFirestore.collection("Users").whereNotEqualTo("uid",firebaseAuth.getUid());

        FirestoreRecyclerOptions<FirebaseModel> allusername = new FirestoreRecyclerOptions.Builder<FirebaseModel>().setQuery(query,FirebaseModel.class).build();

        chatAdapter = new FirestoreRecyclerAdapter<FirebaseModel, NoteViewHolder>(allusername) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull FirebaseModel firebaseModel) {

                noteViewHolder.particularusername.setText(firebaseModel.getName());
                String uri = firebaseModel.getImage();

                Picasso.get().load(uri).into(mimageviewofuser);

                if(firebaseModel.getStatus().equals("Online")){
                    noteViewHolder.statusofuser.setText(firebaseModel.getStatus());
                    noteViewHolder.statusofuser.setTextColor(Color.GREEN);
                }else{
                    noteViewHolder.statusofuser.setText(firebaseModel.getStatus());
                }

                noteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getActivity(),SpecificChat.class);
                        intent.putExtra("name",firebaseModel.getName());
                        intent.putExtra("receiveruid",firebaseModel.getUid());
                        intent.putExtra("imageuri",firebaseModel.getImage());
                        startActivity(intent);
                        //Toast.makeText(getActivity(),"Item is clicked",Toast.LENGTH_SHORT).show();
                    }
                });


            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.chatviewlayout,parent,false);
                return new NoteViewHolder(view);
            }
        };

        mrecyclerview.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mrecyclerview.setLayoutManager(linearLayoutManager);
        mrecyclerview.setAdapter(chatAdapter);
        return v;
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder{

        private TextView particularusername;
        private TextView statusofuser;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            particularusername = itemView.findViewById(R.id.nameofuser);
            statusofuser = itemView.findViewById(R.id.statusofuser);
            mimageviewofuser = itemView.findViewById(R.id.imageviewofuser);

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        chatAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        if(chatAdapter!=null){
            chatAdapter.stopListening();
        }
    }
}
