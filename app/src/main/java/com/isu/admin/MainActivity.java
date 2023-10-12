package com.isu.admin;

import static com.isu.admin.Constants.adminName_;
import static com.isu.admin.Constants.packageName_;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    ArrayList<AdminItems> adminItems;
    JSONArray jsonArray;
    AdminAdapter adapter;
    RecyclerView recyclerView;

    ProgressDialog dialog;

    TextInputEditText packageEt, adminET, appCodeET, appNameET, createdET, mainAppET, uniqueET, versionET;

    Button buttonUpdateMorefun, updateDeviceSetUp;

    public static String loadJson(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("adminName.json");
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String s = loadJson(MainActivity.this);

        recyclerView = findViewById(R.id.main_rv);

        packageEt = findViewById(R.id.home_package);
        adminET = findViewById(R.id.home_admin);
        appCodeET = findViewById(R.id.home_code);
        appNameET = findViewById(R.id.home_name);
        createdET = findViewById(R.id.home_created);
        mainAppET = findViewById(R.id.home_main);
        uniqueET = findViewById(R.id.home_unique);
        versionET = findViewById(R.id.home_version);
        updateDeviceSetUp = findViewById(R.id.updateDeviceSetUp);

        buttonUpdateMorefun = findViewById(R.id.update_morefun);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adminItems = new ArrayList<>();
        jsonArray = new JSONArray();
        try {
            jsonArray = new JSONArray(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adminItems = convert(jsonArray);

        adapter = new AdminAdapter(MainActivity.this, adminItems);
        recyclerView.setAdapter(adapter);
//device setup
        updateDeviceSetUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDeviceSetUpPackageAdminList();
            }
        });

        findViewById(R.id.home_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UpdatePackageActivity.class));
            }
        });
        findViewById(R.id.btn_training).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTrainingData();
            }
        });

        findViewById(R.id.button_add_to_firesore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFirestore(adminItems);
            }
        });
        findViewById(R.id.show_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AdminListActivity.class));
            }
        });
//update beta testing
        buttonUpdateMorefun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createMorefunPackageAdminList();
            }
        });
