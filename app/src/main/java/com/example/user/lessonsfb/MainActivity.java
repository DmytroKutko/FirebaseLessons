package com.example.user.lessonsfb;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.lessonsfb.model.Note;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EditText etTitle, etDescription, etPriority;
    private Button btnAdd, btnLoad;
    private TextView tvData;

    private FirebaseFirestore store;
    private CollectionReference notebookRef;
    private DocumentSnapshot lastResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListener();
        executeTransaction();
    }

    private void executeTransaction() {
        store.runTransaction(new Transaction.Function<Long>() {
            @Override
            public Long apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference noteRef = notebookRef.document("New Note");
                DocumentSnapshot documentSnapshot = transaction.get(noteRef);
                long newPriority = documentSnapshot.getLong("priority") + 1;
                transaction.update(noteRef, "priority", newPriority);
                return newPriority;
            }
        }).addOnSuccessListener(new OnSuccessListener<Long>() {
            @Override
            public void onSuccess(Long aLong) {
                Toast.makeText(MainActivity.this, "New Priority: " + aLong, Toast.LENGTH_SHORT).show();
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

    public void loadNotes(View v) {
        Query query;

        if (lastResult == null) {
            query = notebookRef.orderBy("priority")
                    .limit(3);
        } else {
            query = notebookRef.orderBy("priority")
                    .startAfter(lastResult)
                    .limit(3);
        }

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        StringBuilder data = new StringBuilder();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            Note note = queryDocumentSnapshot.toObject(Note.class);
                            note.setDocumentId(queryDocumentSnapshot.getId());
                            data.append(note.toString());
                        }
                        if (queryDocumentSnapshots.size() > 0) {
                            data.append("\n_________________________________\n");
                            tvData.append(data);
                            lastResult = queryDocumentSnapshots.getDocuments()
                                    .get(queryDocumentSnapshots.size() - 1);
                        }
                    }
                });

    }
}
