package rk.android.app.privacydashboard.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "logs_table")
public class Logs {

    @PrimaryKey
    public long timestamp;
    public String packageName;
    public String permission;
    public int state;
    public String date;

    public Logs(long timestamp, String packageName, String permission, int state, String date) {
        this.timestamp = timestamp;
        this.packageName = packageName;
        this.permission = permission;
        this.state = state;
        this.date = date;
    }

}
