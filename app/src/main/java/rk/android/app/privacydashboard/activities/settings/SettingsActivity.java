package rk.android.app.privacydashboard.activities.settings;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import rk.android.app.privacydashboard.BuildConfig;
import rk.android.app.privacydashboard.R;
import rk.android.app.privacydashboard.activities.settings.excluded.ExcludeActivity;
import rk.android.app.privacydashboard.activities.settings.indicator.IndicatorActivity;
import rk.android.app.privacydashboard.activities.settings.notification.NotificationActivity;
import rk.android.app.privacydashboard.constant.Constants;
import rk.android.app.privacydashboard.databinding.ActivitySettingsBinding;
import rk.android.app.privacydashboard.manager.PreferenceManager;
import rk.android.app.privacydashboard.service.PrivacyService;
import rk.android.app.privacydashboard.util.Dialogs;
import rk.android.app.privacydashboard.util.Permissions;
import rk.android.app.privacydashboard.util.Utils;

public class SettingsActivity extends AppCompatActivity {

    Context context;

    PreferenceManager preferenceManager;
    ActivitySettingsBinding binding;

    boolean switchPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = SettingsActivity.this;
        preferenceManager =  new PreferenceManager(getApplicationContext());
        getWindow().setBackgroundDrawable(null);

        setupToolbar();
        initValues();
        initOnClickListeners();

    }

    private void setupToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.settings_title));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.menu_back);
        toolbar.setNavigationOnClickListener(v -> {
            setResult(RESULT_CANCELED,null);
            finish();
        });

        binding.scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (binding.scrollView.canScrollVertically(Constants.SCROLL_DIRECTION_UP)){
                toolbar.setElevation(Constants.TOOLBAR_SCROLL_ELEVATION);
            }else{
                toolbar.setElevation(Constants.TOOLBAR_DEFAULT_ELEVATION);
            }
        });
    }

    private void initValues(){

        binding.settingsTheme.setInfo(Utils.getTheme(context));

        binding.settingsDots.setSwitchState(preferenceManager.isPrivacyDots());
        if (preferenceManager.isPrivacyDots()) {
            binding.rlDotsCustom.setAlpha(1f);
            binding.rlDotsCustom.setClickable(true);
        }else {
            binding.rlDotsCustom.setAlpha(0.5f);
            binding.rlDotsCustom.setClickable(false);
        }

        binding.settingsNotification.setSwitchState(preferenceManager.isPrivacyNotification());
        if (preferenceManager.isPrivacyNotification()) {
            binding.rlNotificationCustom.setAlpha(1f);
            binding.rlNotificationCustom.setClickable(true);
        }else {
            binding.rlNotificationCustom.setAlpha(0.5f);
            binding.rlNotificationCustom.setClickable(false);
        }

        binding.settingsAccessibility.setSwitchState(Permissions.accessibilityPermission(context, PrivacyService.class));
        binding.settingsLocation.setSwitchState(Permissions.checkLocation(context));

        binding.settingsRelease.setInfo(getString(R.string.settings_release_info) + " " + BuildConfig.VERSION_NAME);

    }

    private void initOnClickListeners(){

        binding.settingsTheme.setOnClickListener(view -> Dialogs.showThemeDialog(context,getLayoutInflater(),preferenceManager,SettingsActivity.class));

        binding.settingsDots.setOnSwitchListener((compoundButton, b) -> {
            preferenceManager.setPrivacyDots(b);
            if (b) {
                binding.rlDotsCustom.setAlpha(1f);
                binding.rlDotsCustom.setClickable(true);
            }else {
                binding.rlDotsCustom.setAlpha(0.5f);
                binding.rlDotsCustom.setClickable(false);
            }
        });
        binding.settingsDots.setOnClickListener(view -> binding.settingsDots.performSwitchClick());

        binding.rlDotsCustom.setOnClickListener(view -> {
            Bundle bundle = ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
            Intent i = new Intent(context, IndicatorActivity.class);
            startActivity(i, bundle);
        });

        binding.settingsNotification.setOnSwitchListener((compoundButton, b) -> {
            preferenceManager.setPrivacyNotification(b);
            if (b) {
                binding.rlNotificationCustom.setAlpha(1f);
                binding.rlNotificationCustom.setClickable(true);
            }else {
                binding.rlNotificationCustom.setAlpha(0.5f);
                binding.rlNotificationCustom.setClickable(false);
            }
        });
        binding.settingsNotification.setOnClickListener(view -> binding.settingsNotification.performSwitchClick());

        binding.rlNotificationCustom.setOnClickListener(view -> {
            Bundle bundle = ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
            Intent i = new Intent(context, NotificationActivity.class);
            startActivity(i, bundle);
        });

        binding.settingsExcluded.setOnClickListener(view -> {
            Bundle bundle = ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
            Intent i = new Intent(context, ExcludeActivity.class);
            startActivity(i, bundle);
        });

        binding.settingsAccessibility.setOnSwitchListener((compoundButton, b) -> {
            if (!switchPressed) {
                if (Permissions.accessibilityPermission(context, PrivacyService.class)) {
                    Toast.makeText(context, getString(R.string.settings_accessibility_off), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, getString(R.string.settings_accessibility_on), Toast.LENGTH_SHORT).show();
                }
                Bundle bundle = ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
                startActivity(new Intent("android.settings.ACCESSIBILITY_SETTINGS"), bundle);
                switchPressed = true;
            }
        });
        binding.settingsAccessibility.setOnClickListener(view -> binding.settingsAccessibility.performSwitchClick());

        binding.settingsLocation.setOnSwitchListener((compoundButton, b) -> {
            if (!switchPressed) {
                askPermission();
            }
        });
        binding.settingsLocation.setOnClickListener(view -> binding.settingsLocation.performSwitchClick());

        binding.settingsLimitation.setOnClickListener(view -> Dialogs.showHelpDialog(context, getLayoutInflater()));

        binding.settingsHideNotification.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startActivity(new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE,BuildConfig.APPLICATION_ID)
                        .putExtra(Settings.EXTRA_CHANNEL_ID,Constants.NOTIFICATION_CHANNEL));
            }else {
                Utils.openAppSettings(context,getPackageName());
            }
        });

        binding.settingsDeleteLogs.setOnClickListener(view -> Dialogs.deleteLogs(getApplication(),context, getLayoutInflater(),
                getString(R.string.delete_logs_title2),getString(R.string.delete_logs_info2),null));

        binding.settingsRelease.setOnClickListener(view -> Dialogs.showWhatsNewDialog(context,getLayoutInflater(),preferenceManager,true));

        binding.settingsGithub.setOnClickListener(view -> Utils.openLink(context, Constants.LINK_GITHUB));

        binding.settingsTelegram.setOnClickListener(view -> Utils.openLink(context, Constants.LINK_TELEGRAM));

        binding.settingsTwitter.setOnClickListener(view -> Utils.openLink(context,Constants.LINK_TWITTER));

    }

    private void askPermission() {
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == 0)) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }else {
            Toast.makeText(context, getString(R.string.settings_location_off), Toast.LENGTH_SHORT).show();
            Utils.openAppSettings(context,getPackageName());
            switchPressed = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initValues();
        switchPressed = false;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_righ);
    }
}
