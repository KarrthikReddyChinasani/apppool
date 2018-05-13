package com.hardway.twilight.recieve;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.hardway.twilight.service.FloatingViewService;


/**
 * Created by karth on 2/1/2018.
 */
public class RecieveReBoot extends BroadcastReceiver
{
    public static final String MyPREFERENCES = "Twilight" ;
    SharedPreferences sharedpreferences;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.e("App Pool","App came here");
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean isService = sharedpreferences.getBoolean("serviceOn",false);
        if(isService){
            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                Intent serviceIntent = new Intent(context, FloatingViewService.class);
                context.startService(serviceIntent);
            }
        }
    }
}