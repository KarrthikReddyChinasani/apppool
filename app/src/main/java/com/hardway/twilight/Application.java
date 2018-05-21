package com.hardway.twilight;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.hardway.twilight.application.Apps;
import com.hardway.twilight.application.AppsAdapter;
import com.hardway.twilight.database.ApplicationSqlite;
import com.hardway.twilight.database.DatabaseHandler;
import com.hardway.twilight.service.FloatingViewService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Application extends AppCompatActivity {

    List<String> googleApps = Arrays.asList("com.google.android.googlequicksearchbox","com.android.chrome","com.google.android.gm", "com.google.android.apps.maps", "com.google.android.youtube" , "com.google.android.apps.docs", "com.google.android.music", "com.google.android.videos", "com.google.android.apps.tachyon", "com.google.android.apps.photos");
    private List<Apps> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AppsAdapter mAdapter;
    DatabaseHandler db;
    public static final String MyPREFERENCES = "Twilight" ;
    SharedPreferences sharedpreferences;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new DatabaseHandler(Application.this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new AppsAdapter(movieList,Application.this);

        recyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(mLayoutManager);

        // adding inbuilt divider line
         recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
         //recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);

        // row click listener
/*
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Apps apps = movieList.get(position);
                Toast.makeText(getApplicationContext(), apps.getAppName() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
*/
        prepareMovieData();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        findMenuItems.inflate(R.menu.application_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home || id==R.id.add){
            final List<ApplicationSqlite> appsSqliteList  = db.getAllContacts();
            boolean isService = sharedpreferences.getBoolean("serviceOn",false);
            if(!isService){
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Application.this);
                // Setting Dialog Title
                alertDialog.setTitle("Widget is not turned on...");
                // Setting Dialog Message
                alertDialog.setMessage("Would you like to Start the widget?");
                // Setting Icon to Dialog
                alertDialog.setIcon(R.mipmap.ic_launcher);
                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(appsSqliteList.size()>0) {
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putBoolean("serviceOn", true);
                            editor.commit();
                            dialog.cancel();
                            progressDialog = new ProgressDialog(Application.this);
                            progressDialog.setCancelable(false);
                            progressDialog.setIcon(R.mipmap.ic_launcher);
                            progressDialog.setTitle("Turning on the widget service");
                            progressDialog.setMessage("This may take few moments....");
                            progressDialog.show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    startService(new Intent(Application.this, FloatingViewService.class));
                                    progressDialog.dismiss();
                                Intent ij = new Intent(Application.this, MainActivity.class);
                                startActivity(ij);
                                finish();
                                }
                            }, 1000);
                        } else if(appsSqliteList.size()==0){
                            Toast.makeText(getApplicationContext(), "Please select some applications and try turing on the widget again", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }

                    }
                });
                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent ij = new Intent(Application.this,MainActivity.class);
                        startActivity(ij);
                        finish();
                    }
                });
                // Showing Alert Message
                alertDialog.show();
            } else{
                Intent abc = new Intent(Application.this,MainActivity.class);
                startActivity(abc);
                finish();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void prepareMovieData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            List<PackageInfo> packList = getPackageManager().getInstalledPackages(0);
            Apps apps;
            final PackageItemInfo.DisplayNameComparator comparator = new PackageItemInfo.DisplayNameComparator(getPackageManager());
            Collections.sort(packList, new Comparator<PackageInfo>() {
                @Override
                public int compare(PackageInfo lhs, PackageInfo rhs) {
                    return comparator.compare(lhs.applicationInfo, rhs.applicationInfo);
                }
            });
            for (int i = 0; i < packList.size(); i++) {
                PackageInfo packInfo = packList.get(i);
                String appPackageName = packInfo.applicationInfo.packageName;
                Log.e("Package Manager", appPackageName);
                if ((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 || googleApps.contains(appPackageName)) {
                    String appName = packInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                    if (appPackageName.equals("com.amazon.appmanager"))
                        continue;
                    apps = new Apps(appName, appPackageName);
                    movieList.add(apps);
                } else {
                    // Log.e("console", packInfo.applicationInfo.loadLabel(getPackageManager()).toString());
                }
            }
        }
        else {
            Apps apps;
            final PackageManager pm = getPackageManager();
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            final PackageItemInfo.DisplayNameComparator comparator = new PackageItemInfo.DisplayNameComparator(pm);
            Collections.sort(packages, new Comparator<ApplicationInfo>() {
                @Override
                public int compare(ApplicationInfo lhs, ApplicationInfo rhs)
                {
                    return comparator.compare(lhs, rhs);
                }
            });
            for (ApplicationInfo packInfo : packages) {
                String appPackageName = packInfo.packageName;
                Log.e("Package Manager",appPackageName);
                if (  (packInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0  || googleApps.contains(appPackageName) )
                {
                    String appName = packInfo.loadLabel(getPackageManager()).toString();
                    if(appPackageName.equals("com.amazon.appmanager")|| appPackageName.contains("com.samsung"))
                        continue;
                    if(appPackageName.contains("com.android"))
                        continue;
                    apps = new Apps(appName, appPackageName);
                    movieList.add(apps);
                } else{
                    // Log.e("console", packInfo.applicationInfo.loadLabel(getPackageManager()).toString());
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }
}


