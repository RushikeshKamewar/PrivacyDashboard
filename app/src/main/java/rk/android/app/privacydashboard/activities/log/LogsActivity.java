package rk.android.app.privacydashboard.activities.log;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import rk.android.app.privacydashboard.R;
import rk.android.app.privacydashboard.activities.log.adapter.LogsAdapter;
import rk.android.app.privacydashboard.constant.Constants;
import rk.android.app.privacydashboard.databinding.ActivityLogsBinding;
import rk.android.app.privacydashboard.manager.PreferenceManager;
import rk.android.app.privacydashboard.util.Dialogs;
import rk.android.app.privacydashboard.util.Permissions;
import rk.android.app.privacydashboard.util.Utils;

public class LogsActivity extends AppCompatActivity {

    Context context;
    PreferenceManager preferenceManager;
    ActivityLogsBinding binding;

    LogsViewModel logsViewModel;
    LogsAdapter adapter;

    String permission = Constants.PERMISSION_LOCATION;
    String permission_name = Constants.PERMISSION_LOCATION;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = LogsActivity.this;
        preferenceManager =  new PreferenceManager(getApplicationContext());
        bundle = ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
        getWindow().setBackgroundDrawable(null);

        setupToolbar();
        initValues();
        initOnClickListeners();
    }

    private void setupToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.log_title));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.menu_back);
        toolbar.setNavigationOnClickListener(v -> {
            setResult(RESULT_CANCELED,null);
            finish();
        });

        binding.recyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (binding.recyclerView.canScrollVertically(Constants.SCROLL_DIRECTION_UP)){
                toolbar.setElevation(Constants.TOOLBAR_SCROLL_ELEVATION);
            }else{
                toolbar.setElevation(Constants.TOOLBAR_DEFAULT_ELEVATION);
            }

            if((scrollY - oldScrollY) > 0){
                binding.buttonSettingsPermission.hide();
            } else{
                binding.buttonSettingsPermission.show();
            }
        });
    }

    private void initValues(){

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            if (bundle.containsKey(Constants.EXTRA_PERMISSION)){
                permission = bundle.getString(Constants.EXTRA_PERMISSION);
            }
        }

        permission_name = Permissions.getName(context,permission);
        logsViewModel = new ViewModelProvider(this).get(LogsViewModel.class);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new LogsAdapter(context, permission, getLayoutInflater());
        binding.recyclerView.setAdapter(adapter);

        logsViewModel.getLogs(permission).observe(this, logs -> {
            adapter.stopLoading();
            adapter.setLogsList(logs);

            if (logs.isEmpty()){
                binding.rlEmpty.setVisibility(View.VISIBLE);
            }else {
                binding.rlEmpty.setVisibility(View.GONE);
            }
        });

    }

    private void initOnClickListeners(){
        binding.buttonSettingsPermission.setOnClickListener(view -> {
            if (permission.equals(Constants.PERMISSION_LOCATION)) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), bundle);
            }else {
                Utils.openPrivacySettings(context);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logs_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete){
            Dialogs.deleteLogs(getApplication(),context,getLayoutInflater(),
                    getString(R.string.delete_logs_title).replace("#ALIAS#",permission_name),
                    getString(R.string.delete_logs_info).replace("#ALIAS#",permission_name),
                    permission);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_righ);
    }

}
