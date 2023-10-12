package com.isu.admin;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BannerLoopActivity extends AppCompatActivity {

    private static final String TAG = BannerLoopActivity.class.getSimpleName();

    ArrayList<AdminItems> adminItems;
    public static ArrayList<HashMap<String, Object>> producList = new ArrayList<>();
    int firstIndex;
    int lastIndex;
    int listSize;
    String strFirstIndex="",strSecondIndex="";
    boolean isUpdated = false;
    JSONArray adminArray;
    AdminAdapter adapter;
    RecyclerView recyclerView;
    EditText etFirstIndex,etSecondIndex;
    ProgressDialog dialog;
    TextView pkgNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_loop);

        recyclerView = findViewById(R.id.admin_rv);
        pkgNo = findViewById(R.id.tvPkgSize);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        etFirstIndex = findViewById(R.id.etFirstIndex);
        etSecondIndex = findViewById(R.id.etSecondIndex);

        adminArray = new JSONArray();
        adminItems = new ArrayList<>();

        dialog = new ProgressDialog(BannerLoopActivity.this);

        getAllPackageList();

        findViewById(R.id.admin_json).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adminItems.clear();
                strFirstIndex = etFirstIndex.getText().toString().trim();
                strSecondIndex = etSecondIndex.getText().toString().trim();
                if(strFirstIndex.equals("")){
                    etFirstIndex.setError("Please Enter First Index:");
                }else if(strSecondIndex.equals("")){
                    etSecondIndex.setError("Please Enter Second Index:");
                }else{
                    firstIndex = Integer.parseInt(strFirstIndex);
                    lastIndex = Integer.parseInt(strSecondIndex);
                    showFirestore(firstIndex,lastIndex);
                }

            }
        });

    }

    private void getAllPackageList() {
        dialog.setMessage("Please wait getting pkg size...!!!");
        dialog.setCancelable(false);
        dialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference uNames = db.collection("AdminNameManagement");
        uNames.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    pkgNo.setText("Total Package No: "+ Objects.requireNonNull(task.getResult()).getDocuments().size());
                    dialog.dismiss();
                }
            }
        });
    }

    public void showFirestore(int firstIndex,int lastIndex) {

        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference uNames = db.collection("AdminNameManagement");


        listSize = lastIndex-firstIndex;
        uNames.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "onEvent: error occured");
                }

                if (snapshots.isEmpty()) {
                    Log.e(TAG, "onEvent: empty snap");
                } else {
                    List<DocumentSnapshot> doc = snapshots.getDocuments();
                    //for (int i = 0; i < doc.size(); i++) {
                    for (int i = firstIndex; i < lastIndex; i++) {
                        String packageName = doc.get(i).getId();
                        Map<String, Object> appDetails = doc.get(i).getData();
                        String adminName = (String) appDetails.get("AdminName");
                        String aepsDriverActivityUpdate = (String) appDetails.get("AepsDriverActivityUpdate");
                        String appCode = (String) appDetails.get("AppCode");
                        String appName = (String) appDetails.get("AppName");
                        String createdBy = (String) appDetails.get("CreatedBy");
                        String customTheme = (String) appDetails.get("CustomTheme");
                        String mainApp = (String) appDetails.get("MainApp");
                        String uniqueId = (String) appDetails.get("UniqueId");
                        String versionName = (String) appDetails.get("VersionName");

                        isUpdated = adminItems.size() == listSize;

                        if (adminItems.size() < listSize) {
                            adminItems.add(new AdminItems(packageName, adminName, appCode, appName, createdBy, mainApp, uniqueId, versionName,aepsDriverActivityUpdate,customTheme));
                        }

                    }

                    try {
                        for (int i = 0; i < adminItems.size(); i++) {
                            if (!isUpdated) {

                                updateBannerList(adminItems.get(i).packageName);
                            }
                        }
                    } catch (Exception jsonException) {
                        jsonException.printStackTrace();
                    }

                    adapter = new AdminAdapter(BannerLoopActivity.this, adminItems);
                    recyclerView.setAdapter(adapter);

                }


            }
        });


    }

    private void updateBannerList(String packageName) {

            FirebaseApp.initializeApp(getApplicationContext());
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("AdminNameManagement").document(packageName);

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete (@NonNull Task<DocumentSnapshot> task) {
                    Log.e(TAG, "onComplete: executed");
                    if (task.isSuccessful()) {
                        if (task.getResult().getData() != null) {
                            Log.e(TAG, "onComplete: has data status " + task.getResult().getData());
                            Map<String, Object> packageMap = task.getResult().getData();
                            producList.clear();
                            if(packageMap.get("BannerList") ==null){
                                newUpdateBannerList(producList,packageName);
                            }else{
                                newUpdateBannerList((ArrayList<HashMap<String, Object>>) packageMap.get("BannerList"),packageName);
                            }
                        }
                    }

                }
            });
    }

    private void newUpdateBannerList(ArrayList<HashMap<String, Object>> producList, String packageName) {

        ArrayList<HashMap<String, Object>> bannerListNew = new ArrayList<>();

        for(int i=0;i<producList.size();i++){
            if(!producList.get(i).get("Url").equals("")){
                bannerListNew.add(producList.get(i));
            }
        }

        HashMap<String, Object> myMap = new HashMap<>();

        myMap.put("Type","image");
        myMap.put("Url","https://firebasestorage.googleapis.com/v0/b/iserveumainapp/o/AndroidBanner%2Fimages%2Fimage-4.png?alt=media&token=94842c43-d285-4ca9-a14c-c1ade72e639e");

        bannerListNew.add(myMap);
        Map<String,Object>bannerListMap = new HashMap<>();
        bannerListMap.put("BannerList",bannerListNew);




        if (packageName.equals("com.isu.isuiserveu")) {
            //Do Nothing
        } else {
            Log.d(TAG, "updateDatabase: ______________" + ": 2");
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference uNames = db.collection("AdminNameManagement");

            uNames.document(packageName).update(bannerListMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e(TAG, "DocumentSnapshot successfully written!" + packageName);
                            dialog.dismiss();
                            Toast.makeText(BannerLoopActivity.this, "BannerList Updated..!!", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error writing document", e);
                            dialog.dismiss();
                            Toast.makeText(BannerLoopActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }


}