package com.theflopguyproductions.ticktrack.ui.timer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.counter.CounterAdapter;
import com.theflopguyproductions.ticktrack.dialogs.DeleteCounter;
import com.theflopguyproductions.ticktrack.dialogs.DeleteTimer;
import com.theflopguyproductions.ticktrack.timer.TimerAdapter;
import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.ui.utils.deletehelper.TimerSlideDeleteHelper;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.TickTrackThemeSetter;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;

public class TimerRecyclerFragment extends Fragment implements TimerSlideDeleteHelper.RecyclerItemTouchHelperListener {


    private static ArrayList<TimerData> timerDataArrayList = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private Activity activity;
    private static RecyclerView timerRecyclerView;
    private static TextView noTimerText;
    private ConstraintLayout timerRecyclerRootLayout;
    private static TimerAdapter timerAdapter;

    public static void deleteTimer(int position, Activity activity, String timerName) {
        deleteItem(position, activity);
        Toast.makeText(activity, "Deleted Timer " + timerName, Toast.LENGTH_SHORT).show();
    }

    public static void deleteItem(int position, Activity activity){
        timerDataArrayList.remove(position);
        TickTrackDatabase.storeTimerList(timerDataArrayList, activity);
        buildRecyclerView(activity);
        timerRecyclerView.scheduleLayoutAnimation();

    }

    public static void refreshRecyclerView() {
        timerAdapter.notifyDataSetChanged();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_timer_recycler, container, false);
        activity = getActivity();

        assert activity != null;
        timerDataArrayList = TickTrackDatabase.retrieveTimerList(activity);
        timerRecyclerView = root.findViewById(R.id.timerFragmentRecyclerView);

        noTimerText = root.findViewById(R.id.timerFragmentNoTimerText);
        timerRecyclerRootLayout = root.findViewById(R.id.timerRecyclerRootLayout);

        TickTrackThemeSetter.timerRecycleTheme(activity, timerRecyclerView);
        buildRecyclerView(activity);

        //TickTrackThemeSetter.counterFragmentTheme(getActivity(), counterRecyclerView, counterFragmentRootLayout, noCounterText);


        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new TimerSlideDeleteHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(timerRecyclerView);
        sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        return root;
    }

    private static void buildRecyclerView(Activity activity) {
        timerAdapter = new TimerAdapter(timerDataArrayList);

        if(timerDataArrayList.size()>0){
            timerRecyclerView.setVisibility(View.VISIBLE);
            noTimerText.setVisibility(View.INVISIBLE);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
            timerRecyclerView.setItemAnimator(new DefaultItemAnimator());
            timerRecyclerView.setHasFixedSize(true);

            Collections.sort(timerDataArrayList);

            TickTrackDatabase.storeTimerList(timerDataArrayList, activity);

            timerRecyclerView.setLayoutManager(layoutManager);
            timerRecyclerView.setAdapter(timerAdapter);
            timerAdapter.notifyDataSetChanged();
            timerRecyclerView.scheduleLayoutAnimation();

        } else {
            timerRecyclerView.setVisibility(View.INVISIBLE);
            noTimerText.setVisibility(View.VISIBLE);
        }

    }

    String deletedTimer = null;
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof TimerAdapter.RecyclerItemViewHolder) {
            deletedTimer = timerDataArrayList.get(viewHolder.getAdapterPosition()).getTimerLabel();
            position = viewHolder.getAdapterPosition();

            DeleteTimer timerDelete = new DeleteTimer(activity);
            timerDelete.show();
            int finalPosition = position;
            if(!deletedTimer.equals("Set label")){
                timerDelete.dialogMessage.setText("Delete timer "+deletedTimer+"?");
            } else {
                timerDelete.dialogMessage.setText("Delete timer ?");
            }
            timerDelete.yesButton.setOnClickListener(view -> {
                TimerRecyclerFragment.deleteTimer(finalPosition, activity, deletedTimer);
                timerDelete.dismiss();
            });
            timerDelete.noButton.setOnClickListener(view -> {
                TimerRecyclerFragment.refreshRecyclerView();
                timerDelete.dismiss();
            });
            timerDelete.setOnCancelListener(dialogInterface -> {
                TimerRecyclerFragment.refreshRecyclerView();
                timerDelete.cancel();
            });

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = (sharedPreferences, s) ->  {
        timerDataArrayList = TickTrackDatabase.retrieveTimerList(activity);
        if (s.equals("TimerData")){
            buildRecyclerView(getActivity());
        }
    };


}