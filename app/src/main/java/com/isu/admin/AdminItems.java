package com.isu.admin;

public class AdminItems {
    String packageName, admin, appCode, appName, createdBy, mainApp, uniqueId, versionName,customTheme,aepsDriverActivityUpdate;

    public String getCustomTheme() {
        return customTheme;
    }

    public String getAepsDriverActivityUpdate() {
        return aepsDriverActivityUpdate;
    }

    public AdminItems(String packageName, String admin, String appCode, String appName, String createdBy, String mainApp, String uniqueId, String versionName,String aepsDriverActivityUpdate,String customTheme ) {
        this.packageName = packageName;
        this.admin = admin;
        this.appCode = appCode;
        this.appName = appName;
        this.createdBy = createdBy;
        this.mainApp = mainApp;
        this.uniqueId = uniqueId;
        this.versionName = versionName;
        this.aepsDriverActivityUpdate = aepsDriverActivityUpdate;
        this.customTheme = customTheme;
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




