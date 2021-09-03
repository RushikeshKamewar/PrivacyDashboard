package rk.android.app.privacydashboard.activities.settings.excluded.async;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.List;

import rk.android.app.privacydashboard.helper.async.BaseTask;

@SuppressWarnings("rawtypes")
public class AppsTask extends BaseTask {

    private final iOnDataFetched listener;
    PackageManager packageManager;

    public AppsTask(iOnDataFetched onDataFetchedListener, PackageManager packageManager) {
        this.listener = onDataFetchedListener;
        this.packageManager = packageManager;
    }

    @Override
    public Object call() {
        List<ApplicationInfo> apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        apps.sort((o1, o2) -> o1.loadLabel(packageManager).toString().toLowerCase()
                .compareTo(o2.loadLabel(packageManager).toString().toLowerCase()));
        return apps;
    }

    @Override
    public void setUiForLoading() {
        listener.showProgressBar();
    }

    @Override
    public void setDataAfterLoading(Object result) {
        listener.setDataInPageWithResult(result);
        listener.hideProgressBar();
    }

    public interface iOnDataFetched {
        void showProgressBar();
        void hideProgressBar();
        void setDataInPageWithResult(Object result);
    }

}