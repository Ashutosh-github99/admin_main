package com.isu.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiteActivity extends AppCompatActivity {
    private static final String TAG = LiteActivity.class.getSimpleName();
    ArrayList<LiteAdmin> adminItems;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lite);

        showFirestore();

        findViewById(R.id.click1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: " + adminItems.size());
                update();
            }
        });
    }

    public void update() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference uNames = db.collection("LiteAppPackage");

        for (int i = 0; i < adminItems.size(); i++) {
            Map<String, Object> appDetails = new HashMap<>();
            appDetails.put("AdminName", adminItems.get(i).getaName());

            String packageName = adminItems.get(i).getpName();

            uNames.document(packageName).update(appDetails)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e(TAG, "DocumentSnapshot successfully written!" + appDetails);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Error writing document" + e + " for package" + packageName);
                }
            });
        }


    }

    public void showFirestore() {
        adminItems = new ArrayList<>();


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference uNames = db.collection("LiteAppPackage");

        uNames.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "onEvent: error occured");
                }

                if (snapshots.isEmpty()) {
                    Log.e(TAG, "onEvent: empty snap");
                } else {
                    adminItems = new ArrayList<>();
                    List<DocumentSnapshot> doc = snapshots.getDocuments();
                    for (int i = 0; i < doc.size(); i++) {
                        String packageName = doc.get(i).getId();
                        if (!packageName.equals("com.miniatm.miniatmlite") &&
                                !packageName.equals("com.suvidha.suvidhamartlite") &&
                                !packageName.equals("com.isu.raspaylitee") &&
                                !packageName.equals("com.bharatmoney.bharatmoneylite") &&
                                !packageName.equals("com.digitalindia.digitalindiapaymentslite")) {
                            Map<String, Object> appDetails = doc.get(i).getData();
                            String adminName = (String) appDetails.get("AdminName");

                            Log.e(TAG, "onEvent: package " +packageName+ " "+ appDetails.get("AdminName"));
                            adminName = adminName.substring(0, adminName.length()-1);
                            Log.e(TAG, "onEvent: new admin"+adminName );

                            adminItems.add(new LiteAdmin(packageName, adminName));
                        }
                    }
                }


            }
        });


    }

}