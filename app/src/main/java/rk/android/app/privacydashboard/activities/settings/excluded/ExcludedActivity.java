package rk.android.app.privacydashboard.activities.settings.excluded;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import rk.android.app.privacydashboard.R;
import rk.android.app.privacydashboard.activities.log.database.LogsRepository;
import rk.android.app.privacydashboard.activities.settings.excluded.adapter.AppAdapter;
import rk.android.app.privacydashboard.activities.settings.excluded.async.AppsTask;
import rk.android.app.privacydashboard.activities.settings.excluded.database.ExcludedRepository;
import rk.android.app.privacydashboard.constant.Constants;
import rk.android.app.privacydashboard.databinding.ActivityExcludedBinding;
import rk.android.app.privacydashboard.helper.async.TaskRunner;
import rk.android.app.privacydashboard.model.Apps;

public class ExcludedActivity extends AppCompatActivity {

    Context context;
    ActivityExcludedBinding binding;

    AppAdapter adapter;
    ExcludedRepository repository;
    LogsRepository logsRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityExcludedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = ExcludedActivity.this;
        getWindow().setBackgroundDrawable(null);
        repository = new ExcludedRepository(getApplication());
        logsRepository = new LogsRepository(getApplication());

        setupToolbar();
        initValues();
        initOnClickListeners();
        loadData();

    }

    private void setupToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.settings_excluded_title));
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
        });
    }

    private void initValues(){
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new AppAdapter(context, repository, logsRepository);
        binding.recyclerView.setAdapter(adapter);
        adapter.setListener((b, position) -> {
            ApplicationInfo app = adapter.getApp(position);
            if (b) {
                repository.insert(new Apps(app.packageName));
            }else {
                repository.delete(new Apps(app.packageName));
            }
        });

    }

    private void initOnClickListeners(){

        binding.swipeToRefresh.setOnRefreshListener(this::loadData);

    }

    @SuppressWarnings("unchecked")
    private void loadData(){

        TaskRunner runner = new TaskRunner();
        runner.executeAsync(new AppsTask(new AppsTask.iOnDataFetched() {
            @Override
            public void showProgressBar() {
                binding.swipeToRefresh.setRefreshing(true);
                adapter.clearList();
            }

            @Override
            public void hideProgressBar() {
                binding.swipeToRefresh.setRefreshing(false);
            }

            @Override
            public void setDataInPageWithResult(Object result) {
                adapter.setDataList((List<ApplicationInfo>) result);

                if (adapter.isEmpty()){
                    binding.rlEmpty.setVisibility(View.VISIBLE);
                }else {
                    binding.rlEmpty.setVisibility(View.GONE);
                }
            }
        }, getPackageManager()));

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_righ);
    }

}
