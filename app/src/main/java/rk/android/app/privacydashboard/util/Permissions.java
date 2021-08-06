package rk.android.app.privacydashboard.util;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.accessibility.AccessibilityManager;

import androidx.core.app.ActivityCompat;

import rk.android.app.privacydashboard.R;
import rk.android.app.privacydashboard.constant.Constants;
import rk.android.app.privacydashboard.manager.PreferenceManager;
import rk.android.app.privacydashboard.service.PrivacyService;

public class Permissions {

    public static String getString(Context context, int position){
        switch (position){
            case Constants.POSITION_LOCATION:
                return context.getString(R.string.permission_location);

            case Constants.POSITION_CAMERA:
                return context.getString(R.string.permission_camera);

            case Constants.POSITION_MICROPHONE:
            default:
                return context.getString(R.string.permission_microphone);

        }
    }

    public static String getName(Context context, String permission){
        switch (permission){
            case Constants.PERMISSION_LOCATION:
                return context.getString(R.string.permission_location);

            case Constants.PERMISSION_CAMERA:
                return context.getString(R.string.permission_camera);

            case Constants.PERMISSION_MICROPHONE:
            default:
                return context.getString(R.string.permission_microphone);

        }
    }

    public static int getIcon(Context context, String permission){
        switch (permission){
            case Constants.PERMISSION_LOCATION:
                return R.drawable.icon_location;

            case Constants.PERMISSION_CAMERA:
                return R.drawable.icon_camera;

            case Constants.PERMISSION_MICROPHONE:
            default:
                return R.drawable.icon_microphone;

        }
    }

    public static String getPermissionUsageInfo(Context context, int apps){
        if (apps != 0) {
            return context.getString(R.string.permission_info)
                    .replace("#ALIAS#",String.valueOf(apps));
        }else {
            return context.getString(R.string.permission_no_apps);
        }
    }

    public static int getColor(Context context, String permission){
        switch (permission){
            case Constants.PERMISSION_LOCATION:
                return Utils.getAttrColor(context,R.attr.colorLocation);

            case Constants.PERMISSION_CAMERA:
                return Utils.getAttrColor(context,R.attr.colorCamera);

            case Constants.PERMISSION_MICROPHONE:
            default:
                return Utils.getAttrColor(context,R.attr.colorMicrophone);

        }
    }

    public static int getColor(int position){
        switch (position){
            case Constants.POSITION_LOCATION:
                return R.attr.colorLocation;

            case Constants.POSITION_CAMERA:
                return R.attr.colorCamera;

            case Constants.POSITION_MICROPHONE:
            default:
                return R.attr.colorMicrophone;

        }
    }

    public static boolean accessibilityPermission(Context context, Class<?> cls) {
        ComponentName componentName = new ComponentName(context, cls);
        String string = Settings.Secure.getString(context.getContentResolver(), "enabled_accessibility_services");
        if (string == null) {
            return false;
        }
        TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(':');
        simpleStringSplitter.setString(string);
        while (simpleStringSplitter.hasNext()) {
            ComponentName unflattenFromString = ComponentName.unflattenFromString(simpleStringSplitter.next());
            if (unflattenFromString != null && unflattenFromString.equals(componentName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkAccessibility(Context context) {
        AccessibilityManager manager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        return manager.isEnabled();
    }

    public static boolean checkLocation(Context context) {
        return (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    public static boolean isAccessibilityServiceRunning(Context context) {
        String prefString = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        return prefString != null && prefString.contains(context.getPackageName() + "/" + PrivacyService.class.getName());
    }

    public static void checkAutoStartRequirement(Context context, LayoutInflater inflater, PreferenceManager preferenceManager) {
        String manufacturer = android.os.Build.MANUFACTURER;
        if (preferenceManager.isFirstLaunch()) {
            if ("xiaomi".equalsIgnoreCase(manufacturer)
                    || ("oppo".equalsIgnoreCase(manufacturer))
                    || ("vivo".equalsIgnoreCase(manufacturer))
                    || ("Honor".equalsIgnoreCase(manufacturer))) {
                Dialogs.showAutoStartDialog(context,inflater);
                preferenceManager.setFirstLaunch(false);
            }
        }
    }

}
