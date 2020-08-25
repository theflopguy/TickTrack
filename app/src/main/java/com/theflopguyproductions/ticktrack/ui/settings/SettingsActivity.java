package com.theflopguyproductions.ticktrack.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;

public class SettingsActivity extends AppCompatActivity {

    private TickTrackDatabase tickTrackDatabase;

    private ConstraintLayout themeLayout, backupLayout;
    private TextView themeName, themeTitle, backupTitle, backupEmail;
    private ImageButton backButton;
    private ScrollView settingsScrollView;
    private int prevFragment = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        themeLayout = findViewById(R.id.themeSettingsLayout);
        themeTitle = findViewById(R.id.themeSettingsLabel);
        themeName = findViewById(R.id.themeValueSettingsTextView);
        backButton = findViewById(R.id.settingsActivityBackButton);
        settingsScrollView = findViewById(R.id.settingsActivityScrollView);
        backupLayout = findViewById(R.id.backupSettingsLayout);
        backupTitle = findViewById(R.id.backupTitleSettingsTextView);
        backupEmail = findViewById(R.id.backupEmailSettingsTextView);

        tickTrackDatabase = new TickTrackDatabase(this);

        prevFragment = tickTrackDatabase.retrieveCurrentFragmentNumber();

        TickTrackThemeSetter.settingsActivityTheme(this, themeTitle, themeName, settingsScrollView, themeLayout,
                tickTrackDatabase,backupTitle, backupEmail, backupLayout);

        themeLayout.setOnClickListener(view -> {
            ThemeDialog themeDialog = new ThemeDialog(this, tickTrackDatabase.getThemeMode());
            themeDialog.show();
        });

        backButton.setOnClickListener(view -> onBackPressed());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, SoYouADeveloperHuh.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        tickTrackDatabase.storeCurrentFragmentNumber(prevFragment);
    }
}