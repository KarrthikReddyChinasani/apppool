package com.hardway.twilight;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.hardway.twilight.application.Apps;
import com.hardway.twilight.database.ApplicationSqlite;
import com.hardway.twilight.database.DatabaseHandler;
import com.hardway.twilight.main.AppsAdapter;
import com.hardway.twilight.service.FloatingViewService;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    Button next_page;
    private RecyclerView recyclerView;
    private AppsAdapter adapter;
    private List<Apps> appsList;
    DatabaseHandler db;
    Switch aSwitch;
    ImageView size_image;
    SeekBar seekBar1;
    public static final String MyPREFERENCES = "Twilight" ;
    SharedPreferences sharedpreferences;
    ProgressDialog progressDialog;
    TextView count;
    ImageView facebookShare, whatsappShare, messageShare, shareWithOther;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        facebookShare = (ImageView) findViewById(R.id.facebookShare);
        whatsappShare = (ImageView) findViewById(R.id.whatsappShare);
        messageShare = (ImageView) findViewById(R.id.messageShare);
        shareWithOther = (ImageView) findViewById(R.id.shareWithOther);

        facebookShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean installed = appInstalledOrNot("com.facebook.katana");
                if(installed) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setPackage("com.facebook.katana");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.hardway.twilight");
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                } else {
                    System.out.println("App is not currently installed on your phone");
                }
            }
        });
        whatsappShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean installed = appInstalledOrNot("com.whatsapp");
                if(installed) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setPackage("com.whatsapp");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.hardway.twilight");
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                } else {
                    System.out.println("App is not currently installed on your phone");
                }
            }
        });
        messageShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String smsBody="https://play.google.com/store/apps/details?id=com.hardway.twilight";
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.putExtra("sms_body", smsBody);
                sendIntent.setType("vnd.android-dir/mms-sms");
                startActivity(sendIntent);
            }
        });
        shareWithOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.hardway.twilight");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
        aSwitch = (Switch) findViewById(R.id.notify_me);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean isService = sharedpreferences.getBoolean("serviceOn",false);
        if(isService){
            aSwitch.setChecked(isService);
        } else {
            aSwitch.setChecked(isService);
        }
        db = new DatabaseHandler(MainActivity.this);
        List<ApplicationSqlite> applicationSqliteList  = db.getAllContacts();
        count = (TextView) findViewById(R.id.count);
        count.setText(applicationSqliteList.size()+"");
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_menu_view);
        appsList = new ArrayList<>();
        adapter = new AppsAdapter(this, appsList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(4, dpToPx(5), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        prepareApps();
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)  {
                        List<ApplicationSqlite> appsSqliteList  = db.getAllContacts();
                        if(appsSqliteList.size()>0) {
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putBoolean("serviceOn", true);
                            editor.commit();
                            progressDialog = new ProgressDialog(MainActivity.this);
                            progressDialog.setCancelable(false);
                            progressDialog.setIcon(R.mipmap.ic_launcher);
                            progressDialog.setTitle("Turning on the widget service");
                            progressDialog.setMessage("This may take few moments....");
                            progressDialog.show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    startService(new Intent(MainActivity.this, FloatingViewService.class));
                                    progressDialog.dismiss();
                                }
                            }, 1000);
                        } else {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                            // Setting Dialog Title
                            alertDialog.setTitle("No Apps are selected...");
                            // Setting Dialog Message
                            alertDialog.setMessage("Would you like to add some?");
                            // Setting Icon to Dialog
                            alertDialog.setIcon(R.mipmap.ic_launcher);
                            // Setting Positive "Yes" Button
                            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int which) {
                                  aSwitch.setChecked(false);
                                  dialog.cancel();
                                  Intent ij = new Intent(MainActivity.this, Application.class);
                                  startActivity(ij);
                                }
                            });
                            // Setting Negative "NO" Button
                            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Write your code here to invoke NO event
                                    Toast.makeText(getApplicationContext(), "Please select some applications and try turing on the widget again", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                            });
                            // Showing Alert Message
                            alertDialog.show();
                        }
                } else {
                       SharedPreferences.Editor editor = sharedpreferences.edit();
                       editor.putBoolean("serviceOn", false);
                       editor.commit();
                       stopService(new Intent(MainActivity.this,FloatingViewService.class));
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            if(applicationSqliteList.size()>0){
            }  else {
                Intent ij = new Intent(MainActivity.this,Application.class);
                startActivity(ij);
            }
        }
        next_page = (Button) findViewById(R.id.next_page);
        next_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,Application.class);
                startActivity(i);
                finish();
            }
        });
    }
    private void initializeView() {
        startService(new Intent(MainActivity.this, FloatingViewService.class));
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
                Toast.makeText(this,"Draw over other app permission accepted",Toast.LENGTH_SHORT).show();
            } else { //Permission is not available
                Toast.makeText(this,"Draw over other app permission not available. Closing the application",Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;
        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column
            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }
    private void prepareApps(){
        Apps apps;
        List<ApplicationSqlite> applicationSqliteList  = db.getAllContacts();
        for (ApplicationSqlite app : applicationSqliteList) {
            apps = new Apps(app.getAppName(),app.getPackagename().replace("12811","."));
            Log.e("data",app.getPackagename().replace("12811","."));
            appsList.add(apps);
        }
        adapter.notifyDataSetChanged();
    }
    @Override
    protected void onDestroy() {
        /*
           Toast.makeText(MainActivity.this,"Destroy Main Activity",Toast.LENGTH_LONG).show();
        */
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean isService = sharedpreferences.getBoolean("serviceOn",false);
        if(isService){
            startService(new Intent(MainActivity.this, FloatingViewService.class));
        }
        super.onDestroy();
    }
}