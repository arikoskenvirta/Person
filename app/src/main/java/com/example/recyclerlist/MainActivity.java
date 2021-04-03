package com.example.recyclerlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recyclerlist.databinding.ActivityMainBinding;
import com.example.recyclerlist.databinding.OneLinePersonBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main";

    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter adapter;
    //private ImageView iw_profilePicture;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.rl_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(MainActivity.this, AddOrEditPerson.class);
        startActivity(intent);

        return true;
    }

    private ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = activityMainBinding.getRoot();
        setContentView(view);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("persons");

        RecyclerView mFirestoreRecyclerView;
        mFirestoreRecyclerView = activityMainBinding.rlRecyclerList;

        //rl_recyclerList options eli haetaan kannasta listan data
        FirestoreRecyclerOptions<Person> options = new FirestoreRecyclerOptions.Builder<Person>()
                .setQuery(query, Person.class)
                .build();

        //adapter
        adapter = new FirestoreRecyclerAdapter<Person, PersonViewHolder>(options) {
            @NonNull
            @Override
            public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                OneLinePersonBinding oneLinePersonBinding = OneLinePersonBinding.inflate(layoutInflater, parent, false);
                return new PersonViewHolder(oneLinePersonBinding);
            }

            @Override
            protected void onBindViewHolder(@NonNull PersonViewHolder holder, int position, @NonNull Person model) {
                StorageReference storageReference;

                holder.tv_name.setText(model.getName());
                holder.tv_age.setText(String.valueOf(model.getAge()));
                Glide.with(MainActivity.this).load(
                        model.getProfilePicture().toString()
                )
                        .into(holder.iw_profilePicture);


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

        mFirestoreRecyclerView.setHasFixedSize(true);
        mFirestoreRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreRecyclerView.setAdapter(adapter);

    }


    private class PersonViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private TextView tv_age;
        private ImageView iw_profilePicture;
        private de.hdodenhof.circleimageview.CircleImageView iw_personPicture2;

        OneLinePersonBinding oneLinePersonBinding;

        public PersonViewHolder(@NonNull OneLinePersonBinding oneLinePersonBinding) {
            super(oneLinePersonBinding.getRoot());
            this.oneLinePersonBinding = oneLinePersonBinding;

            tv_name = oneLinePersonBinding.tvName;
            tv_age = oneLinePersonBinding.tvAge;
            iw_profilePicture = oneLinePersonBinding.iwProfilePicture;

            this.oneLinePersonBinding.imageElasticView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //StorageReference storageReference;

                    DocumentSnapshot snapshot;
                    snapshot = (DocumentSnapshot) adapter.getSnapshots().getSnapshot(getAdapterPosition());
                    String uid = snapshot.getId();

                    Intent intent = new Intent(MainActivity.this, AddOrEditPerson.class);
                    intent.putExtra("uid", uid);

                    MainActivity.this.startActivity(intent);
                }
            }
            );
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