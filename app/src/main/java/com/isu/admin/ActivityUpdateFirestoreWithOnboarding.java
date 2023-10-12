package com.isu.admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityUpdateFirestoreWithOnboarding extends AppCompatActivity {
    private static final String TAG = ActivityUpdateFirestoreWithOnboarding.class.getSimpleName();
    ProgressBar progressBar;
    Button buttonAddApp;
    SwitchCompat swOnOff;
    ArrayList<AdminItemsOnboarding> adminItems;
    JSONArray jsonArray;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_firestore_with_onboarding);

        progressBar = findViewById(R.id.update_progress);
        buttonAddApp = findViewById(R.id.btn_add_app);

        String s = loadJson(ActivityUpdateFirestoreWithOnboarding.this);
        adminItems = new ArrayList<>();
        jsonArray = new JSONArray();
        try {
            jsonArray = new JSONArray(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adminItems = convert(jsonArray);



        buttonAddApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    public void update(String packageName) {
        progressBar.setVisibility(View.VISIBLE);
        findViewById(R.id.update_list).setVisibility(View.GONE);

        Map<String, Object> onboardMap = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        ArrayList<String> alAdmin = new ArrayList<>();
        ArrayList<String> alUsers = new ArrayList<>();

        alAdmin.add("demoisu");
        alUsers.add("itpl");

        onboardMap.put("Admins", alAdmin);
        onboardMap.put("Users", alUsers);
        map.put("BetaTesting", onboardMap);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference uNames = db.collection("AdminNameManagement");
        uNames.document(packageName).update(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.e(TAG, "DocumentSnapshot successfully written!");
                        progressBar.setVisibility(View.GONE);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error writing document", e);
            }
        });


    }

    public static String loadJson(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("adminNameWithOnboarding.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public ArrayList<AdminItemsOnboarding> convert(JSONArray array) {
        ArrayList<AdminItemsOnboarding> items = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject object = array.getJSONObject(i);
                String packageName = object.getString("Package");
                String admin = object.getString("AdminName");
                String appCode = object.getString("AppCode");
                String appName = object.getString("AppName");
                String createdBy = object.getString("CreatedBy");
                String mainApp = object.getString("MainApp");
                String uniqueId = object.getString("UniqueId");
                String versionName = object.getString("VersionName");

                JSONObject objectOnboard = new JSONObject(object.getString("Onboard"));
                JSONObject objectOnboardAadhaar = new JSONObject(objectOnboard.getString("Aadhaar"));
                JSONObject objectOnboardBasicInfo = new JSONObject(objectOnboard.getString("BasicInfo"));
                JSONObject objectOnboardEmail = new JSONObject(objectOnboard.getString("Email"));
                JSONObject objectOnboardMobile = new JSONObject(objectOnboard.getString("Mobile"));
                JSONObject objectOnboardPan = new JSONObject(objectOnboard.getString("Pan"));
                String ShowOnBoard = objectOnboard.getString("ShowOnBoard");

                String basicInfoAvailable, basicInfoSkip,
                        aadhaarAvailable, aadhaarSkip, aadhaarSequence,
                        emailAvailable, emailSkip,
                        mobileAvailable, mobileSkip,
                        panAvailable, panSkip, panSequence;

                basicInfoAvailable = objectOnboardBasicInfo.getString("Available");
                basicInfoSkip = objectOnboardBasicInfo.getString("Skip");

                aadhaarAvailable = objectOnboardAadhaar.getString("Available");
                aadhaarSkip = objectOnboardAadhaar.getString("Skip");
                aadhaarSequence = objectOnboardAadhaar.getString("Sequence");

                emailAvailable = objectOnboardEmail.getString("Available");
                emailSkip = objectOnboardEmail.getString("Skip");

                mobileAvailable = objectOnboardMobile.getString("Available");
                mobileSkip = objectOnboardMobile.getString("Skip");

                panAvailable = objectOnboardPan.getString("Available");
                panSkip = objectOnboardPan.getString("Skip");
                panSequence = objectOnboardPan.getString("Sequence");


                items.add(new AdminItemsOnboarding(packageName, admin, appCode, appName, createdBy, mainApp, uniqueId, versionName,
                        basicInfoAvailable, basicInfoSkip,
                        aadhaarAvailable, aadhaarSkip, aadhaarSequence,
                        emailAvailable, emailSkip,
                        mobileAvailable, mobileSkip,
                        panAvailable, panSkip, panSequence,
                        ShowOnBoard));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return items;
    }

    public void addFirestore(ArrayList<AdminItems> list) {

        dialog = new ProgressDialog(ActivityUpdateFirestoreWithOnboarding.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference uNames = db.collection("AdminNameManagement");

        for (int i = 0; i < list.size(); i++) {
            AdminItems current = list.get(i);

            String packageName = current.getPackageName();

            Map<String, Object> appDetails = new HashMap<>();
            appDetails.put("AdminName", current.getAdmin());
            appDetails.put("AppCode", current.getAppCode());
            appDetails.put("AppName", current.getAppName());
            appDetails.put("CreatedBy", current.getCreatedBy());
            appDetails.put("MainApp", current.getMainApp());
            appDetails.put("UniqueId", current.getUniqueId());
            appDetails.put("VersionName", current.getVersionName());
            appDetails.put("Onboard", current.getVersionName());

            uNames.document(packageName).set(appDetails)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e(TAG, "DocumentSnapshot successfully written!" + appDetails);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Error writing document", e);
                }
            });


        }

        dialog.dismiss();

    }
}