package rk.android.app.privacydashboard.activities.settings.excluded.async;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

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
        Intent intent = new Intent(Intent.ACTION_MAIN, null)
                .addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = packageManager.queryIntentActivities(intent,0);
        apps.sort(new ResolveInfo.DisplayNameComparator(packageManager));

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