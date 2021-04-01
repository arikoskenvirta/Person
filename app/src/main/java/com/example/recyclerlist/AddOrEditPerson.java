package com.example.recyclerlist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddOrEditPerson extends AppCompatActivity {

    private static final String TAG = "Edit";

    Button btn_ok, btn_cancel, btn_delete;
    EditText et_name, et_age, et_profilePicture;
    TextView tv_id;

    String id;
    Person person = null;

    List<Person> personList;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public ImageView iw_profilePicture;
    public Uri imageURi;

    private FirebaseStorage storage;
    private StorageReference storageReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_person);

        et_name = findViewById(R.id.et_name);
        et_age =  findViewById(R.id.et_age);
        et_profilePicture =  findViewById(R.id.et_profilePicture);
        tv_id =  findViewById(R.id.tv_id);
        iw_profilePicture =  findViewById(R.id.iw_profilePicture);

        iw_profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

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
                    et_profilePicture.setText(person.getProfilePicture());
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
                person.put("profilePicture", et_profilePicture.getText().toString());

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

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageURi = data.getData();
            iw_profilePicture.setImageURI(imageURi);

            String filename = imageURi.getLastPathSegment();
            filename = filename.substring(filename.lastIndexOf("/")+1);

            uploadPicture(filename);
            et_profilePicture.setText(filename);
            
            
        }
            
    }

    private void uploadPicture(String filename) {

        ProgressBar pb;

        StorageReference storageRef = storageReference.child("ProfilePictures/"+ filename );

        pb = (ProgressBar)findViewById(R.id.progress_bar);

        storageRef.putFile(imageURi)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Snackbar.make(findViewById(android.R.id.content), "Profilepicture " + filename + " uploaded.", Snackbar.LENGTH_LONG).show();
                        pb.setVisibility(View.INVISIBLE);
                        Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                        firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String url = uri.toString();
                                //update to profile url field

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to upload profilepicture" + filename, Toast.LENGTH_LONG).show();
                        pb.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot tasksnapshot) {
                    double progressPercent = (100.00 * tasksnapshot.getBytesTransferred()/tasksnapshot.getTotalByteCount());
                    if (tasksnapshot.getBytesTransferred()-tasksnapshot.getTotalByteCount()!=0) {
                        pb.setProgress((int)progressPercent);
                    }
                    else
                        pb.setVisibility(View.INVISIBLE);



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



/*    private void makePhoneCall(String phonenumber) {

            String number = phonenumber;
                String dial = "tel:" + "0407308732";

                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(dial));
                startActivity(intent);

                Toast.makeText(this, "Enter Phone Number", Toast.LENGTH_SHORT).show();


        }

 */

}