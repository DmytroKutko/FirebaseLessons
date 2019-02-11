package com.example.user.lessonsfb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.user.lessonsfb.model.Note;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EditText etTitle, etDescription, etPriority, etTags;
    private Button btnAdd, btnLoad;
    private TextView tvData;

    private FirebaseFirestore store;
    private CollectionReference notebookRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListener();
        updateArray();
    }

    private void updateArray() {
        notebookRef.document("x6R9ypYKN5c1ktBi875d")
//                .update("tags", FieldValue.arrayUnion("new tag"))
                .update("tags", FieldValue.arrayRemove("new tag"));
    }

    public void initListener() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote(v);
            }
        });
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNotes(v);
            }
        });
    }

    public void initView() {
        store = FirebaseFirestore.getInstance();
        notebookRef = store.collection("Notebook");

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etPriority = findViewById(R.id.etPriority);
        etTags = findViewById(R.id.etTags);
        btnAdd = findViewById(R.id.btnAdd);
        btnLoad = findViewById(R.id.btnLoad);
        tvData = findViewById(R.id.tvData);
    }

    public void addNote(View v) {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (etPriority.length() == 0) {
            etPriority.setText("0");
        }

        int priority = Integer.parseInt(etPriority.getText().toString());

        String tagInput = etTags.getText().toString();
        String[] tagArray = tagInput.split("\\s*,\\s*");
        List<String> tags = Arrays.asList(tagArray);

        Note note = new Note(title, description, priority, tags);

        notebookRef.add(note);
    }

    public void loadNotes(View v) {
        notebookRef.whereArrayContains("tags", "tag3").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        StringBuilder data = new StringBuilder();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Note note = documentSnapshot.toObject(Note.class);
                            note.setDocumentId(documentSnapshot.getId());

                            data.append(note.toString()).append("\n");
                        }
                        tvData.setText(data);
                    }
                });
    }
}
