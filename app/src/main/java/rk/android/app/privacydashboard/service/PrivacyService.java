package rk.android.app.privacydashboard.service;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.res.ColorStateList;
import android.graphics.PixelFormat;
import android.hardware.camera2.CameraManager;
import android.location.GnssStatus;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.AudioRecordingConfiguration;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.List;

import rk.android.app.privacydashboard.BuildConfig;
import rk.android.app.privacydashboard.R;
import rk.android.app.privacydashboard.activities.appinfo.AppInfoActivity;
import rk.android.app.privacydashboard.activities.log.database.LogsRepository;
import rk.android.app.privacydashboard.activities.settings.excluded.database.ExcludedRepository;
import rk.android.app.privacydashboard.constant.Constants;
import rk.android.app.privacydashboard.manager.PreferenceManager;
import rk.android.app.privacydashboard.model.Logs;
import rk.android.app.privacydashboard.util.Utils;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class PrivacyService extends AccessibilityService {

    private static final String TAG = "PrivacyService";

    LogsRepository repository;
    ExcludedRepository excludedRepository;
    PreferenceManager preferenceManager;

    boolean isCamInUse = false, isMicInUse = false, isLocInUse = false;
    boolean isCamUseStart = false, isMicUseStart = false, isLocUseStart = false;
    boolean isPermissionStopped = true;

    CameraManager cameraManager;
    CameraManager.AvailabilityCallback cameraCallback;
    AudioManager audioManager;
    AudioManager.AudioRecordingCallback micCallback;
    LocationManager locationManager;
    GnssStatus.Callback locationCallback;

    WindowManager.LayoutParams layoutParams;
    WindowManager windowManager;
    FrameLayout hoverLayout;
    ImageView dotCamera, dotMic, dotLoc, dotIcon;
    LinearLayout lyDots;

    NotificationManagerCompat notificationManager;
    NotificationCompat.Builder notificationCompatBuilder;

    String runningAppPackage = BuildConfig.APPLICATION_ID;
    String lastAppPackage = BuildConfig.APPLICATION_ID;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Intent notificationIntent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE,BuildConfig.APPLICATION_ID)
                    .putExtra(Settings.EXTRA_CHANNEL_ID,Constants.NOTIFICATION_CHANNEL);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

            Notification notification = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL)
                    .setContentTitle(getString(R.string.notification_desc))
                    .setContentText(getString(R.string.notification_hide))
                    .setSmallIcon(R.drawable.app_logo)
                    .setContentIntent(pendingIntent)
                    .build();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                startForeground(3, notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA
                                | ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
                                | ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION);
            } else {
                startForeground(3, notification);
            }
        }

        repository = new LogsRepository(getApplication());
        excludedRepository = new ExcludedRepository(getApplication());
        preferenceManager = new PreferenceManager(getApplicationContext());
        notificationManager = NotificationManagerCompat.from(getApplicationContext());

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        createOverlay();
        initDots();
        initCallbacks();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        try {
            if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                    && accessibilityEvent.getPackageName() != null) {
                ComponentName componentName = new ComponentName(accessibilityEvent.getPackageName().toString(),
                        accessibilityEvent.getClassName().toString());
                runningAppPackage = componentName.getPackageName();
            }
        } catch (Exception e) {
            Log.i(TAG, "onAccessibilityEvent:" + new Exception(e).getMessage());

        }
    }

    @Override
    public void onInterrupt() {
        Log.i(TAG, "onInterrupt: ");
    }

    @Override
    public void onDestroy() {
        unRegisterCameraCallBack();
        unRegisterMicCallback();
        unRegisterLocCallback();

        if (notificationManager != null) {
            notificationManager.cancel(3);
        }

        stopForeground(true);
        super.onDestroy();
    }

    private void initCallbacks() {
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        cameraManager.registerAvailabilityCallback(getCameraCallback(), null);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.registerAudioRecordingCallback(getMicCallback(), null);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.registerGnssStatusCallback(getLocationCallback(),null);
        }

    }

    // Camera service callbacks
    private CameraManager.AvailabilityCallback getCameraCallback() {
        cameraCallback = new CameraManager.AvailabilityCallback() {
            @Override
            public void onCameraAvailable(String cameraId) {
                super.onCameraAvailable(cameraId);
                    isCamInUse = false;
                    isCamUseStart = true;
                    dismissOnUseNotification();
                    hideDots(Constants.PERMISSION_CAMERA);
                    makeLog(Constants.PERMISSION_CAMERA);
            }

            @Override
            public void onCameraUnavailable(String cameraId) {
                super.onCameraUnavailable(cameraId);
                isCamInUse = true;
                isCamUseStart = true;
                showDots(Constants.PERMISSION_CAMERA);
                makeLog(Constants.PERMISSION_CAMERA);
            }
        };
        return cameraCallback;
    }

    // Audio manager callbacks
    private AudioManager.AudioRecordingCallback getMicCallback() {
        micCallback = new AudioManager.AudioRecordingCallback() {
            @Override
            public void onRecordingConfigChanged(List<AudioRecordingConfiguration> configs) {
                isMicInUse = configs.size() > 0;
                isMicUseStart = true;
                if (isMicInUse) {
                    showDots(Constants.PERMISSION_MICROPHONE);
                }else {
                    dismissOnUseNotification();
                    hideDots(Constants.PERMISSION_MICROPHONE);
                }
                makeLog(Constants.PERMISSION_MICROPHONE);
            }
        };
        return micCallback;
    }

    // Location service Callbacks
    private GnssStatus.Callback getLocationCallback(){
        locationCallback = new GnssStatus.Callback() {
            @Override
            public void onStarted() {
                super.onStarted();
                isLocUseStart = true;
                isLocInUse = true;
                showDots(Constants.PERMISSION_LOCATION);
                makeLog(Constants.PERMISSION_LOCATION);
            }

            @Override
            public void onStopped() {
                super.onStopped();
                isLocInUse = false;
                isLocUseStart = true;
                dismissOnUseNotification();
                hideDots(Constants.PERMISSION_LOCATION);
                makeLog(Constants.PERMISSION_LOCATION);
            }
        };
        return locationCallback;
    }

    private void makeLog(String permission){

        int state = Constants.STATE_INVALID;

        switch (permission){
            case Constants.PERMISSION_CAMERA:
                if (isCamUseStart && isCamInUse) {
                    state = Constants.STATE_ON;
                } else if (isCamUseStart) {
                    state = Constants.STATE_OFF;
                    isCamUseStart = false;
                }
                break;

            case Constants.PERMISSION_MICROPHONE:
                if (isMicUseStart && isMicInUse) {
                    state = Constants.STATE_ON;
                } else if (isMicUseStart) {
                    state = Constants.STATE_OFF;
                    isMicUseStart = false;
                }
                break;

            case Constants.PERMISSION_LOCATION:
                if (isLocUseStart && isLocInUse) {
                    state = Constants.STATE_ON;
                } else if (isLocUseStart) {
                    state = Constants.STATE_OFF;
                    isLocUseStart = false;
                }
                break;

        }

        boolean isAllowed = isAppAllowed(permission);

        if (!isAllowed && state == Constants.STATE_OFF && !lastAppPackage.equals(BuildConfig.APPLICATION_ID)){
            if (!excludedRepository.isExcluded(lastAppPackage) || !preferenceManager.isPrivacyExcludeLogs()) {
                Logs log = new Logs(Calendar.getInstance().getTimeInMillis(), lastAppPackage, permission, state, Utils.getDateFromTimestamp(Calendar.getInstance().getTimeInMillis()));
                repository.insertLogs(log);
            }
            lastAppPackage = BuildConfig.APPLICATION_ID;
        }

        if (isAppAllowed(permission)) {
            lastAppPackage = runningAppPackage;
            if (!excludedRepository.isExcluded(runningAppPackage) || !preferenceManager.isPrivacyExcludeLogs()) {
                Logs log = new Logs(Calendar.getInstance().getTimeInMillis(), runningAppPackage, permission, state, Utils.getDateFromTimestamp(Calendar.getInstance().getTimeInMillis()));
                repository.insertLogs(log);
                showOnUseNotification();
            }
        }

    }

    // Unregistering on destroy
    private void unRegisterCameraCallBack() {
        if (cameraManager != null && cameraCallback != null) {
            cameraManager.unregisterAvailabilityCallback(cameraCallback);
        }
    }

    private void unRegisterMicCallback() {
        if (audioManager != null && micCallback != null) {
            audioManager.unregisterAudioRecordingCallback(micCallback);
        }
    }

    private void unRegisterLocCallback() {
        if (locationManager != null && locationCallback != null) {
            locationManager.unregisterGnssStatusCallback(locationCallback);
        }
    }


    //Privacy dots
    private void createOverlay() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        hoverLayout = new FrameLayout(this);

        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = preferenceManager.getPrivacyDotPosition();
        if (preferenceManager.isPrivacyDotMargin()){
            layoutParams.horizontalMargin = Constants.DOTS_MARGIN;
            layoutParams.verticalMargin = Constants.DOTS_MARGIN;
        }else {
            layoutParams.horizontalMargin = 0f;
            layoutParams.verticalMargin = 0f;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(R.layout.overlay_dot, hoverLayout);

        windowManager.addView(hoverLayout, layoutParams);
    }

    private void initDots() {
        lyDots = hoverLayout.findViewById(R.id.ly_overlay);
        dotCamera = hoverLayout.findViewById(R.id.icon_camera);
        dotMic = hoverLayout.findViewById(R.id.icon_microphone);
        dotLoc = hoverLayout.findViewById(R.id.icon_location);
        dotIcon = hoverLayout.findViewById(R.id.icon_dot);

        dotCamera.postDelayed(() -> {
            dotCamera.setVisibility(View.GONE);
            dotMic.setVisibility(View.GONE);
            dotLoc.setVisibility(View.GONE);
            dotIcon.setVisibility(View.GONE);
            lyDots.setVisibility(View.GONE);
        }, 300);
    }

    private void getIcons(){

        if (preferenceManager.isPrivacyDots()) {
            switch (preferenceManager.getPrivacyDotType()){
                case Constants.DOTS_TYPE_ICON:
                    dotCamera.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_camera));
                    dotMic.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_microphone));
                    dotLoc.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_location));
                    lyDots.setBackgroundResource(R.drawable.overlay_background);
                    lyDots.setBackgroundTintList(ColorStateList.valueOf(preferenceManager.getIconColor()));
                    break;

                case Constants.DOTS_TYPE_DOT:
                    dotCamera.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_dot_camera));
                    dotMic.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_dot_mic));
                    dotLoc.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_dot_location));
                    lyDots.setBackground(null);
                    break;
            }
        } else {
            dotCamera.setImageDrawable(null);
            dotMic.setImageDrawable(null);
            dotLoc.setImageDrawable(null);
        }

        if (preferenceManager.isPrivacyDotClick()) {
            dotCamera.setOnClickListener(view -> Utils.openPermissionLog(getApplicationContext(), Constants.PERMISSION_CAMERA));
            dotMic.setOnClickListener(view -> Utils.openPermissionLog(getApplicationContext(), Constants.PERMISSION_MICROPHONE));
            dotLoc.setOnClickListener(view -> Utils.openPermissionLog(getApplicationContext(), Constants.PERMISSION_LOCATION));
        }else {
            dotCamera.setOnClickListener(null);
            dotMic.setOnClickListener(null);
            dotLoc.setOnClickListener(null);
        }

        int size = (int) (30 * Utils.getDensity(getApplicationContext()) * preferenceManager.getPrivacyDotSize() / 100);
        int padding = (int) (5 * Utils.getDensity(getApplicationContext()) * preferenceManager.getPrivacyDotSize() / 100);
        int image_padding = (int) (5 * Utils.getDensity(getApplicationContext()) * preferenceManager.getPrivacyDotSize() / 100);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);

        dotCamera.setPadding(image_padding, image_padding, image_padding, image_padding);
        dotMic.setPadding(image_padding, image_padding, image_padding, image_padding);
        dotLoc.setPadding(image_padding, image_padding, image_padding, image_padding);
        dotIcon.setPadding(image_padding, image_padding, image_padding, image_padding);
        dotCamera.setLayoutParams(params);
        dotMic.setLayoutParams(params);
        dotLoc.setLayoutParams(params);
        dotIcon.setLayoutParams(params);

        lyDots.setPadding(padding, 0, padding, 0);
        lyDots.setAlpha((float) preferenceManager.getPrivacyDotOpacity()/100);

    }

    private void showDots(String permission){
        if (!excludedRepository.isExcluded(runningAppPackage) || !preferenceManager.isPrivacyExcludeIndicator()) {
            isPermissionStopped = false;

            getIcons();

            switch (permission) {
                case Constants.PERMISSION_CAMERA:
                    dotCamera.setVisibility(View.VISIBLE);
                    break;

                case Constants.PERMISSION_MICROPHONE:
                    dotMic.setVisibility(View.VISIBLE);
                    break;

                case Constants.PERMISSION_LOCATION:
                    dotLoc.setVisibility(View.VISIBLE);
                    break;

                default:

            }

            if (preferenceManager.isPrivacyDots()) {
                lyDots.setVisibility(View.VISIBLE);
                dotIcon.setVisibility(View.GONE);

                if (preferenceManager.isPrivacyDotAutoHide()) {
                    lyDots.postDelayed(() -> {
                        if (!isPermissionStopped) {
                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                            lyDots.startAnimation(animation);
                            lyDots.postDelayed(() -> {
                                if (!isPermissionStopped) {
                                    dotCamera.setVisibility(View.GONE);
                                    dotMic.setVisibility(View.GONE);
                                    dotLoc.setVisibility(View.GONE);
                                    lyDots.setBackground(null);
                                    dotIcon.setVisibility(View.VISIBLE);
                                }
                            }, 400);
                        }
                    }, preferenceManager.getPrivacyDotAutoHideTimer() * 1000);
                }

                if (preferenceManager.isPrivacyDotHide()) {
                    lyDots.postDelayed(() -> {
                        if (!isPermissionStopped) {
                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
                            lyDots.startAnimation(animation);
                            lyDots.postDelayed(() -> {
                                if (!isPermissionStopped) {
                                    dotCamera.setVisibility(View.GONE);
                                    dotMic.setVisibility(View.GONE);
                                    dotLoc.setVisibility(View.GONE);
                                    lyDots.setBackground(null);
                                    dotIcon.setVisibility(View.GONE);
                                }
                            }, 400);
                        }
                    }, preferenceManager.getPrivacyDotHideTimer() * 1000);
                }

                layoutParams.gravity = preferenceManager.getPrivacyDotPosition();
                if (preferenceManager.isPrivacyDotMargin()) {
                    layoutParams.horizontalMargin = Constants.DOTS_MARGIN;
                    layoutParams.verticalMargin = Constants.DOTS_MARGIN;
                } else {
                    layoutParams.horizontalMargin = 0f;
                    layoutParams.verticalMargin = 0f;
                }

                windowManager.updateViewLayout(hoverLayout, layoutParams);
            }
        }
    }

    private void hideDots(String permission){
        switch (permission){
            case Constants.PERMISSION_CAMERA:
                dotCamera.setVisibility(View.GONE);
                break;

            case Constants.PERMISSION_MICROPHONE:
                dotMic.setVisibility(View.GONE);
                break;

            case Constants.PERMISSION_LOCATION:
                dotLoc.setVisibility(View.GONE);
                break;

            default:

        }

        if (dotCamera.getVisibility() == View.GONE && dotMic.getVisibility() == View.GONE
                && dotLoc.getVisibility() == View.GONE){
            lyDots.setVisibility(View.GONE);
        }else {
            lyDots.setVisibility(View.VISIBLE);
        }

        isPermissionStopped = true;

    }

    //Privacy notification
    private void initOnUseNotification(String packageName) {
        notificationCompatBuilder = new NotificationCompat.Builder(getApplicationContext(), Constants.PERMISSION_NOTIFICATION_CHANNEL)
                .setSmallIcon(getPermissionIcon())
                .setContentTitle(getNotificationTitle(Utils.getNameFromPackageName(this, runningAppPackage)))
                .setContentText(getNotificationDescription())
                .setContentIntent(getPendingIntent(packageName))
                .setOngoing(preferenceManager.isPrivacyNotificationOngoing())
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (preferenceManager.isPrivacyNotificationIcon())
            notificationCompatBuilder.setLargeIcon(Utils.getAppIcon(this, runningAppPackage));

        notificationManager = NotificationManagerCompat.from(getApplicationContext());
    }

    private void showOnUseNotification() {
        if (preferenceManager.isPrivacyNotification()) {
            if (!excludedRepository.isExcluded(runningAppPackage) || !preferenceManager.isPrivacyExcludeNotification()) {
                initOnUseNotification(runningAppPackage);
                if (notificationManager != null)
                    notificationManager.notify(Constants.NOTIFICATION_ID, notificationCompatBuilder.build());
            }
        }
    }

    private void dismissOnUseNotification() {
        if (isCamInUse || isMicInUse || isLocInUse) {
            showOnUseNotification();
        } else {
            if (notificationManager != null) notificationManager.cancel(Constants.NOTIFICATION_ID);
        }
    }

    private String getNotificationTitle(String appName) {
        if (appName.isEmpty() || appName.equals("(unknown)")) {
            appName = getString(R.string.notification_app_unknown);
        }
        return appName;
    }

    private String getNotificationDescription() {

        String description = getString(R.string.notification_app_using);

        if (isCamInUse) {
            description = description + getString(R.string.permission_camera);
        }
        if (isMicInUse) {
            if (isCamInUse && !isLocInUse)
                description = description + getString(R.string.notification_app_and);

            if (isCamInUse && isLocInUse)
                description = description + ", ";
            description = description + getString(R.string.permission_microphone);
        }
        if (isLocInUse) {
            if (isCamInUse | isMicInUse)
                description = description + getString(R.string.notification_app_and);

            description = description + getString(R.string.permission_location);
        }

        if ((isCamInUse && isMicInUse) | (isMicInUse && isLocInUse) |
                (isLocInUse && isCamInUse)) {
            description = description + getString(R.string.notification_app_permissions);
        }else {
            description = description + getString(R.string.notification_app_permission);
        }

        return description;
    }

    private PendingIntent getPendingIntent(String packageName) {

        Intent i;
        if (!preferenceManager.isPrivacyNotificationClick()) {
            i = new Intent(getApplicationContext(), AppInfoActivity.class);
            i.putExtra(Constants.EXTRA_APP, packageName);
        }else {
            i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.setData(Uri.parse("package:" + packageName));
        }

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return PendingIntent.getActivity(getApplicationContext(), 1,i, FLAG_UPDATE_CURRENT);
    }

    private int getPermissionIcon(){

        if (isCamInUse && isMicInUse && isLocInUse) {
            return R.drawable.notification_cml;
        }
        if (isCamInUse && isMicInUse) {
            return R.drawable.notification_cm;
        }
        if (isCamInUse && isLocInUse) {
            return R.drawable.notification_cl;
        }
        if (isMicInUse && isLocInUse) {
            return R.drawable.notification_ml;
        }
        if (isCamInUse) {
            return R.drawable.icon_camera;
        }
        if (isMicInUse) {
            return R.drawable.icon_microphone;
        }
        if (isLocInUse) {
            return R.drawable.icon_location;
        }

        return R.drawable.notification_icon;
    }


    private boolean isAppAllowed(String permission) {

        String permissionManifest = Manifest.permission.ACCESS_FINE_LOCATION;

        switch (permission) {
            case Constants.PERMISSION_CAMERA:
                permissionManifest = Manifest.permission.CAMERA;
                break;

            case Constants.PERMISSION_MICROPHONE:
                permissionManifest = Manifest.permission.RECORD_AUDIO;
                break;

            case Constants.PERMISSION_LOCATION:
                permissionManifest = Manifest.permission.ACCESS_FINE_LOCATION;
                break;
        }

        if (runningAppPackage.equals(BuildConfig.APPLICATION_ID))
            return false;

        if (Utils.getSystemApps(getApplicationContext()).contains(runningAppPackage))
            return false;

        return getPackageManager().checkPermission(permissionManifest, runningAppPackage) == PackageManager.PERMISSION_GRANTED;
    }
}
