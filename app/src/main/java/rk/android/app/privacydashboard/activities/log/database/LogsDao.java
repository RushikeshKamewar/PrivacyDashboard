package rk.android.app.privacydashboard.activities.log.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import rk.android.app.privacydashboard.model.Logs;

@Dao
public interface LogsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertLogs(Logs logs);

    @Update
    void update(Logs logs);

    @Delete
    void delete(Logs logs);

    @Query("DELETE FROM logs_table")
    void clearLogs();

    @Query("SELECT * FROM logs_table ORDER BY timestamp DESC")
    LiveData<List<Logs>> getAllLogs();

    @Query("SELECT * FROM logs_table WHERE permission=:permission ORDER BY timestamp DESC")
    LiveData<List<Logs>> getAllLogsForPermission(String permission);

    @Query("SELECT * FROM logs_table WHERE packageName=:packageName ORDER BY timestamp DESC")
    LiveData<List<Logs>> getAllLogsForPackage(String packageName);

    @Query("SELECT * FROM logs_table WHERE permission=:date ORDER BY timestamp DESC")
    LiveData<List<Logs>> getAllLogsForDate(String date);

    @Query("SELECT COUNT(*) FROM logs_table WHERE permission=:permission")
    int getLogsCount(String permission);

    @Query("SELECT COUNT(*) FROM logs_table WHERE packageName=:packageName")
    int getLogsCountForPackage(String packageName);

    @Query("SELECT COUNT(*) FROM (SELECT DISTINCT packageName FROM logs_table WHERE permission=:permission and date=:date)")
    int getLogsCount(String permission, String date);

    @Query("DELETE FROM logs_table WHERE permission=:permission")
    void clearLogs(String permission);

    @Query("DELETE FROM logs_table WHERE packageName=:packageName")
    void clearAppLogs(String packageName);

}
