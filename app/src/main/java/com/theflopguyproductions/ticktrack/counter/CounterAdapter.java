package com.theflopguyproductions.ticktrack.counter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.counter.CounterFragment;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.TimeAgo;

import java.util.ArrayList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class CounterAdapter extends RecyclerView.Adapter<CounterAdapter.counterDataViewHolder> {

    private ArrayList<CounterData> counterDataArrayList;
    private Context context;

    public CounterAdapter(Context context, ArrayList<CounterData> counterDataArrayList){
        this.context = context;
        this.counterDataArrayList = counterDataArrayList;
    }

    private void setTheme(counterDataViewHolder holder, int theme) {
        if(theme == 1){
            holder.counterLayout.setBackgroundResource(R.drawable.recycler_layout_light);
            holder.counterLabel.setTextColor(holder.context.getResources().getColor(R.color.DarkText));
            holder.lastModified.setTextColor(holder.context.getResources().getColor(R.color.LightDarkText));
        } else {
            holder.counterLayout.setBackgroundResource(R.drawable.recycler_layout_dark);
            holder.counterLabel.setTextColor(holder.context.getResources().getColor(R.color.LightText));
            holder.lastModified.setTextColor(holder.context.getResources().getColor(R.color.DarkLightText));
        }
        holder.countValue.setTextColor(holder.context.getResources().getColor(R.color.Accent));
    }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "B");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "q");
        suffixes.put(1_000_000_000_000_000_000L, "Q");
    }
    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    private void setColor(counterDataViewHolder holder) {
        if(holder.itemColor==1){
            holder.counterFlag.setImageResource(R.drawable.ic_flag_red);
        }
        else if(holder.itemColor==2){
            holder.counterFlag.setImageResource(R.drawable.ic_flag_green);
        }
        else if(holder.itemColor==3){
            holder.counterFlag.setImageResource(R.drawable.ic_flag_orange);
        }
        else if(holder.itemColor==4){
            holder.counterFlag.setImageResource(R.drawable.ic_flag_purple);
        }
        else if(holder.itemColor==5){
            holder.counterFlag.setImageResource(R.drawable.ic_flag_blue);
        } else {
            holder.counterFlag.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public counterDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView;

        if(viewType == R.layout.counter_item_layout){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.counter_item_layout, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_footer_layout, parent, false);
        }

        return new counterDataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull counterDataViewHolder holder, int position) {

        int currentTheme = holder.tickTrackDatabase.getThemeMode();

        if(position == counterDataArrayList.size()) {
            if(currentTheme == 1){
                holder.footerCounterTextView.setTextColor(holder.context.getResources().getColor(R.color.LightDarkText));
            } else {
                holder.footerCounterTextView.setTextColor(holder.context.getResources().getColor(R.color.DarkLightText));
            }
            Resources resources = holder.context.getResources();
            String[] footerArray = resources.getStringArray(R.array.footer_string_array);
            int randomFooter = new Random().nextInt(footerArray.length);
            holder.footerCounterTextView.setText(footerArray[randomFooter]);
        } else {
            long currentValue = counterDataArrayList.get(position).getCounterValue();
            holder.countValue.setText(format(currentValue));
            holder.counterLabel.setText(counterDataArrayList.get(position).getCounterLabel());

            if(counterDataArrayList.get(position).getCounterTimestamp()!=-1){
                holder.lastModified.setText("Last edited: "+ TimeAgo.getTimeAgo(counterDataArrayList.get(position).getCounterTimestamp()));
            }

            holder.itemColor = counterDataArrayList.get(position).getCounterFlag();
            setColor(holder);
            setTheme(holder, currentTheme);

            holder.counterLayout.setOnClickListener(v -> {
                CounterFragment.startCounterActivity(counterDataArrayList.get(holder.getAdapterPosition()).getCounterID(), (Activity) holder.context);
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == counterDataArrayList.size()) ? R.layout.recycler_footer_layout : R.layout.counter_item_layout;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return counterDataArrayList.size()+1;
    }

    public void updateData(ArrayList<CounterData> counterDataArrayList){
        this.counterDataArrayList.clear();
        this.counterDataArrayList.addAll(counterDataArrayList);
    }

    public void diffUtilsChangeData(ArrayList<CounterData> counterDataArrayList){

        CounterDiffUtilCallback counterDiffUtilCallback = new CounterDiffUtilCallback(counterDataArrayList, this.counterDataArrayList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(counterDiffUtilCallback, false);
        diffResult.dispatchUpdatesTo(this);
        this.counterDataArrayList = counterDataArrayList;

    }

    public static class counterDataViewHolder extends RecyclerView.ViewHolder {

        private TextView countValue, lastModified, counterLabel;
        public ConstraintLayout counterLayout;
        private int itemColor;
        private ImageView counterFlag;
        private Context context;
        private TextView footerCounterTextView;
        TickTrackDatabase tickTrackDatabase;

        public counterDataViewHolder(@NonNull View parent) {
            super(parent);

            countValue = parent.findViewById(R.id.counterValueItemTextView);
            counterLabel = parent.findViewById(R.id.counterLabelItemTextView);
            lastModified = parent.findViewById(R.id.counterLastUpdateItemTextView);
            counterLayout = parent.findViewById(R.id.counterItemRootLayout);
            counterFlag = parent.findViewById(R.id.counterFlagItemImageView);
            footerCounterTextView = parent.findViewById(R.id.recylerFooterTextView);

            context=parent.getContext();
            tickTrackDatabase = new TickTrackDatabase(context);

        }
    }
}
