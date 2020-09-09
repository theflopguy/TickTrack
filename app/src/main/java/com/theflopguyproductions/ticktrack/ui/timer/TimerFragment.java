package com.theflopguyproductions.ticktrack.ui.timer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.timer.activity.TimerActivity;
import com.theflopguyproductions.ticktrack.timer.data.TimerData;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackAnimator;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.ArrayList;

public class TimerFragment extends Fragment implements QuickTimerCreatorFragment.QuickTimerCreateListener {

    static TickTrackDatabase tickTrackDatabase;

    private FloatingActionButton quickTimerFab, normalTimerFab;
    private FloatingActionsMenu timerPlusFab;
    private com.google.android.material.floatingactionbutton.FloatingActionButton timerDiscardFAB;
    private boolean recyclerOn=false;

    private ArrayList<TimerData> timerDataArrayList = new ArrayList<>();
    private Activity activity;

    private String action;

    public TimerFragment() {
    }

    public TimerFragment(String action){
        this.action = action;
    }

    public static void startTimerActivity(int position, Activity context) {
        Intent timerIntent = new Intent(context, TimerActivity.class);
        timerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ArrayList<TimerData> timerData;
        timerData = tickTrackDatabase.retrieveTimerList();
        timerIntent.putExtra("timerID",timerData.get(position).getTimerID());
        context.startActivity(timerIntent);

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        assert activity != null;
        tickTrackDatabase = new TickTrackDatabase(activity);
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        if("timerCreate".equals(action)){
            addTimer();
        } else {
            displayRecyclerView();
        }

        normalTimerFab.setOnClickListener(view12 -> addTimer());
        timerDiscardFAB.setOnClickListener(view14 -> displayRecyclerView());

        requireView().setFocusableInTouchMode(true);
        requireView().requestFocus();
        requireView().setOnKeyListener((v, keyCode, event) -> {
            if( keyCode == KeyEvent.KEYCODE_BACK ) {
                if(!recyclerOn){
                    displayRecyclerView();
                    return true;
                } else {
                    requireActivity().finish();
                }
                return false;
            }
            return false;
        });

    }

    private void displayCreatorView() {
        timerPlusFab.collapse();
        timerPlusFab.setVisibility(View.GONE);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        tickTrackDatabase.setFirstTimer(true);
        transaction.replace(R.id.timerFragmentInnerFragmentContainer, new TimerCreatorFragment()).commit();
        recyclerOn=false;
    }

    private void displayRecyclerView() {
        TickTrackAnimator.fabDissolve(timerDiscardFAB);
        TickTrackAnimator.timerFabFadeIn(timerPlusFab);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.timerFragmentInnerFragmentContainer,  new TimerRecyclerFragment()).commit();
        recyclerOn=true;
    }

    private void addTimer(){
        timerPlusFab.collapse();
        TickTrackAnimator.timerFabFadeOut(timerPlusFab);
        TickTrackAnimator.fabUnDissolve(timerDiscardFAB);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        tickTrackDatabase.setFirstTimer(false);
        transaction.replace(R.id.timerFragmentInnerFragmentContainer, new TimerCreatorFragment()).commit();
        recyclerOn=false;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_timer, container, false);
        timerDiscardFAB = root.findViewById(R.id.timerCreateFragmentDiscardFAB);
        quickTimerFab = root.findViewById(R.id.quickTimerFragmentFAB);
        normalTimerFab = root.findViewById(R.id.normalTimerFragmentFAB);
        timerPlusFab = root.findViewById(R.id.multiple_actions);
        activity = getActivity();

        assert activity != null;
        tickTrackDatabase = new TickTrackDatabase(activity);
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();

        return root;
    }


    @Override
    public void onCreatedListener() {
        displayRecyclerView();
    }
}