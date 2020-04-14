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
    androidx.fragment.app.FragmentManager fm;
    private Fragment active;
    private Fragment fragment1;
    private Fragment fragment2;
    private Fragment fragment3;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        fragment1 = new HomeFragment ();
        fragment2 = new CompletedTaskFragment ();
        fragment3 = new ProfileFragment ();
        active = fragment1;
        fm = getSupportFragmentManager ();
        fm.beginTransaction ().add (R.id.fragmentcontainer, fragment3, "3").hide (fragment3).commit ();
        fm.beginTransaction ().add (R.id.fragmentcontainer, fragment2, "2").hide (fragment2).commit ();
        fm.beginTransaction ().add (R.id.fragmentcontainer, fragment1, "1").commit ();

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
                R.drawable.ic_home, getString (R.string.Home),
                getString (R.color.colorItem1Background), getString (R.color.Black));

        CustomBottomItem Com_task = new CustomBottomItem (COMTASK, R.drawable.icon_completed,
                getString (R.string.ComTask), getString (R.color.colorItem2Background),
                getString (R.color.Black));

        CustomBottomItem profile = new CustomBottomItem (PROFILE, R.drawable.icon_profile,
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

                fm.beginTransaction ().hide (active).show (fragment1).commit ();
                active = fragment1;
                try {
                    fragment1.onStart ();
                } catch (Exception e) {
                    e.printStackTrace ();
                }

                break;

            case COMTASK:

                fm.beginTransaction ().hide (active).show (fragment2).commit ();
                active = fragment2;
                try {
                    fragment2.onStart ();
                } catch (Exception e) {
                    e.printStackTrace ();
                }

                break;
            case PROFILE:

                fm.beginTransaction ().hide (active).show (fragment3).commit ();
                active = fragment3;
                try {
                    fragment3.onStart ();

                } catch (Exception e) {
                    e.printStackTrace ();
                }

                break;
        }
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