package com.example.recyclerlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main";

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView mFirestoreRecyclerView;
    private FirestoreRecyclerAdapter adapter;

    Button btn_addNew;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        mFirestoreRecyclerView = findViewById(R.id.rl_recyclerList);

        Query query = db.collection("persons");

        //rl_recyclerList options
        FirestoreRecyclerOptions<Person> options = new FirestoreRecyclerOptions.Builder<Person>()
                .setQuery(query, Person.class)
                .build();
        //adapter
        adapter = new FirestoreRecyclerAdapter<Person, PersonViewHolder>(options) {
            @NonNull
            @Override
            public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_line_person, parent, false);
                return new PersonViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PersonViewHolder holder, int position, @NonNull Person model) {
                holder.tv_name.setText(model.getName());
                holder.tv_age.setText(String.valueOf(model.getAge()));
                Glide.with(MainActivity.this).load(model.getImageURL()).into(holder.iw_personPicture);


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                        String uid = snapshot.getId();

                        Intent intent = new Intent(MainActivity.this, AddOrEditPerson.class);
                        intent.putExtra("uid", uid);

                        MainActivity.this.startActivity(intent);

                    }
                });
            }
        };

        btn_addNew = findViewById(R.id.btn_addNew);
        btn_addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddOrEditPerson.class);
                startActivity(intent);
            }
        });

        //recyclerView = findViewById(R.id.lv_presidentList);
        mFirestoreRecyclerView.setHasFixedSize(true);
        mFirestoreRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreRecyclerView.setAdapter(adapter);

    }



    private class PersonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tv_name;
        private TextView tv_age;
        private ImageView iw_personPicture;

        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_age = itemView.findViewById(R.id.tv_age);
            iw_personPicture = itemView.findViewById(R.id.iw_personPicture);


        }

        @Override
        public void onClick(View v) {
            //Intent intent = new Intent(MainActivity.this, AddOrEditOne.class);
            //startActivity(intent);

        }
    }

    @Override
    protected void  onStop(){
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void  onStart(){
        super.onStart();
        adapter.startListening();
    }



}