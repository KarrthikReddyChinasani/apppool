package com.hardway.twilight.application;

/**
 * Created by karth on 1/25/2018.
 */

public class Apps {

    private String appName, packagename;
    int id;
    public  Apps(){

      }

      public Apps(String appName,String packagename){
          this.appName = appName;
          this.packagename = packagename;
      }

    public Apps(int id,String appName,String packagename){
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
