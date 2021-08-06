package rk.android.app.privacydashboard.activities.log.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import rk.android.app.privacydashboard.R;

public class HeaderViewHolder extends RecyclerView.ViewHolder {

    public ImageView icon;
    public TextView title, info;
    public LinearProgressIndicator progressIndicator;

    public HeaderViewHolder(View itemView) {
        super(itemView);

        icon = itemView.findViewById(R.id.image_icon);

        title = itemView.findViewById(R.id.text_title);
        info = itemView.findViewById(R.id.text_info);

        progressIndicator = itemView.findViewById(R.id.progress_horizontal);

    }
}
