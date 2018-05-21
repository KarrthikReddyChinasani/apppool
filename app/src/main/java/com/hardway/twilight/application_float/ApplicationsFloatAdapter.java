package com.hardway.twilight.application_float;

/**
 * Created by karth on 1/28/2018.
 */

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
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
import java.util.Arrays;
import java.util.List;

public class ApplicationsFloatAdapter extends RecyclerView.Adapter<ApplicationsFloatAdapter.MyViewHolder> {
    Context context;
    private List<Application_Float> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView app_image_application_circle;
        public TextView title_rv_application_circle;
        public MyViewHolder(View view) {
            super(view);
            app_image_application_circle = (ImageView) view.findViewById(R.id.app_image_application_circle);
            title_rv_application_circle = (TextView) view.findViewById(R.id.title_rv_application_circle);
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


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Bitmap bitmap = generateCircleBitmap(this.context,getMaterialColor(apps),30, apps.getAppName().substring(0,1));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitmapdata = stream.toByteArray();

            Glide.with(context).load(bitmapdata)
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(context))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.app_image_application_circle);

            holder.title_rv_application_circle.setText(apps.getAppName());

        } else {

            Drawable icon = null;
            try {
                icon = context.getPackageManager().getApplicationIcon(apps.getPackagename().replace("12811", "."));
                // Log.v("icon", icon.toString());
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitmapdata = stream.toByteArray();


            Glide.with(context).load(bitmapdata)
                    .crossFade()
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.app_image_application_circle);
        }
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

    public static Bitmap generateCircleBitmap(Context context, int circleColor, float diameterDP, String text){
        final int textColor = 0xffffffff;

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float diameterPixels = diameterDP * (metrics.densityDpi / 160f);
        float radiusPixels = diameterPixels/2;

        // Create the bitmap
        Bitmap output = Bitmap.createBitmap((int) diameterPixels, (int) diameterPixels,
                Bitmap.Config.ARGB_8888);

        // Create the canvas to draw on
        Canvas canvas = new Canvas(output);
        canvas.drawARGB(0, 0, 0, 0);

        // Draw the circle
        final Paint paintC = new Paint();
        paintC.setAntiAlias(true);
        paintC.setColor(circleColor);
        canvas.drawCircle(radiusPixels, radiusPixels, radiusPixels, paintC);

        // Draw the text
        if (text != null && text.length() > 0) {
            final Paint paintT = new Paint();
            paintT.setColor(textColor);
            paintT.setAntiAlias(true);
            paintT.setTextSize(radiusPixels * 2);
            Typeface typeFace = Typeface.createFromAsset(context.getAssets(),"fonts/roboto-thin.ttf");
            paintT.setTypeface(typeFace);
            final Rect textBounds = new Rect();
            paintT.getTextBounds(text, 0, text.length(), textBounds);
            canvas.drawText(text, radiusPixels - textBounds.exactCenterX(), radiusPixels - textBounds.exactCenterY(), paintT);
        }

        return output;
    }

    private static List<Integer> materialColors = Arrays.asList(
            0xffe57373,
            0xfff06292,
            0xffba68c8,
            0xff9575cd,
            0xff7986cb,
            0xff64b5f6,
            0xff4fc3f7,
            0xff4dd0e1,
            0xff4db6ac,
            0xff81c784,
            0xffaed581,
            0xffff8a65,
            0xffd4e157,
            0xffffd54f,
            0xffffb74d,
            0xffa1887f,
            0xff90a4ae
    );

    public static int getMaterialColor(Object key) {
        return materialColors.get(Math.abs(key.hashCode()) % materialColors.size());
    }


}
