package com.isu.admin;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BannerActivity extends AppCompatActivity {
    public static ArrayList<HashMap<String, Object>> producList = new ArrayList<>();
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        dialog = new ProgressDialog(this);

        findViewById(R.id.btnBanner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBannerOnFireBase();
            }
        });


    }
    private void addBannerOnFireBase() {
        dialog.setMessage("Please wait..!!!");
        dialog.setCancelable(false);
        dialog.show();

        ArrayList<String>packNameList = new ArrayList<>();
        packNameList.add("com.prepaid.queueoff");
//        packNameList.add("");

        checkParam(packNameList);
    }

    private void checkParam(ArrayList<String> packNameList2) {

        for(int i=0;i<packNameList2.size();i++){
                FirebaseApp.initializeApp(getApplicationContext());
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("AdminNameManagement").document("com.prepaid.queueoff");
            int finalI = i;
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
                                     updateBannerList(producList,packNameList2.get(finalI));
                                 }else{
                                     updateBannerList((ArrayList<HashMap<String, Object>>) packageMap.get("BannerList"),packNameList2.get(finalI));
                                 }
                            }
                        }

                    }
                });

        }

    }

    private void updateBannerList(ArrayList<HashMap<String, Object>> bannerList, String pkgName) {
        ArrayList<HashMap<String, Object>> bannerListNew = new ArrayList<>();

        for(int i=0;i<bannerList.size();i++){
            if(!bannerList.get(i).get("Url").equals("")){
                bannerListNew.add(bannerList.get(i));
            }
        }

        HashMap<String, Object> myMap = new HashMap<>();

        myMap.put("Type","image");
        myMap.put("Url","https://firebasestorage.googleapis.com/v0/b/iserveumainapp/o/AndroidBanner%2Fimages%2Fimage-1.png?alt=media&token=82830a52-9213-420d-a874-92373438c875");

                bannerListNew.add(myMap);
                Map<String,Object>bannerListMap = new HashMap<>();
                bannerListMap.put("BannerList",bannerListNew);

                FirebaseFirestore db2 = FirebaseFirestore.getInstance();
                CollectionReference uNames2 = db2.collection("AdminNameManagement");
                uNames2.document(pkgName).update(bannerListMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e(TAG, "DocumentSnapshot successfully written!" + pkgName);
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "BannerList Updated...!!", Toast.LENGTH_SHORT).show();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(BannerActivity.this, "Error:"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }





