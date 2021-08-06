package rk.android.app.privacydashboard.activities.log.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import rk.android.app.privacydashboard.model.Logs;

public class LogsRepository {

    LogsDao logsDao;
    LiveData<List<Logs>> liveLogs;

    public LogsRepository(Application application){
        LogsDatabase database = LogsDatabase.getInstance(application);
        logsDao = database.logsDao();
        liveLogs = logsDao.getAllLogs();
    }

    public LiveData<List<Logs>> getLogs() {
        return liveLogs;
    }

    public LiveData<List<Logs>> getAllLogsForPackage(String packageName) {
        liveLogs = logsDao.getAllLogsForPackage(packageName);
        return liveLogs;
    }

    public LiveData<List<Logs>> getAllLogsForPermission(String permission) {
        liveLogs = logsDao.getAllLogsForPermission(permission);
        return liveLogs;
    }

    public int getLogsCount(String permission){
        return logsDao.getLogsCount(permission);
    }

    public int getLogsCountForPackage(String packageName){
        return logsDao.getLogsCountForPackage(packageName);
    }

    public int getLogsCount(String permission, String date){
        return logsDao.getLogsCount(permission, date);
    }

    public void insertLogs(Logs logs) {
        LogsDatabase.databaseWriteExecutor.execute(() -> {
            logsDao.insertLogs(logs);
        });
    }

    public void clearLogs() {
        LogsDatabase.databaseWriteExecutor.execute(() -> {
            logsDao.clearLogs();
        });
    }

    public void clearLogs(String permission) {
        LogsDatabase.databaseWriteExecutor.execute(() -> {
            logsDao.clearLogs(permission);
        });
    }

    public void clearAppLogs(String packageName) {
        LogsDatabase.databaseWriteExecutor.execute(() -> {
            logsDao.clearAppLogs(packageName);
        });
    }
}
