package rk.android.app.privacydashboard.activities.settings.excluded.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import rk.android.app.privacydashboard.R;

public class AppViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageApp;
    public TextView textAppName, textInfo;
    public CheckBox checkBox;

    public AppViewHolder(View itemView) {
        super(itemView);

        imageApp = itemView.findViewById(R.id.image_app);

        textAppName = itemView.findViewById(R.id.text_app_name);
        textInfo = itemView.findViewById(R.id.text_app_info);

        checkBox = itemView.findViewById(R.id.checkbox_select);

    }
}
