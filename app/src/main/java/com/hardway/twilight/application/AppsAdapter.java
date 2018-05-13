package com.hardway.twilight.application;

/**
 * Created by karth on 1/26/2018.
 */

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.BoolRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hardway.twilight.R;
import com.hardway.twilight.database.ApplicationSqlite;
import com.hardway.twilight.database.DatabaseHandler;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.MyViewHolder> {
    Context context;
    private List<Apps> appsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView app_name;
        public ImageView app_images;
       // public Switch apps_switch;
        public CheckBox apps_check;
        public MyViewHolder(View view) {
            super(view);
            app_name = (TextView) view.findViewById(R.id.app_name);
            app_images = (ImageView) view.findViewById(R.id.app_image);
           // apps_switch = (Switch) view.findViewById(R.id.app_switch);
            apps_check = (CheckBox) view.findViewById(R.id.app_check);
        }
    }


    public AppsAdapter(List<Apps> appsList, Context context) {
        this.appsList = appsList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.application, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Drawable icon = null;
        final DatabaseHandler db = new DatabaseHandler(context);
        final Apps apps = appsList.get(position);
        holder.app_name.setText(apps.getAppName());
        String packagename = apps.getPackagename();


        boolean check_false_true = false;

        try {
            icon = context.getPackageManager().getApplicationIcon(apps.getPackagename());
           // Log.v("icon", icon.toString());
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        /*
       Glide.with(context)
                .load( icon)
                .asBitmap()
                .apply(RequestOptions.circleCropTransform())
                .into(holder.app_images);

        */

        Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();



        Glide.with(context).load(bitmapdata)
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.app_images);


        //holder.apps_switch.setText(movie.getYear());

         check_false_true = db.getContact(apps.getPackagename().replace(".","12811"));
         Log.e("data check",check_false_true+"");
         holder.apps_check.setChecked(check_false_true);

        holder.apps_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                  //  Log.d("add",b+"");
                    if(holder.apps_check.isPressed()) {
                        String package_changed = apps.getPackagename().replace(".", "12811");
                        Log.e("package", package_changed);
                        db.addContact(new ApplicationSqlite(apps.getAppName(), package_changed));
                    }
                } else {
                    if (holder.apps_check.isPressed()) {
                        String package_changed = apps.getPackagename().replace(".", "12811");
                        //  Log.d("delete",b+"");
                        db.deleteContact(new ApplicationSqlite(apps.getAppName(), package_changed));
                    }
                }
            }
        });

    }
    @Override
    public int getItemCount() {
        return appsList.size();
    }
}
