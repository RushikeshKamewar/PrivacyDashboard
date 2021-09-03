package rk.android.app.privacydashboard.activities.settings.indicator.color.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import rk.android.app.privacydashboard.R;


public class ColorAdapter extends RecyclerView.Adapter<ColorViewHolder> {

    private final CustomItemClickListener listener;
    private final List<Integer> colors =  new ArrayList<>();
    public int selected = -1;

    public ColorAdapter(CustomItemClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color, parent, false);
        ColorViewHolder viewHolder = new ColorViewHolder(itemView);
        itemView.setOnClickListener(v -> {
            setSelected(viewHolder.getAdapterPosition());
            listener.onItemClick(v, viewHolder.getAdapterPosition());
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        holder.imageColor.setImageTintList(ColorStateList.valueOf(colors.get(position)));
        if (selected == position){
            holder.selectIcon.setVisibility(View.VISIBLE);
        }else {
            holder.selectIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void clear(){
        this.colors.clear();
        notifyDataSetChanged();
    }

    public void addColor(int color){
        this.colors.add(color);
        notifyItemChanged(colors.size()-1);
    }

    public void setSelected(int selected) {
        int lastSelected = this.selected;
        this.selected = selected;
        if (lastSelected!=-1)
            notifyItemChanged(lastSelected);
        notifyItemChanged(selected);
    }

    public void clearSelection(){
        selected = -1;
        notifyDataSetChanged();
    }

    public int getSelected() {
        return colors.get(selected);
    }

    public void setColors(List<Integer> colors) {
        this.colors.addAll(colors);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    public interface CustomItemClickListener {
        void onItemClick(View v, int position);
    }

}
