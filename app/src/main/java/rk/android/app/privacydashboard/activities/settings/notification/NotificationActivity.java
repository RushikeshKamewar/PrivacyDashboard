package rk.android.app.privacydashboard.activities.settings.notification;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import rk.android.app.privacydashboard.R;
import rk.android.app.privacydashboard.constant.Constants;
import rk.android.app.privacydashboard.databinding.ActivitySettingsNotificationBinding;
import rk.android.app.privacydashboard.manager.PreferenceManager;

public class NotificationActivity extends AppCompatActivity{

    Context context;

    PreferenceManager preferenceManager;
    ActivitySettingsNotificationBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = NotificationActivity.this;
        preferenceManager =  new PreferenceManager(getApplicationContext());

        setupToolbar();
        initValues();
        initOnClickListeners();

    }

    private void setupToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.settings_notification_custom));
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

        binding.notificationOngoing.setSwitchState(preferenceManager.isPrivacyNotificationOngoing());
        binding.notificationIcon.setSwitchState(preferenceManager.isPrivacyNotificationIcon());
        binding.notificationClick.setSwitchState(preferenceManager.isPrivacyNotificationClick());

    }

    private void initOnClickListeners(){


        binding.notificationOngoing.setOnSwitchListener((compoundButton, b) -> preferenceManager.setPrivacyNotificationOngoing(b));
        binding.notificationOngoing.setOnClickListener(view -> binding.notificationOngoing.performSwitchClick());

        binding.notificationIcon.setOnSwitchListener((compoundButton, b) -> preferenceManager.setPrivacyNotificationIcon(b));
        binding.notificationIcon.setOnClickListener(view -> binding.notificationIcon.performSwitchClick());

        binding.notificationClick.setOnSwitchListener((compoundButton, b) -> preferenceManager.setPrivacyNotificationClick(b));
        binding.notificationClick.setOnClickListener(view -> binding.notificationClick.performSwitchClick());

    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_righ);
    }
}