//unified_aeps enable
        findViewById(R.id.unified_aeps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatdUnifiedAeps();
            }
        });
        //update wallet topup
        findViewById(R.id.btn_wallet_Topup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateWalletTopup();
            }
        });

        //update product
        findViewById(R.id.btn_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProduct();
            }
        });

        findViewById(R.id.btn_bannerList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBannerList();
            }
        });

        findViewById(R.id.btn_zendexForm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zendexFormData();
            }
        });

        //Add Manually
        findViewById(R.id.home_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new ProgressDialog(MainActivity.this);
                dialog.setMessage("Loading...");
                dialog.setCancelable(false);
                dialog.show();

                Map<String, Object> appDetails = new HashMap<>();
                appDetails.put("AdminName", adminET.getText().toString());
                appDetails.put("AppCode", appCodeET.getText().toString());
                appDetails.put("AppName", appNameET.getText().toString());
                appDetails.put("CreatedBy", createdET.getText().toString());
                appDetails.put("MainApp", mainAppET.getText().toString());
                appDetails.put("UniqueId", uniqueET.getText().toString());
                appDetails.put("VersionName", versionET.getText().toString());
                appDetails.put("Onboard", getOnboardingMap());

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference uNames = db.collection("AdminNameManagement");

                uNames.document(packageEt.getText().toString()).set(appDetails)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e(TAG, "successfully written!" + appDetails);
                                Toast.makeText(MainActivity.this, "successfully written!", Toast.LENGTH_SHORT).show();
                                adminET.setText("");
                                appCodeET.setText("");
                                appNameET.setText("");
                                createdET.setText("");
                                mainAppET.setText("");
                                uniqueET.setText("");
                                versionET.setText("");
                                packageEt.setText("");
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error writing document", e);
                        Toast.makeText(MainActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

            }
        });

    }

    private void UpdateWalletTopup() {

        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("plz wait..!!");
        dialog.setCancelable(false);
        dialog.show();

        List<String> listPackageName = new ArrayList<>();
        listPackageName.add(packageName_);
        List<String> listAdminName = new ArrayList<>();
        listAdminName.add(adminName_);

        updateWallet(listAdminName,listPackageName);
    }

    private void updateProduct() {

        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("plz wait..!!");
        dialog.setCancelable(false);
        dialog.show();

        List<String> listPackageName = new ArrayList<>();
        listPackageName.add(packageName_);
        List<String> listAdminName = new ArrayList<>();
        listAdminName.add(adminName_);


        for (int i=0 ;i<listPackageName.size() ;i++){
            {
                Map<String, Object> giftPhyMap = new HashMap<>();
                Map<String, Object> giftVerMap = new HashMap<>();
                Map<String, Object> gprPhyMap = new HashMap<>();
                Map<String, Object> gprVerMap = new HashMap<>();

                Map<String, Object> giftMap = new HashMap<>();
                Map<String, Object> gprMap = new HashMap<>();

                Map<String, Object> inMap =new HashMap<>();

                Map<String, Object> map =new HashMap<>();

                /*ArrayList<String> alAdmin = new ArrayList<>();
                ArrayList<String> alUsers = new ArrayList<>();

                alAdmin.add(listAdminName.get(i));
                alUsers.add("");*/

                giftPhyMap.put("pid", 936);
                giftPhyMap.put("purl", "https://ecom.iserveu.tech/#/products/productdetails/gift-card-physical");

                giftVerMap.put("pid", 932);
                giftVerMap.put("purl", "https://ecom.iserveu.tech/#/products/productdetails/gift-card-virtual");

                gprPhyMap.put("pid", 931);
                gprPhyMap.put("purl", "https://ecom.iserveu.tech/#/products/productdetails/gpr-physical");

                gprVerMap.put("pid", 936);
                gprVerMap.put("purl", "https://ecom.iserveu.tech/#/products/productdetails/gpr-virtual");

                giftMap.put("physical", giftPhyMap);
                giftMap.put("virtual", giftVerMap);
                gprMap.put("physical", gprPhyMap);
                gprMap.put("virtual", gprVerMap);

                inMap.put("corporateID", "220");
                inMap.put("giftcard", giftMap);
                inMap.put("gpr", gprMap);
                map.put("Products", inMap);

                int finalI1 = i;//as i cant go into firebase instance
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference uName = db.collection("AdminNameManagement");
                uName.document(listPackageName.get(i)).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.e(TAG, "DocumentSnapshot successfully written!" + listPackageName.get(finalI1));
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Products successfully Added !!!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error writing document", e);
                            }
                        });




            }
        }


    }

    private void updateWallet(List<String> listAdminName, List<String> listPackageName) {

        for (int i=0 ; i<listPackageName.size(); i++){
            Map<String, Object> onboardMap = new HashMap<>();
            Map<String, Object> map = new HashMap<>();
            Map<String, Object> inMap =new HashMap<>();

            ArrayList<String> alAdmin = new ArrayList<>();
            ArrayList<String> alUsers = new ArrayList<>();

            alAdmin.add(listAdminName.get(i));
            alUsers.add("");

            onboardMap.put("Admins", alAdmin);
            onboardMap.put("Users", alUsers);

            inMap.put("BankChallan", onboardMap);
            inMap.put("CashDiposit", onboardMap);
            inMap.put("UPI", onboardMap);

            map.put("WalletTopup", inMap);

            int finalI = i;
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference uName = db.collection("AdminNameManagement");
            uName.document(listPackageName.get(i)).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.e(TAG, "DocumentSnapshot successfully written!" + listPackageName.get(finalI));
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "WalletTopup successfully Added !!!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error writing document", e);
                        }
                    });




        }
    }

    private void addTrainingData() {
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();
        List<String> listPackageName = new ArrayList<>();
        listPackageName.add(packageName_);
        updateTraingData(listPackageName);

    }

    private void updateTraingData(List<String> pkgNameList){
        for (int j = 0; j < pkgNameList.size(); j++) {
            HashMap tranningMap = new HashMap();
            HashMap typesTranningMap = new HashMap();
            HashMap<String, Object> myMap = new HashMap<>();
            myMap.put("content","Aadhar Enable Payment System is a bank-led model created by National Payments Corporation of India(NPCI) that allows the customer to use Aadhaar as an identity to do the financial transactions which work at the POS(Point of Sale) through UIDAI or Aadhaar authentication of the customer. ");
            myMap.put("title","AEPS");
            myMap.put("video","https://www.youtube.com/embed/Z6nM-8n1vKA");
            HashMap<String, Object> myMap1 = new HashMap<>();
            myMap1.put("content","");
            myMap1.put("title","");
            myMap1.put("video","https://www.youtube.com/embed/daersgc6xTc");
            HashMap<String, Object> myMap2 = new HashMap<>();
            myMap2.put("content","");
            myMap2.put("title","");
            myMap2.put("video","https://www.youtube.com/embed/wYfmZGGJjw8");
            HashMap<String, Object> myMap3 = new HashMap<>();
            myMap3.put("content","");
            myMap3.put("title","");
            myMap3.put("video","https://www.youtube.com/embed/m0lsl469A0Q");
            HashMap<String, Object> myMap4 = new HashMap<>();
            myMap4.put("content","");
            myMap4.put("title","");
            myMap4.put("video","https://www.youtube.com/embed/qsddaKZuSac");
            ArrayList aepsList = new ArrayList();
            aepsList.add(0,myMap);
            aepsList.add(1,myMap1);
            aepsList.add(2,myMap2);
            aepsList.add(3,myMap3);
            aepsList.add(4,myMap4);
            Log.e(TAG, "updateAepsOption: "+aepsList);
            HashMap<String, Object> bbpsMap = new HashMap<>();
            bbpsMap.put("content","BBPS bill payment system that offers interoperable and accessible bill payment services to customers through a network of agents, enabling multiple payment modes, and providing instant confirmation of receipt of payment. The Bharat Bill payment system is a Reserve Bank of India (RBI) conceptualised system driven by National Payments Corporation of India (NPCI). It is a one-stop ecosystem for payment of all bills providing an interoperable and accessible “Anytime Anywhere” Bill payment service to all customers across India with certainty, reliability and safety of transactions.");
            bbpsMap.put("title","BBPS");
            bbpsMap.put("video","https://www.youtube.com/embed/88KlHeKKyB4");
            HashMap<String, Object> bbpsMap1 = new HashMap<>();
            bbpsMap1.put("content","");
            bbpsMap1.put("title","");
            bbpsMap1.put("video","https://www.youtube.com/embed/Yx8wpYQZNXo");
            ArrayList bbpsList = new ArrayList();
            bbpsList.add(0,bbpsMap);
            bbpsList.add(1,bbpsMap1);
            HashMap<String, Object> cobMap = new HashMap<>();
            cobMap.put("content","Common Onboarding is a KYC process where the retailer needs to provide documents such as Aadhar Card & PAN card along with other basic information to have a secure onboarding, required to start using the services. ");
            cobMap.put("title","Common onboarding  ");
            cobMap.put("video","https://player.vimeo.com/video/735351138?h=e216eb7849&amp;badge=0&amp;autopause=0&amp;player_id=0&amp;app_id=58479%22");
            HashMap<String, Object> cobMap1 = new HashMap<>();
            cobMap1.put("content","");
            cobMap1.put("title","");
            cobMap1.put("video","https://www.youtube.com/embed/7X85JWNaGL8");
            ArrayList cobList = new ArrayList();
            cobList.add(0,cobMap);
            cobList.add(1,cobMap1);
            HashMap<String, Object> dematMap = new HashMap<>();
            dematMap.put("content","A Demat account is a necessary account to hold financial securities in a digital form and to trade shares in the share market.    It is a short for dematerialisation account and makes the process of holding investments like shares, bonds, government securities, Mutual Funds, Insurance and ETFs easier, doing away the hassles of physical handling and maintenance of paper shares and related documents. In India to invest in the stock market, it is mandatory to open a Demat account    ");
            dematMap.put("title","Demat  ");
            dematMap.put("video","https://www.youtube.com/embed/b3-hkGw2Yo8");
            ArrayList dematList = new ArrayList();
            dematList.add(0,dematMap);
            HashMap<String, Object> deviceMap = new HashMap<>();
            deviceMap.put("content","A financial transaction device means an instrument or device that can be used to obtain cash, services or to make financial payments.");
            deviceMap.put("title","Device");
            deviceMap.put("video","https://www.youtube.com/embed/daersgc6xTc");
            HashMap<String, Object> deviceMap1 = new HashMap<>();
            deviceMap1.put("content","");
            deviceMap1.put("title","");
            deviceMap1.put("video","https://www.youtube.com/embed/m0lsl469A0Q");
            HashMap<String, Object> deviceMap2 = new HashMap<>();
            deviceMap2.put("content","");
            deviceMap2.put("title","");
            deviceMap2.put("video","https://www.youtube.com/embed/VNXUt44xn28");
            HashMap<String, Object> deviceMap3 = new HashMap<>();
            deviceMap3.put("content","");
            deviceMap3.put("title","");
            deviceMap3.put("video","https://www.youtube.com/embed/8CORjLkI8_0");
            HashMap<String, Object> deviceMap4 = new HashMap<>();
            deviceMap4.put("content","");
            deviceMap4.put("title","");
            deviceMap4.put("video","https://www.youtube.com/embed/L6vSwiE3vvA");
            HashMap<String, Object> deviceMap5 = new HashMap<>();
            deviceMap5.put("content","");
            deviceMap5.put("title","");
            deviceMap5.put("video","https://www.youtube.com/embed/tLep6V9zo8s");
            HashMap<String, Object> deviceMap6 = new HashMap<>();
            deviceMap6.put("content","");
            deviceMap6.put("title","");
            deviceMap6.put("video","https://www.youtube.com/embed/qsddaKZuSac");
            ArrayList deviceList = new ArrayList();
            deviceList.add(0,deviceMap);
            deviceList.add(1,deviceMap1);
            deviceList.add(2,deviceMap2);
            deviceList.add(3,deviceMap3);
            deviceList.add(4,deviceMap4);
            deviceList.add(5,deviceMap5);
            deviceList.add(6,deviceMap6);
            HashMap<String, Object> dmtMap = new HashMap<>();
            dmtMap.put("content","Direct Money Transfer (DMT) is a unique product that can be used to send money instantly to any Bank’s account holder within India. Through this product any Indian citizen can walk in with cash-in-hand to any nearest retail outlet fill in few details and have the money transferred to loved ones, anywhere in the country.    ");
            dmtMap.put("title","DMT");
            dmtMap.put("video","https://www.youtube.com/embed/98VD9RDSWDo");
            ArrayList dmtList = new ArrayList();
            dmtList.add(0,dmtMap);
            HashMap<String, Object> insuMap = new HashMap<>();
            insuMap.put("content","LIC- LIC is a protection plan that provides financial protection to the life assured’s family in the event of his or her demise. ");
            insuMap.put("title","insurance");
            insuMap.put("video","https://www.youtube.com/embed/mjKmRg4Hszg");
            ArrayList insuList = new ArrayList();
            insuList.add(0,insuMap);
            HashMap<String, Object> matmMap = new HashMap<>();
            matmMap.put("content","Micro-ATMs are portable devices which enables BCs to provide banking services as Cash Withdrawal as well as Balance Enquiry. These are the terminals used to withdraw cash in remote locations where bank branches cannot reach. ");
            matmMap.put("title","MATM");
            matmMap.put("video","https://www.youtube.com/embed/tLep6V9zo8s");
            HashMap<String, Object> matmMa1 = new HashMap<>();
            matmMa1.put("content","");
            matmMa1.put("title","");
            matmMa1.put("video","https://www.youtube.com/embed/L6vSwiE3vvA");
            HashMap<String, Object> matmMa2 = new HashMap<>();
            matmMa2.put("content","");
            matmMa2.put("title","");
            matmMa2.put("video","https://www.youtube.com/embed/8CORjLkI8_0");
            ArrayList matmList = new ArrayList();
            matmList.add(0,matmMap);
            matmList.add(1,matmMa1);
            matmList.add(2,matmMa2);
            HashMap<String, Object> posMap = new HashMap<>();
            posMap.put("content","Mobile point-of-sale (MPOS) technology enables online payments through electronic devices such as tablets and mobile phone. With an MPOS app installed a wireless device can act as a cash register or an electronic point of sale terminal. The app is linked to the user’s bank account and the business or organization taking payment simply uses a digital reader to conduct the whole transaction. ");
            posMap.put("title","POS");
            posMap.put("video","https://player.vimeo.com/video/725620776?h=30c624c5f0&amp;badge=0&amp;autopause=0&amp;player_id=0&amp;app_id=58479");
            ArrayList posList = new ArrayList();
            posList.add(0,posMap);
            HashMap<String, Object> rechMap = new HashMap<>();
            rechMap.put("content","Recharge  system that offers interoperable and accessible bill payment services for mobile top up and post-paid service  to customers through a network of agents, enabling multiple payment modes, and providing instant confirmation of receipt of payment.    ");
            rechMap.put("title","Recharge");
            rechMap.put("video","https://www.youtube.com/embed/jjGhDBERh-o");
            ArrayList rechList = new ArrayList();
            rechList.add(0,rechMap);
            HashMap<String, Object> upiMap = new HashMap<>();
            upiMap.put("content","Unified Payments Interface (UPI) is a system that powers multiple bank accounts into a single mobile application (of any participating bank), merging several banking features, seamless fund routing & merchant payments into one hood. It also caters to the “Peer to Peer” collect request which can be scheduled and paid as per requirement and convenience");
            upiMap.put("title","UPI");
            upiMap.put("video","https://www.youtube.com/embed/dA6FHxUGCoY");
            HashMap<String, Object> upiMap1 = new HashMap<>();
            upiMap1.put("content","");
            upiMap1.put("title","");
            upiMap1.put("video","https://www.youtube.com/embed/kS6lwhn2U78");
            HashMap<String, Object> upiMap2 = new HashMap<>();
            upiMap2.put("content","");
            upiMap2.put("title","");
            upiMap2.put("video","https://www.youtube.com/embed/0O-2Y6JxpLU");
            ArrayList upiList = new ArrayList();
            upiList.add(0,upiMap);
            upiList.add(1,upiMap1);
            upiList.add(2,upiMap2);
            HashMap<String, Object> walletMap = new HashMap<>();
            walletMap.put("content","wallet 2 cash-out means now you can transfer money from your wallet 2 to any registered bank account of the retailer, with this feature you can transfer money using NEFT and IMPS. ");
            walletMap.put("title","Wallet 2 cash out  ");
            walletMap.put("video","https://www.youtube.com/embed/LN9gJBQGz5Q");
            HashMap<String, Object> walletMa1 = new HashMap<>();
            walletMa1.put("content","By Using Using wallet top-up now you can add money to your wallet 1");
            walletMa1.put("title","Wallet top-up? ");
            walletMa1.put("video","https://www.youtube.com/embed/yFqr4tsAIkos");
            ArrayList walletList = new ArrayList();
            walletList.add(0,walletMap);
            walletList.add(1,walletMa1);
            typesTranningMap.put("aeps",aepsList);
            typesTranningMap.put("bbps",bbpsList);
            typesTranningMap.put("common_onboarding",cobList);
            typesTranningMap.put("demat",dematList);
            typesTranningMap.put("device",deviceList);
            typesTranningMap.put("dmt",dmtList);
            typesTranningMap.put("insurance",insuList);
            typesTranningMap.put("matm",matmList);
            typesTranningMap.put("pos",posList);
            typesTranningMap.put("recharge",rechList);
            typesTranningMap.put("upi",upiList);
            typesTranningMap.put("wallet",walletList);
            tranningMap.put("Training",typesTranningMap);


            FirebaseFirestore db3 = FirebaseFirestore.getInstance();
            CollectionReference uNames3 = db3.collection("AdminNameManagement");
            int finalI = j;
            uNames3.document(pkgNameList.get(finalI)).update(tranningMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Zendex Form Updated...!!", Toast.LENGTH_SHORT).show();


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Error:"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void zendexFormData() {
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Please Wait.......!!!");
        dialog.setCancelable(false);
        dialog.show();

        List<String> listPackageName = new ArrayList<>();
        listPackageName.add(packageName_);

        updateZendexFormData(listPackageName);
    }

    private void updateZendexFormData(List<String> listPackageName) {
        for (int i = 0; i < listPackageName.size(); i++) {
            HashMap<String, Object> zendexMap = new HashMap<>();
            HashMap<String, Object> myMap = new HashMap<>();
            myMap.put("AEPS","6532773651353");
            myMap.put("BBPS","6534230517785");
            myMap.put("DEVICE","6544692337561");
            myMap.put("DMT","6504365909529");
            myMap.put("INSURANCE","6545181424025");
            myMap.put("MATM","6505880802329");
            myMap.put("OTHER","6563780923673");
            myMap.put("POS","6545020002329");
            myMap.put("RECHARGE","6537141974809");
            myMap.put("UPI","6512689016601");
            myMap.put("WALLET","7066033632025");
            zendexMap.put("zendeskForm",myMap);


            FirebaseFirestore db2 = FirebaseFirestore.getInstance();
            CollectionReference uNames2 = db2.collection("AdminNameManagement");
            int finalI = i;
            uNames2.document(listPackageName.get(i)).update(zendexMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e(TAG, "DocumentSnapshot successfully written!" + listPackageName.get(finalI));
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Zendex Form Updated...!!", Toast.LENGTH_SHORT).show();


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Error:"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void addBannerList() {
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Please Wait.......!!!");
        dialog.setCancelable(false);
        dialog.show();

        List<String> listPackageName = new ArrayList<>();
        listPackageName.add(packageName_);


        String imageUrl = "";
        String videoUrl = "";
        updateBannerList(listPackageName,imageUrl,videoUrl);
    }

    private void updateBannerList(List<String> listPackageName, String imageUrl, String videoUrl) {
        for (int i = 0; i < listPackageName.size(); i++) {
            HashMap<String, Object> myMap = new HashMap<>();
            myMap.put("Type","image");
            myMap.put("Url",imageUrl);
            HashMap<String, Object> myMap2 = new HashMap<>();
            myMap2.put("Type","video");
            myMap2.put("Url",videoUrl);
            ArrayList bannerList = new ArrayList();
            bannerList.add(0,myMap);
            bannerList.add(1,myMap2);
            Map<String,Object>bannerListMap = new HashMap<>();
            ArrayList<HashMap<String, Object>> producList = (ArrayList<HashMap<String, Object>>) bannerListMap.put("BannerList",bannerList);

            FirebaseFirestore db2 = FirebaseFirestore.getInstance();
            CollectionReference uNames2 = db2.collection("AdminNameManagement");
            int finalI = i;
            uNames2.document(listPackageName.get(i)).update(bannerListMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e(TAG, "DocumentSnapshot successfully written!" + listPackageName.get(finalI));
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "BannerList Updated...!!", Toast.LENGTH_SHORT).show();


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Error:"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void createMorefunPackageAdminList() {

        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Please Wait.......!!!");
        dialog.setCancelable(false);
        dialog.show();

        List<String> listPackageName = new ArrayList<>();
        listPackageName.add(packageName_);


        List<String> listAdminName = new ArrayList<>();
        listAdminName.add(adminName_);




        updateMorefun(listPackageName, listAdminName);
    }

    private void updatdUnifiedAeps() {
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Please Wait.......!!!");
        dialog.setCancelable(false);
        dialog.show();

        List<String> listPackageName = new ArrayList<>();
        listPackageName.add(packageName_);

        List<String> listAdminName = new ArrayList<>();
        listAdminName.add(adminName_);





        updateUnified(listPackageName, listAdminName);
    }

    private void updateUnified(List<String> listPackageName, List<String> listAdminName) {

        for (int i = 0; i < listPackageName.size(); i++) {
            Map<String, Object> onboardMap = new HashMap<>();
            Map<String, Object> map = new HashMap<>();
            ArrayList<String> alAdmin = new ArrayList<>();
            ArrayList<String> alUsers = new ArrayList<>();

            alAdmin.add(listAdminName.get(i));
            alUsers.add("");


            onboardMap.put("Admins", alAdmin);
            onboardMap.put("Users", alUsers);
            map.put("UnifiedAePSEnable", onboardMap);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference uNames = db.collection("AdminNameManagement");
            int finalI = i;
            uNames.document(listPackageName.get(i)).update(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Log.e(TAG, "DocumentSnapshot successfully written!" + listPackageName.get(finalI));
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "UnifiedAeps successfully Added !!!", Toast.LENGTH_SHORT).show();


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Error writing document", e);
                }
            });
        }


    }



    public void updateMorefun(List<String> listPackageName, List<String> listAdminName) {

        for (int i = 0; i < listPackageName.size(); i++) {
            Map<String, Object> onboardMap = new HashMap<>();
            Map<String, Object> map = new HashMap<>();
            ArrayList<String> alAdmin = new ArrayList<>();
            ArrayList<String> alUsers = new ArrayList<>();

            alAdmin.add(listAdminName.get(i));
            alUsers.add("");


            onboardMap.put("Admins", alAdmin);
            onboardMap.put("Users", alUsers);
            map.put("BetaTesting", onboardMap);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference uNames = db.collection("AdminNameManagement");
            int finalI = i;
            uNames.document(listPackageName.get(i)).update(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Log.e(TAG, "DocumentSnapshot successfully written!" + listPackageName.get(finalI));
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "BetaTesting successfully Added !!!", Toast.LENGTH_SHORT).show();


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error: "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }


    }


    public void addFirestore(ArrayList<AdminItems> list) {

        dialog = new ProgressDialog(MainActivity.this);
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
            appDetails.put("AepsDriverActivityUpdate", current.getAepsDriverActivityUpdate());
            appDetails.put("AppName", current.getAppName());
            appDetails.put("CreatedBy", current.getCreatedBy());
            appDetails.put("CustomTheme", current.getCustomTheme());
            appDetails.put("MainApp", current.getMainApp());
            appDetails.put("UniqueId", current.getUniqueId());
            appDetails.put("VersionName", current.getVersionName());
            appDetails.put("Onboard", getOnboardingMap());

            uNames.document(packageName).set(appDetails)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e(TAG, "DocumentSnapshot successfully written!" + appDetails);
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Add New Apps successfully Added !!!", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error: "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }
            });


        }

        dialog.dismiss();

    }

    private Map<String, Object> getOnboardingMap() {

        Map<String, Object> onboardMap = new HashMap<>();
        ArrayList<String> onBoardKycAdmins = new ArrayList<>();
        onBoardKycAdmins.add(adminName_);
        ArrayList<String> onBoardKycUsers = new ArrayList<>();
        onBoardKycUsers.add("");




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
        onboardMap.put("Admins",onBoardKycAdmins);
        onboardMap.put("Users",onBoardKycUsers);

        return onboardMap;
    }


    public ArrayList<AdminItems> convert(JSONArray array) {
        ArrayList<AdminItems> items = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject object = array.getJSONObject(i);
                String packageName = object.getString("Package");
                String admin = object.getString("AdminName");
                String aepsDriverActivityUpdate = object.getString("AepsDriverActivityUpdate");
                String appCode = object.getString("AppCode");
                String appName = object.getString("AppName");
                String createdBy = object.getString("CreatedBy");
                String customTheme = object.getString("CustomTheme");
                String mainApp = object.getString("MainApp");
                String uniqueId = object.getString("UniqueId");
                String versionName = object.getString("VersionName");

                items.add(new AdminItems(packageName, admin, appCode, appName, createdBy, mainApp, uniqueId, versionName,aepsDriverActivityUpdate,customTheme));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return items;
    }

    public List<String> getList(JSONArray array) {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject object = array.getJSONObject(i);
                String mainApp = object.getString("MainApp");

                items.add(mainApp);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return items;
    }

    private void createDeviceSetUpPackageAdminList() {

        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Please Wait.......!!!");
        dialog.setCancelable(false);
        dialog.show();

        List<String> listPackageName = new ArrayList<>();
        listPackageName.add(packageName_);
        List<String> listAdminNameMoreFun = new ArrayList<>();
        listAdminNameMoreFun.add(adminName_);
        List<String> listAdminNamePaxA910 = new ArrayList<>();
        listAdminNamePaxA910.add("");
        List<String> listAdminNamePaxD180 = new ArrayList<>();
        listAdminNamePaxD180.add(adminName_);
        List<String> listAdminNameWiseasy = new ArrayList<>();
        listAdminNameWiseasy.add("");
        updateDeviceSetUp(listPackageName, listAdminNameMoreFun, listAdminNamePaxA910, listAdminNamePaxD180,listAdminNameWiseasy);
    }

    public void updateDeviceSetUp(List<String> listPackageName, List<String> listAdminNameMoreFun, List<String> listAdminNamePaxA910, List<String> listAdminNamePaxD180, List<String> listAdminNameWiseasy) {

        for (int i = 0; i < listPackageName.size(); i++) {
            Map<String, Object> moreFunMap = new HashMap<>();
            Map<String, Object> paxA910Map = new HashMap<>();
            Map<String, Object> paxD180Map = new HashMap<>();
            Map<String, Object> wiseasyMap = new HashMap<>();

            Map<String, Object> map = new HashMap<>();
            Map<String, Object> mapDeviceType = new HashMap<>();
            ArrayList<String> alAdminMoreFun = new ArrayList<>();
            ArrayList<String> alAdminPaxA910 = new ArrayList<>();
            ArrayList<String> alAdminPaxD180 = new ArrayList<>();
            ArrayList<String> alAdminWiseasy = new ArrayList<>();

            ArrayList<String> alUsers = new ArrayList<>();
            alAdminMoreFun.add(listAdminNameMoreFun.get(i));
            alAdminPaxA910.add(listAdminNamePaxA910.get(i));
            alAdminPaxD180.add(listAdminNamePaxD180.get(i));
            alAdminWiseasy.add(listAdminNameWiseasy.get(i));

            alUsers.add("");
            moreFunMap.put("Admins", alAdminMoreFun);
            moreFunMap.put("Users", alUsers);
            paxA910Map.put("Admins", alAdminPaxA910);
            paxA910Map.put("Users", alUsers);
            paxD180Map.put("Admins", alAdminPaxD180);
            paxD180Map.put("Users", alUsers);
            wiseasyMap.put("Admins", alAdminWiseasy);
            wiseasyMap.put("Users", alUsers);

            mapDeviceType.put("MOREFUN", moreFunMap);
            mapDeviceType.put("PAXA910", paxA910Map);
            mapDeviceType.put("PAXD180", paxD180Map);
            mapDeviceType.put("WISEASY", wiseasyMap);

            map.put("DeviceSetup", mapDeviceType);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference uNames = db.collection("AdminNameManagement");
            int finalI = i;
            uNames.document(listPackageName.get(i)).update(map)
                    .addOnSuccessListener(aVoid -> {
                        Log.e(TAG, "Device SetUp successfully Added !!!" + listPackageName.get(finalI));
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Device SetUp successfully Added !!!", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Error: "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

}