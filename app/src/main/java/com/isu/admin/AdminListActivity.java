package com.isu.admin;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminListActivity extends AppCompatActivity {
    private static final String TAG = AdminListActivity.class.getSimpleName();
    ArrayList<AdminItems> adminItems;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list);

        recyclerView = findViewById(R.id.admin_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        etFirstIndex = findViewById(R.id.etFirstIndex);
        etSecondIndex = findViewById(R.id.etSecondIndex);



        adminArray = new JSONArray();



        findViewById(R.id.admin_json).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    public void showFirestore(int firstIndex,int lastIndex) {
        adminArray = new JSONArray();
        dialog = new ProgressDialog(AdminListActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference uNames = db.collection("AdminNameManagement");
        adminItems = new ArrayList<>();

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

//                        try {
//                            Map<String, Object> onboardMap = new HashMap<>();
//                            Map<String, Object> A910onboardMap = new HashMap<>();
//                            Map<String, Object> mapDeviceSetup = new HashMap<>();
//                            Map<String, Object> mapWalletTopup = new HashMap<>();
//                            Map<String, Object> mapDevice = new HashMap<>();
//
//                            ArrayList<String> alAdmin = new ArrayList<>();
//                            ArrayList<String> alUsers = new ArrayList<>();
//
//                            ArrayList<String> alA910Admin = new ArrayList<>();
//                            ArrayList<String> alA910Users = new ArrayList<>();
//
//                            alAdmin.add(adminName);
//                            alUsers.add("");
//
//                            alA910Admin.add("");
//                            alA910Users.add("");
//
//                            JSONObject object = new JSONObject();
//                            onboardMap.put("Admins", alAdmin);
//                            onboardMap.put("Users", alUsers);
//
//                            A910onboardMap.put("Admins", alA910Admin);
//                            A910onboardMap.put("Users", alA910Users);
//
//                            //bELOW 3 MAPS ARE VALUE OF DeviceSetup
//                            mapDeviceSetup.put("MOREFUN", onboardMap);
//                            mapDeviceSetup.put("PAXA910", A910onboardMap);
//                            mapDeviceSetup.put("PAXD180", onboardMap);
//                            mapDevice.put("DeviceSetup", mapDeviceSetup);
//
//
//                            //bELOW 3 MAPS ARE VALUE OF WalletCashout
//                            mapWalletTopup.put("CashDeposit", onboardMap);
//                            mapWalletTopup.put("BankChallan", onboardMap);
//                            mapWalletTopup.put("UPI", onboardMap);
//
//                            mapDevice.put("WalletTopup", mapWalletTopup);
//
//                            if (packageName.equals("com.isu.isuiserveu")) {
//                                //Do Nothing
//                            } else {
//                                FirebaseFirestore db = FirebaseFirestore.getInstance();
//                                CollectionReference uNames = db.collection("AdminNameManagement");
//                                int finalI = i;
//                                uNames.document(packageName).update(mapDevice)
//                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void aVoid) {
//                                                Log.e(TAG, "DocumentSnapshot successfully written!" + packageName);
//
//                                            }
//                                        }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Log.e(TAG, "Error writing document", e);
//                                    }
//                                });
//                            }
//
//                            //adminArray.put(object);
//                            Log.e(TAG, "onEvent: json from array at index " + i + " ::: " + adminArray.get(i));
//
//
//                        } catch (JSONException jsonException) {
//                            jsonException.printStackTrace();
//                        }

                    }

                    try {
                        for (int i = 0; i < adminItems.size(); i++) {
                            if (!isUpdated) {
                               // updateDatabase(adminItems.get(i).admin, adminItems.get(i).packageName);
                               updateAepsOption(adminItems.get(i).admin, adminItems.get(i).packageName);
                            }
                        }
                    } catch (Exception jsonException) {
                        jsonException.printStackTrace();
                    }

                    adapter = new AdminAdapter(AdminListActivity.this, adminItems);
                    recyclerView.setAdapter(adapter);
                    dialog.dismiss();
                }


            }
        });


    }

    private void updateAepsOption(String admin, String packageName) {

        Map<String,Object>updateAepsOption = new HashMap<>();
         updateAepsOption.put("CustomTheme","#00C3D7");

        if (packageName.equals("com.isu.isuiserveu")) {
            //Do Nothing
        } else {
            Log.d(TAG, "updateDatabase: ______________" + ": 2");
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference uNames = db.collection("AdminNameManagement");
//            int finalI = i;
            uNames.document(packageName).update(updateAepsOption)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "updateDatabase: ______________" + ": 3");
                            Log.e(TAG, "DocumentSnapshot successfully written!" + packageName);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Error writing document", e);
                }
            });
        }
    }

    private void updateDatabase(String adminName, String packageName) {
        Log.d(TAG, "updateDatabase: ______________"+": 1");
        Map<String, Object> onboardMap = new HashMap<>();
//        Map<String, Object> A910onboardMap = new HashMap<>();
        Map<String, Object> mapDeviceSetup = new HashMap<>();
//        Map<String, Object> mapWalletTopup = new HashMap<>();
        Map<String, Object> mapDevice = new HashMap<>();

        ArrayList<String> alAdmin = new ArrayList<>();
        ArrayList<String> alUsers = new ArrayList<>();

//        ArrayList<String> alA910Admin = new ArrayList<>();
//        ArrayList<String> alA910Users = new ArrayList<>();

        alAdmin.add(adminName);
        alUsers.add("");

//        alA910Admin.add("");
//        alA910Users.add("");

        JSONObject object = new JSONObject();
        onboardMap.put("Admins", alAdmin);
        onboardMap.put("Users", alUsers);

//        A910onboardMap.put("Admins", alA910Admin);
//        A910onboardMap.put("Users", alA910Users);

        //bELOW 3 MAPS ARE VALUE OF DeviceSetup
        mapDevice.put("UnifiedAePSEnable", onboardMap);
//        mapDeviceSetup.put("PAXA910", A910onboardMap);
//        mapDeviceSetup.put("PAXD180", onboardMap);
//        mapDevice.put("DeviceSetup", mapDeviceSetup);


        //bELOW 3 MAPS ARE VALUE OF WalletCashout
//        mapWalletTopup.put("CashDeposit", onboardMap);
//        mapWalletTopup.put("BankChallan", onboardMap);
//        mapWalletTopup.put("UPI", onboardMap);
//
//        mapDevice.put("WalletTopup", mapWalletTopup);

        if (packageName.equals("com.isu.isuiserveu")) {
            //Do Nothing
        } else {
            Log.d(TAG, "updateDatabase: ______________"+": 2");
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference uNames = db.collection("AdminNameManagement");
//            int finalI = i;
            uNames.document(packageName).update(mapDevice)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "updateDatabase: ______________"+": 3");
                            Log.e(TAG, "DocumentSnapshot successfully written!" + packageName);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Error writing document", e);
                }
            });
        }

        //adminArray.put(object);
//        try {
//            Log.e(TAG, "onEvent: json from array at index " + i + " ::: " + adminArray.get(i));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

}