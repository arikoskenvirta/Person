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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main";

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView mFirestoreRecyclerView;
    private FirestoreRecyclerAdapter adapter;

    private ImageView iw_profilePicture;

    //Button btn_addNew;


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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("persons");

        mFirestoreRecyclerView = findViewById(R.id.rl_recyclerList);

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
                StorageReference storageReference;

                holder.tv_name.setText(model.getName());
                holder.tv_age.setText(String.valueOf(model.getAge()));
                Glide.with(MainActivity.this).load(
                        model.getProfilePicture().toString()
                )
                        .into(holder.iw_profilePicture);

                //                //        "https://firebasestorage.googleapis.com/v0/b/recyclerlist-f7b76.appspot.com/o/ProfilePictures%2FSARI?alt=media&token=49f7cc71-a626-4cab-84d6-abc869e1ad58"
                /*
                storageReference = FirebaseStorage.getInstance().getReference().child("ProfilePicture/" + model.getName() );

                try {
                    final File localFile = File.createTempFile("test", ".jpg");

                    storageReference.getFile(localFile )
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                            firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String url = uri.toString();
                                    Glide.with(MainActivity.this).load(url).into(holder.iw_profilePicture);
                                    //
                                    //update to profile url field

                                }
                            });



                            //Bitmap bitmap = BitmapFactory.decodeFile(model.getProfilePicture());
                            //((ImageView) findViewById(R.id.iw_profilePicture)).setImageBitmap(bitmap);
                            //((de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.iw_personPicture)).setImageBitmap(bitmap);


                            //holder.iw_personPicture2.setImageResource();


                            Glide.with(MainActivity.this).load(model.getProfilePicture()).into(holder.iw_profilePicture);
                            //Glide.with(MainActivity.this).load(model.getProfilePicture()).into(findViewById(R.id.iw_profilePicture)));
                            //Glide.with(MainActivity.this).load(localFile.getAbsoluteFile())
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }


                 */

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



    private class PersonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tv_name;
        private TextView tv_age;
        private ImageView iw_profilePicture;
        private de.hdodenhof.circleimageview.CircleImageView iw_personPicture2;

        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_age = itemView.findViewById(R.id.tv_age);
            iw_profilePicture = itemView.findViewById(R.id.iw_profilePicture);
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