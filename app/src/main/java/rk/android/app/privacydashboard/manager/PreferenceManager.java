package rk.android.app.privacydashboard.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;

import androidx.appcompat.app.AppCompatDelegate;

import rk.android.app.privacydashboard.constant.Constants;

public class PreferenceManager {

     SharedPreferences sharedPreferences;

    private static class PREF_CONSTANTS{

        public static final String FIRST_LAUNCH = "first.launch";
        public static final String NIGHT_MODE = "night.mode";
        public static final String VERSION_NUMBER = "version.number";

        public static final String PRIVACY_DOTS = "privacy.dots";
        public static final String PRIVACY_DOTS_TYPE = "privacy.dots.type";
        public static final String PRIVACY_DOTS_POSITION = "privacy.dots.position";
        public static final String PRIVACY_DOTS_MARGIN = "privacy.dots.margin";
        public static final String PRIVACY_DOTS_CLICK = "privacy.dots.click";
        public static final String PRIVACY_DOTS_SIZE = "privacy.dots.size";
        public static final String PRIVACY_DOTS_OPACITY = "privacy.dots.opacity";
        public static final String PRIVACY_DOTS_AUTO_HIDE = "privacy.dots.auto_hide";
        public static final String PRIVACY_DOTS_AUTO_HIDE_TIMER = "privacy.dots.auto_hide.timer";

        public static final String PRIVACY_NOTIFICATION = "privacy.notification";
        public static final String PRIVACY_NOTIFICATION_ONGOING = "privacy.notification.ongoing";
        public static final String PRIVACY_NOTIFICATION_ICON = "privacy.notification.icon";

        public static final String EXCLUDE_TYPE_INDICATOR = "exclude.type.indicator";
        public static final String EXCLUDE_TYPE_NOTIFICATION = "exclude.type.notification";
        public static final String EXCLUDE_TYPE_LOGS = "exclude.type.logs";

    }

