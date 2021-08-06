package rk.android.app.privacydashboard.util;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.TypedValue;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import rk.android.app.privacydashboard.BuildConfig;
import rk.android.app.privacydashboard.R;
import rk.android.app.privacydashboard.activities.appinfo.AppInfoActivity;
import rk.android.app.privacydashboard.activities.log.LogsActivity;
import rk.android.app.privacydashboard.constant.Constants;
import rk.android.app.privacydashboard.manager.PreferenceManager;

public class Utils {

    public static void openHistoryActivity(Context context, String permission) {
        Bundle bundle = ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
        Intent i = new Intent(context, LogsActivity.class);
        i.putExtra(Constants.EXTRA_PERMISSION,permission);
        context.startActivity(i, bundle);
    }

    public static void openAppInfoActivity(Context context, String packageName) {
        Bundle bundle = ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
        Intent i = new Intent(context, AppInfoActivity.class);
        i.putExtra(Constants.EXTRA_APP,packageName);
        context.startActivity(i, bundle);
    }

    public static void openPrivacySettings(Context context) {
        Bundle bundle = ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
        Intent intent;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            intent = new Intent(Settings.ACTION_PRIVACY_SETTINGS);
        }else {
            intent = new Intent(Intent.ACTION_MAIN)
                    .setComponent(new ComponentName("com.android.settings",
                            "com.android.settings.Settings$AppAndNotificationDashboardActivity"));
        }

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            try {
                context.startActivity(intent, bundle);
            }catch (Exception ignored){
                context.startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        }
    }

    public static void openPermissionLog(Context context, String permission){
        Bundle bundle = ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
        Intent i = new Intent(context, LogsActivity.class);
        i.putExtra(Constants.EXTRA_PERMISSION,permission);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i, bundle);
    }

    public static void openAppSettings(Context context, String packageName){
        Bundle bundle = ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        context.startActivity(intent, bundle);
    }

    public static void openLink(Context context, String url) {
        Bundle bundle = ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i, bundle);
    }

    public static void sendEmail(Context context){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("content://"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Constants.DEV_MAIL});
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name) + ":" + BuildConfig.VERSION_NAME);
        intent.putExtra(Intent.EXTRA_TEXT,"");
        try {
            context.startActivity(intent);
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

    public static String getTheme(Context context){
        switch (AppCompatDelegate.getDefaultNightMode()) {
            case AppCompatDelegate.MODE_NIGHT_YES:
                return context.getString(R.string.dialog_theme_dark);

            case AppCompatDelegate.MODE_NIGHT_NO:
                return context.getString(R.string.dialog_theme_light);

            case AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY:
                return context.getString(R.string.dialog_theme_battery);

            default:
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
            case AppCompatDelegate.MODE_NIGHT_UNSPECIFIED:
                return context.getString(R.string.dialog_theme_system);

        }
    }

    public static void setTheme(Context context, PreferenceManager preferenceManager, int mode, Class<?> cls){
        preferenceManager.setNightMode(mode);
        AppCompatDelegate.setDefaultNightMode(mode);
        Bundle bundle = ActivityOptions.makeCustomAnimation(context,
                        android.R.anim.fade_in,android.R.anim.fade_out).toBundle();
        Intent intent = new Intent(context, cls);
        ((Activity) context).finish();
        context.startActivity(intent,bundle);
    }

    public static int getAttrColor(Context context, int attr){
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedValue, true);
        return ContextCompat.getColor(context, typedValue.resourceId);
    }

    public static String getTimeFromTimestamp(Context context, long timestamp){
        if (DateFormat.is24HourFormat(context)) {
            return DateFormat.format("HH:mm", new Date(timestamp)).toString();
        }else {
            return DateFormat.format("hh:mm a", new Date(timestamp)).toString();
        }
    }

    public static String getDateFromTimestamp(Context context, long timestamp){

        Calendar calendar = Calendar.getInstance();
        String today = DateFormat.format("dd MMM", calendar.getTimeInMillis()).toString();

        calendar.add(Calendar.DATE,-1);
        String yesterday = DateFormat.format("dd MMM", calendar.getTimeInMillis()).toString();

        String date = DateFormat.format("dd MMM", new Date(timestamp)).toString();
        if (today.equals(date)) {
            return context.getString(R.string.log_today);
        }

        if (yesterday.equals(date)){
            return context.getString(R.string.log_yesterday);
        }

        return date;
    }

    public static String getDateFromTimestamp(long timestamp){
        return DateFormat.format("dd-MMM-yyyy", new Date(timestamp)).toString();
    }

    public static String getNameFromPackageName(Context context, String packageName){
        final PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        return (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "(unknown)");
    }

    public static Bitmap getAppIcon(Context context, String packageName){
        Drawable drawable = getIconFromPackageName(context, packageName);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Drawable getIconFromPackageName(Context context, String packageName){
        try {
            Drawable drawable = context.getPackageManager().getApplicationIcon(packageName);
            if (drawable!=null)
                return drawable;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return ResourcesCompat.getDrawable(context.getResources(),R.mipmap.ic_launcher,context.getTheme());
    }

    public static void openAutoStartAccordingToManufacturer(Context context) {
        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }

            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() > 0) {
                context.startActivity(intent);
            }
        } catch (Exception e) {
            android.util.Log.i("UTILS", "openAutoStartAccordingToManufacturer: " + e.getMessage());
        }
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static List<String> getSystemApps(Context context){

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

        List<String> helpPackages = new ArrayList<>();
        helpPackages.add("com.android.systemui");
        helpPackages.add("com.android.settings");
        if (resolveInfo!=null) {
            helpPackages.add(resolveInfo.activityInfo.packageName);
        }

        return helpPackages;
    }
}
