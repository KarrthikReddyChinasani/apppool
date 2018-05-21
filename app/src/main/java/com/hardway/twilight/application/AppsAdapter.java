package com.hardway.twilight.application;

/**
 * Created by karth on 1/26/2018.
 */

import android.content.Context;
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
import android.support.annotation.BoolRes;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
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
import java.util.Arrays;
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
                    .into(holder.app_images);


        } else {


            Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitmapdata = stream.toByteArray();


            Glide.with(context).load(bitmapdata)
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(context))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.app_images);
        }

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
