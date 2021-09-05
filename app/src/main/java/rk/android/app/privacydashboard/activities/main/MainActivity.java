package rk.android.app.privacydashboard.activities.main;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rk.android.app.privacydashboard.BuildConfig;
import rk.android.app.privacydashboard.R;
import rk.android.app.privacydashboard.activities.donation.DonationActivity;
import rk.android.app.privacydashboard.activities.log.database.LogsRepository;
import rk.android.app.privacydashboard.activities.settings.SettingsActivity;
import rk.android.app.privacydashboard.constant.Constants;
import rk.android.app.privacydashboard.databinding.ActivityMainBinding;
import rk.android.app.privacydashboard.manager.PreferenceManager;
import rk.android.app.privacydashboard.service.PrivacyService;
import rk.android.app.privacydashboard.util.Dialogs;
import rk.android.app.privacydashboard.util.Permissions;
import rk.android.app.privacydashboard.util.PieCharts;
import rk.android.app.privacydashboard.util.Utils;

public class MainActivity extends AppCompatActivity {

    Intent serviceIntent;
    Context context;
    PreferenceManager preferenceManager;
    ActivityMainBinding binding;

    LogsRepository logsRepository;

    String date = "01-Jan-2021";

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = MainActivity.this;
        preferenceManager =  new PreferenceManager(getApplicationContext());
        bundle = ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
        getWindow().setBackgroundDrawable(null);
        logsRepository = new LogsRepository(getApplication());
        date = Utils.getDateFromTimestamp(Calendar.getInstance().getTimeInMillis());

        setupToolbar();
        initUI();
        initPieChart();
        initOnClickListener();
        initValues();

