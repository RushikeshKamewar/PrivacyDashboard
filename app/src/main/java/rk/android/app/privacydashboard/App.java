package rk.android.app.privacydashboard;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;

import rk.android.app.privacydashboard.constant.Constants;
import rk.android.app.privacydashboard.manager.PreferenceManager;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(new PreferenceManager(getApplicationContext()).getNightMode());

        createNotificationChannel();

    }

    public void createNotificationChannel(){

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL,
                    getString(R.string.service_name),
                    NotificationManager.IMPORTANCE_LOW);
            channel.enableLights(false);
            channel.setShowBadge(false);
            channel.enableVibration(false);
            channel.setDescription(getString(R.string.notification_desc));

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

            NotificationChannel notificationChannel = new NotificationChannel(Constants.PERMISSION_NOTIFICATION_CHANNEL,
                    getString(R.string.notification_usage_title),
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(false);
            notificationChannel.setShowBadge(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setDescription(getString(R.string.notification_usage_desc));

            manager.createNotificationChannel(notificationChannel);
        }

    }
}
