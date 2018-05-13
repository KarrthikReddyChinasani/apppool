package com.hardway.twilight.application_float;

/**
 * Created by karth on 1/28/2018.
 */

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hardway.twilight.R;
import com.hardway.twilight.application.CircleTransform;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ApplicationsFloatAdapter extends RecyclerView.Adapter<ApplicationsFloatAdapter.MyViewHolder> {
    Context context;
    private List<Application_Float> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView app_image_application_circle;

        public MyViewHolder(View view) {
            super(view);
            app_image_application_circle = (ImageView) view.findViewById(R.id.app_image_application_circle);
        }
    }


    public ApplicationsFloatAdapter(List<Application_Float> moviesList,  Context context) {
        this.moviesList = moviesList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.application_circle, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Application_Float apps = moviesList.get(position);
        Drawable icon = null;
        try {
            icon = context.getPackageManager().getApplicationIcon(apps.getPackagename().replace("12811","."));
            // Log.v("icon", icon.toString());
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();


        Glide.with(context).load(bitmapdata)
                .crossFade()
                .thumbnail(0.5f)
              //  .bitmapTransform(new CircleTransform(context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.app_image_application_circle);

         holder.app_image_application_circle.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

             }
         });

    }

    @Override
    public int getItemCount()
    {
        return moviesList.size();
    }

    public void clear(){
        moviesList.clear();
    }
}
