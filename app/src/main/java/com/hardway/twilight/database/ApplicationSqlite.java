package com.hardway.twilight.database;

/**
 * Created by karth on 1/26/2018.
 */

public class ApplicationSqlite {

    private String appName, packagename;
    public  ApplicationSqlite(){

    }

    public ApplicationSqlite(String appName,String packagename){
        this.appName = appName;
        this.packagename = packagename;
    }

    public String getAppName(){
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackagename() {
        return packagename;
    }

    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }
}

