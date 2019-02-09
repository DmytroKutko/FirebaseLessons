package com.example.user.lessonsfb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.lessonsfb.model.Note;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";

    private EditText etTitle, etDescription, etPriority;
    private Button btnAdd, btnLoad;
    private TextView tvData;

    private FirebaseFirestore store;
    private CollectionReference notebookRef;
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

        notebookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(MainActivity.this, "Exception Load", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onEvent: " + e.toString());
                    return;
                }

                String data = "";
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Note note = documentSnapshot.toObject(Note.class);
                    note.setDocumentId(documentSnapshot.getId());
                    data += note.toString() + "\n";
                }
                tvData.setText(data);
            }
        });
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
                loadNote(v);
            }
        });
    }

    public void initView() {
        store = FirebaseFirestore.getInstance();
        noteRef = store.document("Notebook/First Note");
        notebookRef = store.collection("Notebook");

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etPriority = findViewById(R.id.etPriority);
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

        Note note = new Note(title, description, priority);

        notebookRef.add(note);
    }

    public void loadNote(View v) {
        Task task1 = notebookRef.whereLessThan("priority", 2)
                .orderBy("priority")
                .get();

        Task task2 = notebookRef.whereGreaterThan("priority", 3)
                .orderBy("priority")
                .get();

        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1, task2);
        allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                StringBuilder data = new StringBuilder();

                for (QuerySnapshot queryDocumentSnapshots : querySnapshots) {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Note note = documentSnapshot.toObject(Note.class);
                        note.setDocumentId(documentSnapshot.getId());
                        data.append(note.toString()).append("\n");
                    }
                }
                tvData.setText(data.toString());
            }
        });
    }
}
