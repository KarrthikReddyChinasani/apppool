package com.hardway.twilight.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.opengl.Visibility;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.IntDef;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hardway.twilight.MainActivity;
import com.hardway.twilight.R;
import com.hardway.twilight.application.Apps;
import com.hardway.twilight.application.CircleTransform;
import com.hardway.twilight.application_float.Application_Float;
import com.hardway.twilight.application_float.ApplicationsFloatAdapter;
import com.hardway.twilight.database.ApplicationSqlite;
import com.hardway.twilight.database.DatabaseHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;


public class FloatingViewService extends Service {
    private WindowManager mWindowManager;
    private View mFloatingView;
    List<ActivityManager.RunningAppProcessInfo> processes;
    ActivityManager amg;
    String TAG = "Floating View Service";

    public static final String MyPREFERENCES = "Twilight" ;
    SharedPreferences sharedpreferences;

    private List<Application_Float> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ApplicationsFloatAdapter mAdapter;

    final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);

    public FloatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean isService = sharedpreferences.getBoolean("serviceOn",true);
       // Toast.makeText(FloatingViewService.this,"Service is on",Toast.LENGTH_LONG).show();

        if(isService) {
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
            params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
            params.x = 0;
            params.y = 100;
            mWindowManager.addView(mFloatingView, params);
            final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);
            final View expandedView = mFloatingView.findViewById(R.id.expanded_container);
            ImageView app_view_close = (ImageView) mFloatingView.findViewById(R.id.app_view_close);
            final ImageView collapsed_iv = (ImageView) mFloatingView.findViewById(R.id.collapsed_iv);

            Glide.with(FloatingViewService.this).load(R.drawable.ic_close)
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(FloatingViewService.this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(app_view_close);
            app_view_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    collapsedView.setVisibility(View.VISIBLE);
                    expandedView.setVisibility(View.GONE);
                }
            });

            /**
             *
             * Recycler View
             *
             */
            recyclerView = (RecyclerView) mFloatingView.findViewById(R.id.recycler_view_app_view);
            mAdapter = new ApplicationsFloatAdapter(movieList, FloatingViewService.this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);

            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new FloatingViewService.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Application_Float apps = movieList.get(position);

                    PackageManager manager = FloatingViewService.this.getPackageManager();
                    try {
                        collapsedView.setVisibility(View.VISIBLE);
                        expandedView.setVisibility(View.GONE);
                        Intent i = manager.getLaunchIntentForPackage(apps.getPackagename().replace("12811", "."));
                        if (i == null) {
                            //throw new ActivityNotFoundException();
                        }
                        i.addCategory(Intent.CATEGORY_LAUNCHER);
                        FloatingViewService.this.startActivity(i);

                    } catch (ActivityNotFoundException e) {

                    }
                }

                @Override
                public void onLongClick(View view, int position) {
                    Application_Float apps = movieList.get(position);
                    Toast.makeText(getApplicationContext(), apps.getAppName() + "", Toast.LENGTH_SHORT).show();
                }
            }));

