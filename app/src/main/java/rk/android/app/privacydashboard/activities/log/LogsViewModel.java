package rk.android.app.privacydashboard.activities.log;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import rk.android.app.privacydashboard.activities.log.database.LogsRepository;
import rk.android.app.privacydashboard.model.Logs;

public class LogsViewModel extends AndroidViewModel {

    LogsRepository repository;
    LiveData<List<Logs>> liveLogs;

    public LogsViewModel(Application application) {
        super(application);
        repository = new LogsRepository(application);
    }

    public LiveData<List<Logs>> getLogs(String permission) {
        return liveLogs = repository.getAllLogsForPermission(permission);
    }

    public void clearLogs() {
        repository.clearLogs();
    }

}