    public PreferenceManager(Context context){
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,Context.MODE_PRIVATE);
    }

    public void clearPreferences(){
        sharedPreferences.edit()
                .clear()
                .apply();
    }

    //First launch
    public void setFirstLaunch(boolean isFirst){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_CONSTANTS.FIRST_LAUNCH, isFirst);
        editor.apply();
    }
    public boolean isFirstLaunch(){
        return sharedPreferences.getBoolean(PREF_CONSTANTS.FIRST_LAUNCH, true);
    }

    //Night mode settings
    public void setNightMode(int mode){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_CONSTANTS.NIGHT_MODE, mode);
        editor.apply();
    }
    public int getNightMode(){
        return sharedPreferences.getInt(PREF_CONSTANTS.NIGHT_MODE, AppCompatDelegate.getDefaultNightMode());
    }

    //Version number
    public void setVersionNumber(int number){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_CONSTANTS.VERSION_NUMBER, number);
        editor.apply();
    }
    public int getVersionNumber(){
        return sharedPreferences.getInt(PREF_CONSTANTS.VERSION_NUMBER,0);
    }

    //Privacy dots
    public void setPrivacyDots(boolean isDots){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_CONSTANTS.PRIVACY_DOTS, isDots);
        editor.apply();
    }
    public boolean isPrivacyDots(){
        return sharedPreferences.getBoolean(PREF_CONSTANTS.PRIVACY_DOTS, true);
    }

    //Privacy dots type
    public void setPrivacyDotType(String type){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_CONSTANTS.PRIVACY_DOTS_TYPE, type);
        editor.apply();
    }
    public String getPrivacyDotType(){
        return sharedPreferences.getString(PREF_CONSTANTS.PRIVACY_DOTS_TYPE, Constants.DOTS_TYPE_ICON);
    }

    //Privacy dots position
    public void setPrivacyDotPosition(int position){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_CONSTANTS.PRIVACY_DOTS_POSITION, position);
        editor.apply();
    }
    public int getPrivacyDotPosition(){
        return sharedPreferences.getInt(PREF_CONSTANTS.PRIVACY_DOTS_POSITION, Gravity.TOP | Gravity.END);
    }

    //Privacy dots margin
    public void setPrivacyDotMargin(boolean isMargin){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_CONSTANTS.PRIVACY_DOTS_MARGIN, isMargin);
        editor.apply();
    }
    public boolean isPrivacyDotMargin(){
        return sharedPreferences.getBoolean(PREF_CONSTANTS.PRIVACY_DOTS_MARGIN, false);
    }

    //Privacy dots click action
    public void setPrivacyDotClick(boolean isClick){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_CONSTANTS.PRIVACY_DOTS_CLICK, isClick);
        editor.apply();
    }
    public boolean isPrivacyDotClick(){
        return sharedPreferences.getBoolean(PREF_CONSTANTS.PRIVACY_DOTS_CLICK, false);
    }

    //Privacy dots position
    public void setPrivacyDotSize(int size){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_CONSTANTS.PRIVACY_DOTS_SIZE, size);
        editor.apply();
    }
    public int getPrivacyDotSize(){
        return sharedPreferences.getInt(PREF_CONSTANTS.PRIVACY_DOTS_SIZE, 100);
    }

    //Privacy dots position
    public void setPrivacyDotOpacity(int opacity){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_CONSTANTS.PRIVACY_DOTS_OPACITY, opacity);
        editor.apply();
    }
    public int getPrivacyDotOpacity(){
        return sharedPreferences.getInt(PREF_CONSTANTS.PRIVACY_DOTS_OPACITY, 100);
    }

    //Privacy dots auto hide
    public void setPrivacyDotAutoHide(boolean isHide){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_CONSTANTS.PRIVACY_DOTS_AUTO_HIDE, isHide);
        editor.apply();
    }
    public boolean isPrivacyDotAutoHide(){
        return sharedPreferences.getBoolean(PREF_CONSTANTS.PRIVACY_DOTS_AUTO_HIDE, false);
    }

    //Privacy dots auto hide timer
    public void setPrivacyDotAutoHideTimer(int time){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_CONSTANTS.PRIVACY_DOTS_AUTO_HIDE_TIMER, time);
        editor.apply();
    }
    public int getPrivacyDotAutoHideTimer(){
        return sharedPreferences.getInt(PREF_CONSTANTS.PRIVACY_DOTS_AUTO_HIDE_TIMER, 3);
    }

    //Privacy notification
    public void setPrivacyNotification(boolean isNotification){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_CONSTANTS.PRIVACY_NOTIFICATION, isNotification);
        editor.apply();
    }
    public boolean isPrivacyNotification(){
        return sharedPreferences.getBoolean(PREF_CONSTANTS.PRIVACY_NOTIFICATION, false);
    }

    //Privacy notification ongoing
    public void setPrivacyNotificationOngoing(boolean isHide){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_CONSTANTS.PRIVACY_NOTIFICATION_ONGOING, isHide);
        editor.apply();
    }
    public boolean isPrivacyNotificationOngoing(){
        return sharedPreferences.getBoolean(PREF_CONSTANTS.PRIVACY_NOTIFICATION_ONGOING, true);
    }

    //Privacy notification ongoing
    public void setPrivacyNotificationIcon(boolean isIcon){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_CONSTANTS.PRIVACY_NOTIFICATION_ICON, isIcon);
        editor.apply();
    }
    public boolean isPrivacyNotificationIcon(){
        return sharedPreferences.getBoolean(PREF_CONSTANTS.PRIVACY_NOTIFICATION_ICON, true);
    }

    //Privacy exclude indicator
    public void setPrivacyExcludeIndicator(boolean isExclude){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_CONSTANTS.EXCLUDE_TYPE_INDICATOR, isExclude);
        editor.apply();
    }
    public boolean isPrivacyExcludeIndicator(){
        return sharedPreferences.getBoolean(PREF_CONSTANTS.EXCLUDE_TYPE_INDICATOR, true);
    }

    //Privacy exclude notification
    public void setPrivacyExcludeNotification(boolean isExclude){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_CONSTANTS.EXCLUDE_TYPE_NOTIFICATION, isExclude);
        editor.apply();
    }
    public boolean isPrivacyExcludeNotification(){
        return sharedPreferences.getBoolean(PREF_CONSTANTS.EXCLUDE_TYPE_NOTIFICATION, false);
    }

    //Privacy exclude indicator
    public void setPrivacyExcludeLogs(boolean isExclude){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_CONSTANTS.EXCLUDE_TYPE_LOGS, isExclude);
        editor.apply();
    }
    public boolean isPrivacyExcludeLogs(){
        return sharedPreferences.getBoolean(PREF_CONSTANTS.EXCLUDE_TYPE_LOGS, false);
    }

}
