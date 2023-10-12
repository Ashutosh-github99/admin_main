package com.isu.admin;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminHolder> {
    private static final String TAG = AdminAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<AdminItems> items;

    String admin1, appCode1, appName1, created1, mainApp1, uniqueID1, versionName1;

    public AdminAdapter(Context context, ArrayList<AdminItems> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public AdminHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.admin_item, parent, false);
        return new AdminHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminHolder holder, int position) {
        AdminItems current = items.get(position);

        String appName = current.getAppName();
        String adminName = current.getAdmin();
        String packageName = current.getPackageName();
        String appCode = current.getAppCode();
        String versionName = current.getVersionName();

        holder.appName.setText(appName + "\n(" + packageName + ")");
        holder.appCode.setText(appCode);
        holder.versionName.setText(versionName);
        holder.adminName.setText(adminName);

        String mainApp = current.getMainApp();
        if (!mainApp.equals(packageName)) {
            holder.mainAppLL.setVisibility(View.VISIBLE);
            holder.mainApp.setText(mainApp);
        }

        String created = current.getCreatedBy();
        if (!created.equals(adminName)) {
            holder.createdLL.setVisibility(View.VISIBLE);
            holder.createdBy.setText(created);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class AdminHolder extends RecyclerView.ViewHolder {
        TextView appName, adminName, appCode, createdBy, mainApp, versionName;
        LinearLayout createdLL, mainAppLL, itemContainer;

        public AdminHolder(@NonNull View view) {
            super(view);
            appName = view.findViewById(R.id.item_name);
            adminName = view.findViewById(R.id.item_admin);
            appCode = view.findViewById(R.id.item_app_code);
            createdBy = view.findViewById(R.id.item_created);
            mainApp = view.findViewById(R.id.item_main_app);
            versionName = view.findViewById(R.id.item_version);
            createdLL = view.findViewById(R.id.item_created_container);
            mainAppLL = view.findViewById(R.id.item_main_app_container);
            itemContainer = view.findViewById(R.id.item_container);

            itemContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (context.getClass().getName().contains("AdminListActivity")) {
                        Dialog myDialog = new Dialog(context);
                        myDialog.setCancelable(false);
                        myDialog.setContentView(R.layout.details_popup);
                        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        myDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                                WindowManager.LayoutParams.MATCH_PARENT);
                        myDialog.show();

                        AdminItems current = items.get(getAdapterPosition());

                        TextInputEditText adminET, appCodeET, appNameET, createdET, mainAppET, uniqueET, versionET;
                        Button update;
                        TextView cancel, title;

                        title = myDialog.findViewById(R.id.details_title);
                        adminET = myDialog.findViewById(R.id.details_admin);
                        appCodeET = myDialog.findViewById(R.id.details_code);
                        appNameET = myDialog.findViewById(R.id.details_name);
                        createdET = myDialog.findViewById(R.id.details_created);
                        mainAppET = myDialog.findViewById(R.id.details_main);
                        uniqueET = myDialog.findViewById(R.id.details_unique);
                        versionET = myDialog.findViewById(R.id.details_version);

                        title.setText(current.getPackageName());
                        adminET.setText(current.getAdmin());
                        appCodeET.setText(current.getAppCode());
                        appNameET.setText(current.getAppName());
                        createdET.setText(current.getCreatedBy());
                        mainAppET.setText(current.getMainApp());
                        uniqueET.setText(current.getUniqueId());
                        versionET.setText(current.getVersionName());

                        update = myDialog.findViewById(R.id.details_update);
                        cancel = myDialog.findViewById(R.id.details_cancel);

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                myDialog.dismiss();
                            }
                        });

                        update.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Map<String, Object> appDetails = new HashMap<>();
                                appDetails.put("AdminName", adminET.getText().toString());
                                appDetails.put("AppCode", appCodeET.getText().toString());
                                appDetails.put("AppName", appNameET.getText().toString());
                                appDetails.put("CreatedBy", createdET.getText().toString());
                                appDetails.put("MainApp", mainAppET.getText().toString());
                                appDetails.put("UniqueId", uniqueET.getText().toString());
                                appDetails.put("VersionName", versionET.getText().toString());

                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                CollectionReference uNames = db.collection("AdminNameManagement");

                                uNames.document(current.getPackageName()).update(appDetails)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.e(TAG, "DocumentSnapshot successfully written!" + appDetails);
                                                myDialog.dismiss();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "Error writing document", e);
                                        Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }
                        });


                    }
                }
            });

        }
    }
}
