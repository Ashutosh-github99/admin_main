package com.isu.admin;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdatePackageActivity extends AppCompatActivity {
    private static final String TAG = UpdatePackageActivity.class.getSimpleName();

    ArrayList<String> packageNames;

    TextView messageTV;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_package);

        messageTV = findViewById(R.id.update_message);
        progressBar = findViewById(R.id.update_progress);

        getList();

        findViewById(R.id.update_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update();
                updateSinglePackage();
            }
        });

    }

    public void update() {
        Map<String, Object> onboardMap = new HashMap<>();

        Map<String, Object> basicInfo = new HashMap<>();
        basicInfo.put("Available", true);
        basicInfo.put("Skip", false);

        Map<String, Object> aadhaar = new HashMap<>();
        aadhaar.put("Available", true);
        aadhaar.put("Skip", false);
        aadhaar.put("Sequence", "2");

        Map<String, Object> email = new HashMap<>();
        email.put("Available", true);
        email.put("Skip", true);

        Map<String, Object> mobile = new HashMap<>();
        mobile.put("Available", true);
        mobile.put("Skip", false);

        Map<String, Object> pan = new HashMap<>();
        pan.put("Available", true);
        pan.put("Skip", false);
        pan.put("Sequence", "1");

        onboardMap.put("BasicInfo", basicInfo);
        onboardMap.put("Aadhaar", aadhaar);
        onboardMap.put("Email", email);
        onboardMap.put("Mobile", mobile);
        onboardMap.put("Pan", pan);
        onboardMap.put("ShowOnBoard", "0");

        Map<String, Object> map = new HashMap<>();

        map.put("Onboard", onboardMap);


        progressBar.setVisibility(View.VISIBLE);
        findViewById(R.id.update_list).setVisibility(View.GONE);

        for (int i = 0; i < packageNames.size(); i++) {
            String name = packageNames.get(i);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference uNames = db.collection("AdminNameManagement");
            int finalI = i;
            uNames.document(name).update(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            String msg = "added Onboard data " + finalI + "/" + packageNames.size() + "\n" + name;
                            messageTV.setText(msg);

                            Log.e(TAG, "DocumentSnapshot successfully written!");
                            if (finalI == packageNames.size() - 1) {
                                progressBar.setVisibility(View.GONE);
                                messageTV.setText("All data added");
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Error writing document", e);
                }
            });


        }


    }

    String AdminName = "";
    String AppCode = "";
    String AppName = "";
    String CreatedBy = "";
    String MainApp = "";
    String UniqueId = "";
    String VersionName = "";

    public void updateSinglePackage() {
        Map<String, Object> onboardMap = new HashMap<>();

        Map<String, Object> basicInfo = new HashMap<>();
        basicInfo.put("Available", true);
        basicInfo.put("Skip", false);

        Map<String, Object> aadhaar = new HashMap<>();
        aadhaar.put("Available", true);
        aadhaar.put("Skip", false);
        aadhaar.put("Sequence", "2");

        Map<String, Object> email = new HashMap<>();
        email.put("Available", true);
        email.put("Skip", true);

        Map<String, Object> mobile = new HashMap<>();
        mobile.put("Available", true);
        mobile.put("Skip", false);

        Map<String, Object> pan = new HashMap<>();
        pan.put("Available", true);
        pan.put("Skip", false);
        pan.put("Sequence", "1");

        onboardMap.put("BasicInfo", basicInfo);
        onboardMap.put("Aadhaar", aadhaar);
        onboardMap.put("Email", email);
        onboardMap.put("Mobile", mobile);
        onboardMap.put("Pan", pan);
        onboardMap.put("ShowOnBoard", "0");

        Map<String, Object> map = new HashMap<>();
        map.put("Onboard", onboardMap);

        /*map.put("AdminName", AdminName);
        map.put("AppCode", AppCode);
        map.put("AppName", AppName);
        map.put("CreatedBy", CreatedBy);
        map.put("MainApp", MainApp);
        map.put("UniqueId", UniqueId);
        map.put("VersionName", VersionName);*/

        progressBar.setVisibility(View.VISIBLE);
        findViewById(R.id.update_list).setVisibility(View.GONE);

        String name = "com.alltimemoney.alltimemoney";
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference uNames = db.collection("AdminNameManagement");
        uNames.document(name).update(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        messageTV.setText(name);

                        progressBar.setVisibility(View.GONE);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error writing document", e);
            }
        });


    }

    public void getList() {
        ProgressDialog dialog = new ProgressDialog(UpdatePackageActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        packageNames = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference pNames = db.collection("AdminNameManagement");

        pNames.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshots) {
                List<DocumentSnapshot> doc = snapshots.getDocuments();
                for (int i = 0; i < doc.size(); i++) {
                    String name = doc.get(i).getId();
                    packageNames.add(name);
                }
                Log.e(TAG, "onSuccess: " + packageNames.size());
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: error " + e.getMessage());
                dialog.dismiss();
            }
        });


    }

}