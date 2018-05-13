package com.hardway.twilight.main;

/**
 * Created by karth on 1/31/2018.
 */

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hardway.twilight.R;
import com.hardway.twilight.application.Apps;
import com.hardway.twilight.application.CircleTransform;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.MyViewHolder> {

    private Context context;
    private List<Apps> appsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
           // title = (TextView) view.findViewById(R.id.title_rv_main);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail_rv_main);
        }
    }


    public AppsAdapter(Context context, List<Apps> appsList) {
        this.context = context;
        this.appsList = appsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.app_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Apps apps = appsList.get(position);
   //        holder.title.setText(apps.getAppName());

        Drawable icon = null;
        try {
            icon = context.getPackageManager().getApplicationIcon(apps.getPackagename());
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
                .bitmapTransform(new CircleTransform(context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.thumbnail);


        // loading apps cover using Glide library
    }


    @Override
    public int getItemCount() {
        return appsList.size();
    }
}