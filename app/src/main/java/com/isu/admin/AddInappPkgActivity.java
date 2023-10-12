package com.isu.admin;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;
import java.util.Objects;

public class AddInappPkgActivity extends AppCompatActivity {

    String pkgName = "";
    EditText etPkgName;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inapp_pkg_add);


        etPkgName = findViewById(R.id.etPkgName);
        progressDialog = new ProgressDialog(this);




        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pkgName = etPkgName.getText().toString().trim();
                if (pkgName.equals("")) {
                    etPkgName.setError("Please enter Pkg Name..!!");
                } else {
                    getAllPkgFromFirebase();
                }


            }
        });


    }



    private void getAllPkgFromFirebase() {
        progressDialog.setMessage("Please wait...!!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("AdminNameManagement").document(pkgName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    if (task.getResult().getData()!=null) {
                        Log.e(TAG, "getMapData"+task.getResult().getData().toString());
                        addOnFireBase(task.getResult().getData());
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(AddInappPkgActivity.this,"Package Not found....!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void addOnFireBase(Map<String, Object> data) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference uNames = db.collection("AdminNameManagement");
        final String newPkgName = pkgName + ".inapp";//".beta";//
        uNames.document(newPkgName).set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "DocumentSnapshot successfully written!");
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "InApp New App Added...!!!", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }
}
