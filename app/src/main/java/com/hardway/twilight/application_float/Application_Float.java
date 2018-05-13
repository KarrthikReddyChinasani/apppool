package com.hardway.twilight.application_float;

/**
 * Created by karth on 1/28/2018.
 */

public class Application_Float {

    private String appName, packagename;
    int id;
    public  Application_Float(){

    }

    public Application_Float(String appName,String packagename){
        this.appName = appName;
        this.packagename = packagename;
    }

    public Application_Float(int id,String appName,String packagename){
        this.appName = appName;
        this.packagename = packagename;
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
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
