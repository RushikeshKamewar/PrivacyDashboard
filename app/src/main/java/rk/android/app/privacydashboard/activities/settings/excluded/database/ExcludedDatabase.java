package rk.android.app.privacydashboard.activities.settings.excluded.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rk.android.app.privacydashboard.model.Apps;

@Database(entities = {Apps.class}, version = 1)
public abstract class ExcludedDatabase extends RoomDatabase{

    private static final int NUMBER_OF_THREADS = 4;

    private static ExcludedDatabase instance;
    public abstract ExcludedDao exceptionsDao();

    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static synchronized ExcludedDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ExcludedDatabase.class,"exception_table")
                    .allowMainThreadQueries()
                    .addCallback(databaseCallback)
                    .build();
        }
        return instance;
    }

    static RoomDatabase.Callback databaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

        }
    };
}
