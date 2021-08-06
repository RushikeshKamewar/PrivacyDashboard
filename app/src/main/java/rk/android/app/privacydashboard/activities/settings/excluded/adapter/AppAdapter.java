package rk.android.app.privacydashboard.activities.settings.excluded.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import rk.android.app.privacydashboard.R;
import rk.android.app.privacydashboard.activities.log.database.LogsRepository;
import rk.android.app.privacydashboard.activities.settings.excluded.database.ExcludedRepository;
import rk.android.app.privacydashboard.util.Utils;


public class AppAdapter extends RecyclerView.Adapter<AppViewHolder> implements Filterable{

    CustomItemClickListener listener;
    List<ResolveInfo> apps = new ArrayList<>();
    List<ResolveInfo> originalApps = new ArrayList<>();
    LogsRepository logsRepository;
    ExcludedRepository repository;

    Context context;
    PackageManager packageManager;

    public AppAdapter(Context context, ExcludedRepository repository, LogsRepository logsRepository){
        this.context = context;
        this.packageManager = context.getPackageManager();
        this.logsRepository = logsRepository;
        this.repository = repository;

    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.object_app, parent, false);
        final AppViewHolder viewHolder = new AppViewHolder(itemView);
        itemView.setOnClickListener(view -> viewHolder.checkBox.performClick());
        viewHolder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> listener.onItemClick(b, viewHolder.getAdapterPosition()));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {

        ResolveInfo app = apps.get(position);

        holder.textAppName.setText(app.loadLabel(packageManager));
        holder.textInfo.setText(String.valueOf(logsRepository.getLogsCountForPackage(app.activityInfo.packageName)));
        holder.textInfo.append(context.getString(R.string.settings_excluded_app_info));
        holder.imageApp.setBackground(Utils.getIconFromPackageName(context,app.activityInfo.packageName));

        holder.checkBox.setChecked(repository.isExcluded(app.activityInfo.packageName));

    }

    public ResolveInfo getApp(int position){
        return apps.get(position);
    }

    @Override
    public int getItemCount() {
        if (apps!=null)
            return apps.size();
        return 0;
    }

    public void clearList(){
        apps.clear();
        originalApps.clear();
        notifyDataSetChanged();
    }

    public void setDataList(List<ResolveInfo> list){
        apps = list;
        originalApps = list;
        notifyDataSetChanged();
    }

    public boolean isEmpty(){
        return apps.isEmpty();
    }

    public interface CustomItemClickListener {
         void onItemClick(Boolean b, int position);
    }

    public void setListener(CustomItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                List<ResolveInfo> filterResults = new ArrayList<>();

                if (charSequence == null || charSequence.length() == 0 || charSequence.toString().isEmpty()) {
                    results.count = originalApps.size();
                    results.values = originalApps;
                }else{
                    String filterStr = charSequence.toString().toLowerCase();

                    for (ResolveInfo appObject : originalApps){
                        String name = "" + appObject.loadLabel(packageManager);
                        if (name.toLowerCase().contains(filterStr)){
                            filterResults.add(appObject);
                        }
                    }

                    results.count = filterResults.size();
                    results.values = filterResults;

                }

                return results;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                apps = (List<ResolveInfo>) filterResults.values; // has the filtered values
                notifyDataSetChanged();
            }

        };
    }

}