/*
            collapsed_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    prepareAppsData();
                    collapsedView.setVisibility(View.GONE);
                    expandedView.setVisibility(View.VISIBLE);

                }
            });
*/

            //Drag and move floating view using user's touch action.
            mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;


                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:

                            //remember the initial position.
                            initialX = params.x;
                            initialY = params.y;

                            //get the touch location
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            return true;
                        case MotionEvent.ACTION_UP:
                            int Xdiff = (int) (event.getRawX() - initialTouchX);
                            int Ydiff = (int) (event.getRawY() - initialTouchY);


                            //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                            //So that is click event.
                            if (Xdiff < 10 && Ydiff < 10) {
                                if (isViewCollapsed()) {
                                    //When user clicks on the image view of the collapsed layout,
                                    //visibility of the collapsed layout will be changed to "View.GONE"
                                    //and expanded view will become visible.
                                    prepareAppsData();
                                    collapsedView.setVisibility(View.GONE);
                                    expandedView.setVisibility(View.VISIBLE);
                                }
                            }
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            //Calculate the X and Y coordinates of the view.
                            params.x = initialX + (int) (event.getRawX() - initialTouchX);
                            params.y = initialY + (int) (event.getRawY() - initialTouchY);
                            //Update the layout with new X & Y coordinate
                            mWindowManager.updateViewLayout(mFloatingView, params);
                            return true;
                    }
                    return false;
                }
            });
        }
    }

    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    public void prepareAppsData(){
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
        Application_Float applicationFloat;
        DatabaseHandler db = new DatabaseHandler(FloatingViewService.this);
        List<ApplicationSqlite> apps = db.getAllContacts();
        for (ApplicationSqlite app : apps) {
            applicationFloat = new Application_Float(app.getAppName(),app.getPackagename().replace("12811","."));
            Log.e("data",app.getPackagename().replace("12811","."));
            movieList.add(applicationFloat);
        }
        mAdapter.notifyDataSetChanged();
    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private FloatingViewService.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final FloatingViewService.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Notification note = new Notification(R.mipmap.ic_launcher_round,
                "Foreground Service notification?", System.currentTimeMillis());
        Intent i = new Intent(this, FloatingViewService.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
        Date dateService=new Date(System.currentTimeMillis());
        String dateString=dateService.toString().split(" ")[1]+" "+dateService.toString().split(" ")[2]+" "+dateService.toString().split(" ")[3];
        /* note.setLatestEventInfo(this, "Foreground service",
                "Now foreground service running: "+dateString, pi);*/
        note.flags |= Notification.FLAG_AUTO_CANCEL;

        startForeground(2337, note);

      /*
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean isService = sharedpreferences.getBoolean("serviceOn",false);
        if(isService) {
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
            params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
            params.x = 0;
            params.y = 100;
            mWindowManager.addView(mFloatingView, params);
            final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);
            final View expandedView = mFloatingView.findViewById(R.id.expanded_container);
            ImageView app_view_close = (ImageView) mFloatingView.findViewById(R.id.app_view_close);
            final ImageView collapsed_iv = (ImageView) mFloatingView.findViewById(R.id.collapsed_iv);
            Glide.with(FloatingViewService.this).load(R.drawable.ic_close)
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(FloatingViewService.this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(app_view_close);
            app_view_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    collapsedView.setVisibility(View.VISIBLE);
                    expandedView.setVisibility(View.GONE);
                }
            });
            recyclerView = (RecyclerView) mFloatingView.findViewById(R.id.recycler_view_app_view);
            mAdapter = new ApplicationsFloatAdapter(movieList, FloatingViewService.this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new FloatingViewService.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Application_Float apps = movieList.get(position);
                    PackageManager manager = FloatingViewService.this.getPackageManager();
                    try {
                        collapsedView.setVisibility(View.VISIBLE);
                        expandedView.setVisibility(View.GONE);
                        Intent i = manager.getLaunchIntentForPackage(apps.getPackagename().replace("12811", "."));
                        if (i == null) {
                            //throw new ActivityNotFoundException();
                        }
                        i.addCategory(Intent.CATEGORY_LAUNCHER);
                        FloatingViewService.this.startActivity(i);
                    } catch (ActivityNotFoundException e) {
                    }
                }
                @Override
                public void onLongClick(View view, int position) {
                    Application_Float apps = movieList.get(position);
                    Toast.makeText(getApplicationContext(), apps.getAppName() + "", Toast.LENGTH_SHORT).show();
                }
            }));
            collapsed_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    prepareAppsData();
                    collapsedView.setVisibility(View.GONE);
                    expandedView.setVisibility(View.VISIBLE);
                }
            });
            //Drag and move floating view using user's touch action.
            mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //remember the initial position.
                            initialX = params.x;
                            initialY = params.y;
                            //get the touch location
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            return true;
                        case MotionEvent.ACTION_UP:
                            int Xdiff = (int) (event.getRawX() - initialTouchX);
                            int Ydiff = (int) (event.getRawY() - initialTouchY);
                            //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                            //So that is click event.
                            if (Xdiff < 10 && Ydiff < 10) {
                                if (isViewCollapsed()) {
                                    //When user clicks on the image view of the collapsed layout,
                                    //visibility of the collapsed layout will be changed to "View.GONE"
                                    //and expanded view will become visible.
                                    prepareAppsData();
                                    collapsedView.setVisibility(View.GONE);
                                    expandedView.setVisibility(View.VISIBLE);
                                }
                            }
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            //Calculate the X and Y coordinates of the view.
                            params.x = initialX + (int) (event.getRawX() - initialTouchX);
                            params.y = initialY + (int) (event.getRawY() - initialTouchY);
                            //Update the layout with new X & Y coordinate
                            mWindowManager.updateViewLayout(mFloatingView, params);
                            return true;
                    }
                    return false;
                }
            });
        }
      */
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean isService = sharedpreferences.getBoolean("serviceOn",false);
        if(isService) {
            Intent i = new Intent("any string");
            i.setClass(this, ServiceRestarterBroadcastReceiver.class);
            this.sendBroadcast(i);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWindowManager.removeView(mFloatingView);
    }
}