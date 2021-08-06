package rk.android.app.privacydashboard.activities.settings.excluded;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import rk.android.app.privacydashboard.R;
import rk.android.app.privacydashboard.activities.settings.excluded.database.ExcludedRepository;
import rk.android.app.privacydashboard.constant.Constants;
import rk.android.app.privacydashboard.databinding.ActivitySettingsExcludeBinding;
import rk.android.app.privacydashboard.manager.PreferenceManager;
import rk.android.app.privacydashboard.util.Dialogs;

public class ExcludeActivity extends AppCompatActivity {

    Context context;
    ActivitySettingsExcludeBinding binding;

    PreferenceManager preferenceManager;
    ExcludedRepository repository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingsExcludeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = ExcludeActivity.this;
        getWindow().setBackgroundDrawable(null);
        repository = new ExcludedRepository(getApplication());
        preferenceManager =  new PreferenceManager(getApplicationContext());

        setupToolbar();
        initValues();
        initOnClickListeners();

    }

    private void setupToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.settings_exclude));
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

        String excludeType = "";
        if (preferenceManager.isPrivacyExcludeIndicator()){
            excludeType = excludeType + getString(R.string.settings_exclude_type_indicator);
        }

        if (preferenceManager.isPrivacyExcludeNotification()){
            if (preferenceManager.isPrivacyExcludeIndicator() && !preferenceManager.isPrivacyExcludeLogs())
                excludeType = excludeType + getString(R.string.notification_app_and);

            if (preferenceManager.isPrivacyExcludeIndicator() && preferenceManager.isPrivacyExcludeLogs())
                excludeType = excludeType + ", ";

            excludeType = excludeType + getString(R.string.settings_exclude_type_notification);
        }

        if (preferenceManager.isPrivacyExcludeLogs()){
            if (preferenceManager.isPrivacyExcludeIndicator() | preferenceManager.isPrivacyExcludeNotification())
                excludeType = excludeType + getString(R.string.notification_app_and);

            excludeType = excludeType + getString(R.string.settings_exclude_type_logs);
        }

        binding.excludeType.setInfo(excludeType);

        binding.excludeList.setInfo(repository.getCount() + getString(R.string.settings_excluded_info));

    }

    private void initOnClickListeners(){

        binding.excludeType.setOnClickListener(view -> Dialogs.showExcludeType(context, getLayoutInflater(),
                preferenceManager, this::initValues));

        binding.excludeList.setOnClickListener(view -> {
            Bundle bundle = ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
            Intent i = new Intent(context, ExcludedActivity.class);
            startActivity(i, bundle);
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        initValues();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_righ);
    }
}
