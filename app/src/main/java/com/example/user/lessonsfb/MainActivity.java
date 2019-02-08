package com.example.user.lessonsfb;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.lessonsfb.model.Note;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";

    private EditText etTitle, etDescription;
    private Button btnSubmit, btnLoad, btnUpdate, btnDeleteDescription, btnDeleteNote;
    private TextView tvData;

    private FirebaseFirestore store;
    private DocumentReference noteRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(MainActivity.this, "Error loading", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onEvent: " + e.toString());
                    return;
                }
                if (documentSnapshot.exists()) {
                    Note note = documentSnapshot.toObject(Note.class);
                    tvData.setText("Title: " + note.getTitle() +
                            "\n" + "Description: " + note.getDescription() +
                            '\n' + "Time created: " + note.getUnixTime());
                } else {
                    tvData.setText("");
                }
            }
        });
    }

    public void initListener() {

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote(v);
            }
        });

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNote(v);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDescription(v);
            }
        });

        btnDeleteDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDescription(v);
            }
        });

        btnDeleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote(v);
            }
        });
    }

    public void initView() {
        store = FirebaseFirestore.getInstance();
        noteRef = store.document("Notebook/First Note");

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnLoad = findViewById(R.id.btnLoad);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDeleteDescription = findViewById(R.id.btnDeleteDescription);
        btnDeleteNote = findViewById(R.id.btnDeleteNote);
        tvData = findViewById(R.id.tvData);
    }

    public void deleteDescription(View v) {
        noteRef.update(KEY_DESCRIPTION, FieldValue.delete());
    }

    public void deleteNote(View v) {
        noteRef.delete();
    }

    public void saveNote(View v) {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        Note note = new Note(title, description);

        noteRef.set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Fail to load note", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
    }

    public void updateDescription(View v) {
        String description = etDescription.getText().toString().trim();

        noteRef.update(KEY_DESCRIPTION, description);
    }

    public void loadNote(View v) {
        noteRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Note note = documentSnapshot.toObject(Note.class);
                            tvData.setText("Title: " + note.getTitle() +
                                    "\n" + "Description: " + note.getDescription() +
                                    '\n' + "Time created: " + note.getUnixTime());
                        } else {
                            Toast.makeText(MainActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Fail to load data", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
    }
}
