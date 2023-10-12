package com.isu.admin;

import java.util.Map;

public class AdminItemsOnboarding {
    String packageName, admin, appCode, appName, createdBy, mainApp, uniqueId, versionName;
    Map<String, Object> map,
            onboardMap,
            basicInfo,
            aadhaar,
            email,
            mobile,
            pan;

    String basicInfoAvailable, basicInfoSkip,
            aadhaarAvailable, aadhaarSkip, aadhaarSequence,
            emailAvailable, emailSkip,
            mobileAvailable, mobileSkip,
            panAvailable, panSkip, panSequence,
            ShowOnBoard;

    public AdminItemsOnboarding(String packageName, String admin, String appCode, String appName, String createdBy, String mainApp, String uniqueId, String versionName,
                                String basicInfoAvailable, String basicInfoSkip,
                                String aadhaarAvailable, String aadhaarSkip, String aadhaarSequence,
                                String emailAvailable, String emailSkip,
                                String mobileAvailable, String mobileSkip,
                                String panAvailable, String panSkip, String panSequence,
                                String ShowOnBoard) {
        this.packageName = packageName;
        this.admin = admin;
        this.appCode = appCode;
        this.appName = appName;
        this.createdBy = createdBy;
        this.mainApp = mainApp;
        this.uniqueId = uniqueId;
        this.versionName = versionName;

        //For Onboard
        this.basicInfoAvailable = basicInfoAvailable;
        this.basicInfoSkip = basicInfoSkip;
        this.aadhaarAvailable = aadhaarAvailable;
        this.aadhaarSkip = aadhaarSkip;
        this.aadhaarSequence = aadhaarSequence;
        this.emailAvailable = emailAvailable;
        this.emailSkip = emailSkip;
        this.mobileAvailable = mobileAvailable;
        this.mobileSkip = mobileSkip;
        this.panAvailable = panAvailable;
        this.panSkip = panSkip;
        this.panSequence = panSequence;
        this.ShowOnBoard = ShowOnBoard;

    }

    public String getPackageName() {
        return packageName;
    }

    public String getAdmin() {
        return admin;
    }

    public String getAppCode() {
        return appCode;
    }

    public String getAppName() {
        return appName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getMainApp() {
        return mainApp;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getVersionName() {
        return versionName;
    }
}




