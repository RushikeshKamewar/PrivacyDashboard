package rk.android.app.privacydashboard.activities.appinfo;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import rk.android.app.privacydashboard.activities.log.database.LogsRepository;
import rk.android.app.privacydashboard.model.Logs;

public class AppInfoViewModel extends AndroidViewModel {

    LogsRepository repository;
    LiveData<List<Logs>> liveLogs;

    public AppInfoViewModel(Application application) {
        super(application);
        repository = new LogsRepository(application);
    }

    public void insertLogs(Logs logs){
        repository.insertLogs(logs);
    }

    public LiveData<List<Logs>> getAllLogsForPackage(String packageName) {
        return liveLogs = repository.getAllLogsForPackage(packageName);
    }

    public void clearLogs() {
        repository.clearLogs();
    }

}