        Permissions.checkAutoStartRequirement(context, getLayoutInflater(), preferenceManager);
        

    }

    private void setupToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.app_name));

        binding.scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (binding.scrollView.canScrollVertically(Constants.SCROLL_DIRECTION_UP)){
                toolbar.setElevation(Constants.TOOLBAR_SCROLL_ELEVATION);
            }else{
                toolbar.setElevation(Constants.TOOLBAR_DEFAULT_ELEVATION);
            }
        });
    }

    private void initUI(){

        Dialogs.showWhatsNewDialog(context,getLayoutInflater(),preferenceManager,false);

        binding.versionText.setText(getString(R.string.profile_version));
        binding.versionText.append(" ");
        binding.versionText.append(BuildConfig.VERSION_NAME);
    }

    private void initPieChart(){

        binding.pieChart.setDrawCenterText(true);
        binding.pieChart.setCenterText(getString(R.string.chart_center_text));
        binding.pieChart.setCenterTextColor(Utils.getAttrColor(context,R.attr.colorIcon));
        binding.pieChart.setCenterTextTypeface(ResourcesCompat.getFont(this, R.font.medium));
        binding.pieChart.setCenterTextSize(16f);
        binding.pieChart.setDrawHoleEnabled(true);
        binding.pieChart.setHoleColor(Utils.getAttrColor(context,R.attr.colorBackground));
        binding.pieChart.setTransparentCircleAlpha(0);
        binding.pieChart.setHoleRadius(90f);
        binding.pieChart.setRotationAngle(0);
        binding.pieChart.setRotationEnabled(false);
        binding.pieChart.setHighlightPerTapEnabled(false);
//        chart.setOnChartValueSelectedListener(this);
        binding.pieChart.setEntryLabelColor(Utils.getAttrColor(context,R.attr.colorIcon));
        binding.pieChart.setEntryLabelTypeface(ResourcesCompat.getFont(this, R.font.medium));
        binding.pieChart.setEntryLabelTextSize(15f);
        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.getLegend().setEnabled(false);
        binding.pieChart.highlightValues(null);
        binding.pieChart.setExtraTopOffset(8f);
        binding.pieChart.setExtraBottomOffset(8f);
        binding.pieChart.setDrawRoundedSlices(false);

    }

    private void initOnClickListener(){

        binding.buttonAccessSetting.setOnClickListener(view -> {
            startActivity(new Intent("android.settings.ACCESSIBILITY_SETTINGS"),bundle);
            Toast.makeText(context,getString(R.string.settings_accessibility_on),Toast.LENGTH_SHORT).show();
        });

        binding.buttonLocationSetting.setOnClickListener(view -> askPermission());

        binding.permissionLocation.setOnClickListener(view -> Utils.openHistoryActivity(context,Constants.PERMISSION_LOCATION));

        binding.permissionCamera.setOnClickListener(view -> Utils.openHistoryActivity(context,Constants.PERMISSION_CAMERA));

        binding.permissionMicrophone.setOnClickListener(view -> Utils.openHistoryActivity(context,Constants.PERMISSION_MICROPHONE));

        binding.permissionMore.setOnClickListener(view -> Utils.openPrivacySettings(context));

        binding.settings.setOnClickListener(view -> startActivity(new Intent(context, SettingsActivity.class), bundle));

        binding.donation.setOnClickListener(view -> startActivity(new Intent(context, DonationActivity.class), bundle));

        binding.buttonGithub.setOnClickListener(view -> Utils.openLink(context, Constants.LINK_GITHUB));

        binding.buttonTelegram.setOnClickListener(view -> Utils.openLink(context, Constants.LINK_TELEGRAM));

        binding.buttonTwitter.setOnClickListener(view -> Utils.openLink(context,Constants.LINK_TWITTER));

    }

    private void initValues(){

        List<Integer> logs = new ArrayList<>();
        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        logs.add(logsRepository.getLogsCount(Constants.PERMISSION_LOCATION,date));
        logs.add(logsRepository.getLogsCount(Constants.PERMISSION_CAMERA,date));
        logs.add(logsRepository.getLogsCount(Constants.PERMISSION_MICROPHONE,date));

        for (int i = 0; i < logs.size(); i++){
            if (logs.get(i) != 0){
                entries.add(new PieEntry(logs.get(i), Permissions.getString(context, i)));
                colors.add(Utils.getAttrColor(context,Permissions.getColor(i)));
            }
        }

        if (entries.size() == 0){
            entries.add(new PieEntry(1, " "));
            colors.add(Utils.getAttrColor(context,R.attr.colorCardBackground));
        }

        binding.permissionLocation.setPermissionUsage(Permissions.getPermissionUsageInfo(context,logs.get(Constants.POSITION_LOCATION)));
        binding.permissionCamera.setPermissionUsage(Permissions.getPermissionUsageInfo(context,logs.get(Constants.POSITION_CAMERA)));
        binding.permissionMicrophone.setPermissionUsage(Permissions.getPermissionUsageInfo(context,logs.get(Constants.POSITION_MICROPHONE)));

        binding.pieChart.setData(PieCharts.getData(context,entries,colors));
        binding.pieChart.invalidate();

    }

    private void askPermission() {
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == 0)) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    private void checkForAccessibilityAndStart() {
        if (!Permissions.accessibilityPermission(getApplicationContext(), PrivacyService.class)) {
            serviceIntent = new Intent(MainActivity.this, PrivacyService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_refresh){
            initValues();
            Toast.makeText(context,getString(R.string.menu_refresh_info),Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.action_theme){
            Dialogs.showThemeDialog(context,getLayoutInflater(),preferenceManager,MainActivity.class);
        }else if (item.getItemId() == R.id.action_release){
            Dialogs.showWhatsNewDialog(context,getLayoutInflater(),preferenceManager,true);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        binding.viewSettings.setVisibility(View.GONE);

        if (Permissions.accessibilityPermission(context, PrivacyService.class)){
            binding.lyAccessibility.setVisibility(View.GONE);
        }else {
            binding.lyAccessibility.setVisibility(View.VISIBLE);
            binding.viewSettings.setVisibility(View.VISIBLE);
        }

        if (Permissions.checkLocation(context)){
            binding.lyLocation.setVisibility(View.GONE);
        }else {
            binding.lyLocation.setVisibility(View.VISIBLE);
            binding.viewSettings.setVisibility(View.VISIBLE);
        }

        checkForAccessibilityAndStart();

    }


}