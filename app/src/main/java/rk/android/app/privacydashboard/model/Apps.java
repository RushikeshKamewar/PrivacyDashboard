package rk.android.app.privacydashboard.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "exception_table")
public class Apps {

    @PrimaryKey @NonNull
    public String packageName;

    public Apps(@NonNull String packageName){
        this.packageName = packageName;
    }

}
