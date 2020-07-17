package com.theflopguyproductions.ticktrack.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.home.HomeFragment;

import java.util.Objects;

public class AlarmDelete  extends Dialog {
    public Activity activity;
    int position;
    private String alarmLabel;
    RecyclerView.ViewHolder viewHolder;
    TextView dialogMessage;

    public AlarmDelete(Activity activity, int position, String counterName, RecyclerView.ViewHolder viewHolder) {
        super(activity);
        this.viewHolder = viewHolder;
        this.alarmLabel = counterName;
        this.position = position;
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = activity.getLayoutInflater().inflate(R.layout.alert_delete_dialog, new CoordinatorLayout(activity), false);
        setContentView(view);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        yesButton = view.findViewById(R.id.yesButton);
        noButton = view.findViewById(R.id.noButton);
        dialogMessage = view.findViewById(R.id.deleteDialogMessage);
        dialogMessage.setText("Delete alarm " + alarmLabel + "?");

        getWindow().getAttributes().windowAnimations = R.style.createdDialog;

        yesButton.setOnClickListener(view12 -> {
            HomeFragment.yesToDelete(position, activity, alarmLabel);
            dismiss();
        });
        noButton.setOnClickListener(view1 -> {
            HomeFragment.noToDelete(viewHolder);
            dismiss();
        });
        setOnCancelListener(dialogInterface -> {
            HomeFragment.noToDelete(viewHolder);
            dismiss();
        });
    }

    public Button yesButton;
    public Button noButton;

}