package rk.android.app.privacydashboard.activities.appinfo;

import android.app.ActivityOptions;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import rk.android.app.privacydashboard.R;
import rk.android.app.privacydashboard.activities.appinfo.adapter.AppInfoAdapter;
import rk.android.app.privacydashboard.activities.settings.excluded.database.ExcludedRepository;
import rk.android.app.privacydashboard.constant.Constants;
import rk.android.app.privacydashboard.databinding.ActivityLogsBinding;
import rk.android.app.privacydashboard.model.Apps;
import rk.android.app.privacydashboard.util.Dialogs;
import rk.android.app.privacydashboard.util.Utils;

public class AppInfoActivity extends AppCompatActivity {

    Context context;
    ActivityLogsBinding binding;

    private final List<String> helpPackages = new ArrayList<>();

    AppInfoViewModel appInfoViewModel;
    AppInfoAdapter adapter;
    ExcludedRepository repository;

    String packageName = Constants.EXTRA_APP;
    Bundle bundle;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = AppInfoActivity.this;
        bundle = ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
        getWindow().setBackgroundDrawable(null);
        repository = new ExcludedRepository(getApplication());

        helpPackages.addAll(Utils.getSystemApps(context));

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
            if (bundle.containsKey(Constants.EXTRA_APP)){
                packageName = bundle.getString(Constants.EXTRA_APP);
            }
        }

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new AppInfoAdapter(context, packageName);
        binding.recyclerView.setAdapter(adapter);

        appInfoViewModel = new ViewModelProvider(this).get(AppInfoViewModel.class);
        appInfoViewModel.getAllLogsForPackage(packageName).observe(this, logs -> {
            adapter.stopLoading();
            adapter.setLogsList(logs);

            if (logs.isEmpty()){
                binding.recyclerView.setVisibility(View.GONE);
                binding.rlEmpty.setVisibility(View.VISIBLE);
            }else {
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.rlEmpty.setVisibility(View.GONE);
            }
        });

    }

    private void initOnClickListeners(){
        binding.buttonSettingsPermission.setOnClickListener(view -> Utils.openAppSettings(context,packageName));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logs_menu,menu);
        menu.findItem(R.id.action_help).setVisible(helpPackages.contains(packageName));
        menu.findItem(R.id.action_exclude).setVisible(true);
        if (!repository.isExcluded(packageName)){
            menu.findItem(R.id.action_exclude).setIcon(R.drawable.menu_exclude_off);
        }else {
            menu.findItem(R.id.action_exclude).setIcon(R.drawable.menu_exclude_on);
        }
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete){
            Dialogs.deleteAppLogs(getApplication(),context,getLayoutInflater(),
                    getString(R.string.delete_logs_title).replace("#ALIAS#",Utils.getNameFromPackageName(context,packageName)),
                    getString(R.string.delete_logs_info).replace("#ALIAS#",Utils.getNameFromPackageName(context,packageName)),
                    packageName);
        }else if (item.getItemId() == R.id.action_exclude){

            if (!repository.isExcluded(packageName)) {
                Dialogs.excludeApp(repository, context, getLayoutInflater(),
                        getString(R.string.exclude_title), getString(R.string.exclude_info), packageName, () -> {
                            repository.insert(new Apps(packageName));
                            Toast.makeText(context,Utils.getNameFromPackageName(context, packageName)
                                    + context.getString(R.string.settings_excluded_toast_on),Toast.LENGTH_SHORT).show();
                            menu.findItem(R.id.action_exclude).setIcon(R.drawable.menu_exclude_on);
                        });
            }else {
                Dialogs.excludeApp(repository, context, getLayoutInflater(),
                        getString(R.string.include_title), getString(R.string.include_info), packageName, () -> {
                            repository.delete(new Apps(packageName));
                            Toast.makeText(context,Utils.getNameFromPackageName(context, packageName)
                                    + context.getString(R.string.settings_excluded_toast_off),Toast.LENGTH_SHORT).show();
                            menu.findItem(R.id.action_exclude).setIcon(R.drawable.menu_exclude_off);
                        });
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public interface OnDialogSubmit{
        void OnSubmit();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_righ);
    }

}
