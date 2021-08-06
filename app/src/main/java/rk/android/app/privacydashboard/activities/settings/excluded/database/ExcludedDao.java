package rk.android.app.privacydashboard.activities.settings.excluded.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import rk.android.app.privacydashboard.model.Apps;

@Dao
public interface ExcludedDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Apps apps);

    @Update
    void update(Apps apps);

    @Delete
    void delete(Apps apps);

    @Query("DELETE FROM exception_table")
    void clearAll();

    @Query("SELECT * FROM exception_table")
    LiveData<List<Apps>> getAllApps();

    @Query("SELECT EXISTS (SELECT * FROM exception_table WHERE packageName=:packageName)")
    boolean isExcluded(String packageName);

    @Query("SELECT COUNT(*) FROM exception_table")
    int getCount();

    @Query("SELECT DISTINCT packageName FROM exception_table")
    List<String> getPackages();



}
