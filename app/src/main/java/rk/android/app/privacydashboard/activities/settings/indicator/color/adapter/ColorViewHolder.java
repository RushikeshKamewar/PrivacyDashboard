package rk.android.app.privacydashboard.activities.settings.indicator.color.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import rk.android.app.privacydashboard.R;

public class ColorViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageColor, selectIcon;

    public ColorViewHolder(View itemView) {
        super(itemView);
        imageColor = itemView.findViewById(R.id.image_color);
        selectIcon = itemView.findViewById(R.id.image_select);
    }
}
