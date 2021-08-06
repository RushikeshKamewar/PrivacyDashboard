package rk.android.app.privacydashboard.activities.appinfo.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rk.android.app.privacydashboard.R;
import rk.android.app.privacydashboard.activities.log.adapter.HeaderViewHolder;
import rk.android.app.privacydashboard.activities.log.adapter.LogsViewHolder;
import rk.android.app.privacydashboard.constant.Constants;
import rk.android.app.privacydashboard.model.Logs;
import rk.android.app.privacydashboard.util.Permissions;
import rk.android.app.privacydashboard.util.Utils;

public class AppInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private final Context context;
    private final List<Logs> logsList = new ArrayList<>();
    private final HashMap<Integer,String> dates = new HashMap<>();
    private final String packageName;
    private boolean load = false;

    public AppInfoAdapter(Context context, String packageName){
        this.context = context;
        this.packageName = packageName;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.object_logs, parent, false);
            LogsViewHolder viewHolder = new LogsViewHolder(inflatedView);
            inflatedView.setOnClickListener(v -> Utils.openAppSettings(context,logsList.get(viewHolder.getAdapterPosition()-1).packageName));
            return viewHolder;
        }

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.object_logs_header, parent, false);
        return new HeaderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof LogsViewHolder){

            LogsViewHolder logsView = (LogsViewHolder) holder;

            final Logs item = logsList.get(position-1);
            String date = Utils.getDateFromTimestamp(context,item.timestamp);

            if (!dates.containsValue(date) | dates.containsKey(position)) {
                logsView.textDate.setText(date);
                logsView.textDate.setVisibility(View.VISIBLE);
                logsView.viewHorizontal.setVisibility(View.VISIBLE);
                dates.put(position,date);
            }else {
                logsView.textDate.setVisibility(View.GONE);
                logsView.viewHorizontal.setVisibility(View.GONE);
            }

            logsView.textTime.setText(Utils.getTimeFromTimestamp(context,item.timestamp));

            logsView.textAppName.setText(Permissions.getName(context,item.permission));
            logsView.imageApp.setImageResource(Permissions.getIcon(context,item.permission));
            logsView.imageApp.setBackgroundTintList(ColorStateList.valueOf(Permissions.getColor(context, item.permission)));

            switch (item.state){
                case Constants.STATE_ON:
                    logsView.textSession.setText(context.getString(R.string.log_permission_start));
                    break;

                case Constants.STATE_OFF:
                    logsView.textSession.setText(context.getString(R.string.log_permission_stop));
                    break;

                case Constants.STATE_INVALID:
                default:
                    logsView.textSession.setText(context.getString(R.string.log_permission_invalid));
            }

        }else if (holder instanceof HeaderViewHolder){

            HeaderViewHolder headerView = (HeaderViewHolder) holder;
            headerView.icon.setImageDrawable(Utils.getIconFromPackageName(context,packageName));
                headerView.title.setText(Utils.getNameFromPackageName(context,packageName));
                headerView.info.setText(context.getString(R.string.log_info_app)
                        .replace("#ALIAS#",Utils.getNameFromPackageName(context,packageName)));
            if (load){
                headerView.progressIndicator.setVisibility(View.VISIBLE);
            }else {
                headerView.progressIndicator.setVisibility(View.GONE);
            }
        }
    }

    public void setLogsList(List<Logs> logsList) {
        this.logsList.addAll(logsList);
        notifyDataSetChanged();
    }

    public void stopLoading(){
        load = false;
        notifyItemChanged(0);
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return logsList.size() + 1;
    }

}
