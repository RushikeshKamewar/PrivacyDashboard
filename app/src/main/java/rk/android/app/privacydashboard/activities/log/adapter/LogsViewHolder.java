package rk.android.app.privacydashboard.activities.log.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import rk.android.app.privacydashboard.R;

public class LogsViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageApp, imageHelp;
    public TextView textDate, textTime, textSession,textAppName;
    public View viewVertical, viewHorizontal;

    public LogsViewHolder(View itemView) {
        super(itemView);

        imageApp = itemView.findViewById(R.id.image_app);
        imageHelp = itemView.findViewById(R.id.image_help);

        textDate = itemView.findViewById(R.id.text_date);
        textTime = itemView.findViewById(R.id.text_time);
        textAppName = itemView.findViewById(R.id.text_app_name);
        textSession = itemView.findViewById(R.id.text_session);

        viewHorizontal = itemView.findViewById(R.id.view_horizontal);
        viewVertical = itemView.findViewById(R.id.view_vertical);

    }
}
