package com.example.recyclerlist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddOrEditPerson extends AppCompatActivity {

    private static final String TAG = "Edit";
    //MyApplication myApplication = (MyApplication) this.getApplication();

    Button btn_ok, btn_cancel, btn_delete;
    EditText et_name, et_age, et_pictureURL;
    TextView tv_id;

    String id;
    Person person = null;

    List<Person> personList;

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_person);

        et_name = findViewById(R.id.et_name);
        et_age =  findViewById(R.id.et_age);
        et_pictureURL =  findViewById(R.id.et_pictureURL);
        tv_id =  findViewById(R.id.tv_id);

        Intent intent = getIntent();
        id = intent.getStringExtra("uid");


        if ( id != null) {

            DocumentReference docRef = db.collection("persons").document(id);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Person person = documentSnapshot.toObject(Person.class);

                    et_name.setText(person.getName());
                    et_age.setText(String.valueOf(person.getAge()));
                    et_pictureURL.setText(person.getImageURL());
                    tv_id.setText(person.getId());

                }
            });

        }

        btn_ok = findViewById(R.id.btn_save);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> person = new HashMap<>();
                person.put("name", et_name.getText().toString());
                person.put("age", Integer.parseInt(et_age.getText().toString()));
                person.put("imageurl", et_pictureURL.getText().toString());

                updatepersondata ( person, id);

                Intent intent = new Intent( AddOrEditPerson.this, MainActivity.class);
                startActivity(intent);
            }
        });


        btn_delete = findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("persons").document(id)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });

                Intent intent = new Intent( AddOrEditPerson.this, MainActivity.class);
                startActivity(intent);
            }
        });


        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( AddOrEditPerson.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }




    private void updatepersondata(Map<String, Object> person, String id) {

        if (id != null) {

            db.collection("persons").document(id)
                    .set(person)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });

        }
        else {

            db.collection("persons")
                    .add(person)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });

        }
    }

}