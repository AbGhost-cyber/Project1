package com.example.taskmanagerpro.ui;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.taskmanagerpro.R;
import com.example.taskmanagerpro.adapter.ItemAdapter;
import com.example.taskmanagerpro.data.CustomBottomBar;
import com.example.taskmanagerpro.data.CustomBottomItem;
import com.example.taskmanagerpro.fragments.CompletedTaskFragment;
import com.example.taskmanagerpro.fragments.HomeFragment;
import com.example.taskmanagerpro.fragments.ProfileFragment;

import hotchemi.android.rate.AppRate;

public class MainActivity extends AppCompatActivity implements ItemAdapter.ItemSelectorInterface {
    private CustomBottomBar customBottomBar;
    public static final int HOME = 0;
    public static final int COMTASK = 1;
    public static final int PROFILE = 2;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        //show rate app prompt
        AppRate.with (this)
                .setInstallDays (1)
                .setLaunchTimes (3)
                .setRemindInterval (2)
                .monitor ();

        AppRate.showRateDialogIfMeetsConditions (this);


        customBottomBar = new CustomBottomBar (this,
                findViewById (R.id.customBottomBar),
                MainActivity.this);
        initItems ();
        customBottomBar.changeBackground (getString (R.color.colorItemDefaultBackground));
        customBottomBar.setDefaultBackground (getString (R.color.colorItemDefaultBackground));
        customBottomBar.setDefaultTint (getString (R.color.colorItemDefaultTint));
        customBottomBar.changeDividerColor (getString (R.color.colorDivider));
        customBottomBar.hideDivider ();
        customBottomBar.apply (HOME);
    }

    @SuppressLint("ResourceType")
    private void initItems() {
        CustomBottomItem Home = new CustomBottomItem (HOME,
                R.drawable.ic_home_black_24dp, getString (R.string.Home),
                getString (R.color.Black), getString (R.color.white));

        CustomBottomItem Com_task = new CustomBottomItem (COMTASK, R.drawable.completed_24dp,
                getString (R.string.ComTask), getString (R.color.colorItem2Background),
                getString (R.color.Black));

        CustomBottomItem profile = new CustomBottomItem (PROFILE, R.drawable.ic_person_black_24dp,
                getString (R.string.Profile), getString (R.color.colorItem3Background),
                getString (R.color.Black));

        customBottomBar.addItem (Home);
        customBottomBar.addItem (Com_task);
        customBottomBar.addItem (profile);
    }

    @Override
    public void itemSelect(int selectedID) {
        switch (selectedID) {
            case HOME:
                //todo do something, when home is selected

                SetupFragment (new HomeFragment ());
                break;

            case COMTASK:
                //todo do something, when comtask is selected

                SetupFragment (new CompletedTaskFragment ());
                break;
            case PROFILE:
                //todo do something, when Profile is selected

                SetupFragment (new ProfileFragment ());
                break;
        }
    }

    private void SetupFragment(Fragment fragment) {
        getSupportFragmentManager ().beginTransaction ().replace (R.id.fragmentcontainer,
                fragment).commit ();
    }

    // handles on hard ware backpressed..
    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager ();
        if (fm.getBackStackEntryCount () > 0) {
            fm.popBackStack ();
        } else {
            super.onBackPressed ();
        }
    }


}