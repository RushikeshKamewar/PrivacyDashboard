package rk.android.app.privacydashboard.activities.log.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rk.android.app.privacydashboard.model.Logs;

@Database(entities = {Logs.class}, version = 1)
public abstract class LogsDatabase extends RoomDatabase {

    private static final int NUMBER_OF_THREADS = 4;

    private static LogsDatabase instance;
    public abstract LogsDao logsDao();

    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static synchronized LogsDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    LogsDatabase.class,"logs_database")
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
