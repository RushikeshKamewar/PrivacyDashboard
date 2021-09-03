package rk.android.app.privacydashboard.activities.settings.indicator.color;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import rk.android.app.privacydashboard.R;
import rk.android.app.privacydashboard.activities.settings.indicator.color.adapter.ColorAdapter;
import rk.android.app.privacydashboard.constant.Constants;
import rk.android.app.privacydashboard.manager.PreferenceManager;

public class ColorDialog {

    public static void showSelection(Context context, LayoutInflater inflater,
                                     PreferenceManager preferenceManager, colorSelection listener){

        final View dialogView = inflater.inflate(R.layout.dialog_color, null);

        AlertDialog dialogs = new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .setCancelable(true)
                .show();

        dialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerView);

        ColorAdapter adapter = new ColorAdapter((v, position) -> {
            listener.selectedColor(Constants.DOTS_COLOR_BASIC,Color.parseColor(Constants.BASIC_COLORS[position]));
            dialogs.dismiss();
        });

        recyclerView.setLayoutManager(new GridLayoutManager(context,4));
        recyclerView.setAdapter(adapter);
        adapter.clear();

        for (String s : Constants.BASIC_COLORS) {
            int color = Color.parseColor(s);
            adapter.addColor(color);
            if (preferenceManager.getColorType().equals(Constants.DOTS_COLOR_BASIC) &&
                    preferenceManager.getIconColor() == color)
                adapter.setSelected(adapter.getItemCount()-1);
        }
    }


    public interface colorSelection {
        void selectedColor(String type, int color);
    }

}
