package com.example.taskmanagerpro.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskmanagerpro.R;
import com.example.taskmanagerpro.data.MyTask;
import com.example.taskmanagerpro.data.TaskViewModel;
import com.example.taskmanagerpro.ui.CreateTaskActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class HomeFragment extends Fragment {
    private static final int ADD_NOTE_REQUEST = 1;
    private static final int EDIT_NOTE_REQUEST = 2;
    private TaskViewModel taskViewModel;
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private TextView DisplayName, Date;
    private TextView EndOfPage;
    private Calendar calendar;
    private RelativeLayout emptyRecView;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate (R.layout.home_activity, container, false);

        //initialize mobile ads
        MobileAds.initialize (getContext (), initializationStatus -> {

        });
        //initial interstitial ad
        mInterstitialAd=new InterstitialAd (Objects.requireNonNull (getContext ()));
        //set unique ad id
        mInterstitialAd.setAdUnitId ("ca-app-pub-7292512767354152/9897483548");
        mInterstitialAd.loadAd (new AdRequest.Builder ().build ());

        // set adlistener to reload new ad
        mInterstitialAd.setAdListener (new AdListener (){
            @Override
            public void onAdClosed(){
                mInterstitialAd.loadAd (new AdRequest.Builder ().build ());
            }
        });
        FloatingActionButton buttonAddTask = v.findViewById (R.id.button_add_task);
        SearchView searchView = v.findViewById (R.id.SearchView);

        mAdView = v.findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance ();
        DatabaseReference databaseReference = firebaseDatabase.getReference ("Users");
        Date = v.findViewById (R.id.currentDate);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance ();
        FirebaseUser user = firebaseAuth.getCurrentUser ();
        calendar = Calendar.getInstance ();
        EndOfPage = v.findViewById (R.id.endPage);
        DisplayName = v.findViewById (R.id.welcomeName);
        emptyRecView=v.findViewById (R.id.emptyRecView);

        retrieveinfos ();


        //gets user's username from database and display in accordance with the current time
        Query query;
        if (user != null) {
            query = databaseReference.orderByChild ("email").equalTo (user.getEmail ());

            query.addValueEventListener (new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot d : dataSnapshot.getChildren ()) {
                        try {
                            if (HasActiveNetworkConnection (Objects.requireNonNull (getContext ()))) {

                                String username = "Welcome," + d.child ("username").getValue ();
                                String date = DateFormat.getDateInstance (DateFormat.LONG).format (calendar.getTime ());
                                DisplayName.setText (String.format ("%s", username));
                                Date.setText (date);
                                DisplayName.setVisibility (View.VISIBLE);
                                Date.setVisibility (View.VISIBLE);

                                try {
                                    SharedPreferences sharedPreferences= Objects.requireNonNull (getActivity ())
                                            .getPreferences (Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor=sharedPreferences.edit ();
                                    editor.putString ("username",username);
                                    editor.putString ("date",date);
                                    editor.apply ();
                                } catch (Exception e) {
                                    e.printStackTrace ();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace ();
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


        //search bar algorithm
        searchView.setQueryHint ("Search");
        searchView.setOnQueryTextListener (new SearchView.OnQueryTextListener () {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter ().filter (newText);
                return false;
            }

        });

        //navigates user to create task activity
        buttonAddTask.setOnClickListener (v12 -> {
            Intent myintent = new Intent (getActivity (), CreateTaskActivity.class);
            startActivityForResult (myintent, ADD_NOTE_REQUEST);
        });
        //set up recyclerview
        recyclerView = v.findViewById (R.id.TaskRecycler);
        LinearLayoutManager myManager = new LinearLayoutManager (getActivity ());
        recyclerView.setLayoutManager (myManager);
        recyclerView.setHasFixedSize (true);


        adapter = new TaskAdapter ();
        recyclerView.setAdapter (adapter);


        taskViewModel = ViewModelProviders.of (this).get (TaskViewModel.class);
        taskViewModel.getAllTasks ().observe (this, myTasks -> {
            //check if the list containing our model class object is null
            //if false,then display the "end of page" text if the list contains something
            if (myTasks.size () <= 0) {
                emptyRecView.setVisibility (View.VISIBLE);
                EndOfPage.setVisibility (View.INVISIBLE);
            }
            else{
                emptyRecView.setVisibility (View.GONE);
            }
            adapter.setTasks (myTasks);
            adapter.notifyDataSetChanged ();
        });


        //swipe delete function
        new ItemTouchHelper (new ItemTouchHelper.SimpleCallback (0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                if (direction == ItemTouchHelper.LEFT) {
                    final int adapterPosition = viewHolder.getAdapterPosition ();
                    final MyTask deletedTask = adapter.getTaskAt (adapterPosition);
                    taskViewModel.delete (deletedTask);


                    Snackbar.make (recyclerView, "Task deleted", Snackbar.LENGTH_LONG)
                            .setActionTextColor (getResources ().getColor (R.color.white))
                            .setAction ("Undo", v1 -> {
                                taskViewModel.insert (deletedTask);
                                adapter.notifyDataSetChanged ();
                                EndOfPage.setVisibility (View.VISIBLE);
                                adapter.notifyItemChanged (adapterPosition);
                            })
                            .show ();
                    return;
                }

                TaskAdapter.TaskHolder taskHolder = (TaskAdapter.TaskHolder) viewHolder;


            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder (c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor (ContextCompat.getColor (Objects.requireNonNull (getActivity ()), R.color.red))
                        .addActionIcon (R.drawable.ic_delete_black_24dp)
                        .addSwipeLeftLabel ("delete")
                        .setSwipeLeftLabelColor (R.color.yellow)
                        .create ()
                        .decorate ();
                super.onChildDraw (c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView (recyclerView);

        //on item click listener
        adapter.setOnItemClickListener (task -> {
            Intent intent1 = new Intent (HomeFragment.this.getActivity (), CreateTaskActivity.class);
            intent1.putExtra (CreateTaskActivity.EXTRA_ID, task.getId ());
            intent1.putExtra (CreateTaskActivity.EXTRA_TITLE, task.getTitleTask ());
            intent1.putExtra (CreateTaskActivity.EXTRA_DESC, task.getDescription ());
            intent1.putExtra (CreateTaskActivity.EXTRA_TIME, task.getTaskTime ());

            HomeFragment.this.startActivityForResult (intent1, EDIT_NOTE_REQUEST);

        });
        return v;

    }

    //retrieve save username & current date from sharedpreferences

    public void retrieveinfos(){
        String username= null;
        String date= null;
        try {
            SharedPreferences sharedPreferences= Objects.requireNonNull (getActivity ()).getPreferences (Context.MODE_PRIVATE);
            username = sharedPreferences.getString ("username","");
            date = sharedPreferences.getString ("date","");
        } catch (Exception e) {
            e.printStackTrace ();
        }

        DisplayName.setText (username);
        DisplayName.setVisibility (View.VISIBLE);
        Date.setText (date);
        Date.setVisibility (View.VISIBLE);
    }

    //internet check
    public static boolean HasActiveNetworkConnection(Context context) {

        final ConnectivityManager manager = (ConnectivityManager) context.getSystemService (Context.CONNECTIVITY_SERVICE);
        assert manager != null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            final Network network = manager.getActiveNetwork ();
            final NetworkCapabilities capabilities = manager.getNetworkCapabilities (network);

            return capabilities != null && capabilities.hasCapability (NetworkCapabilities
                    .NET_CAPABILITY_VALIDATED);
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult (requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            String title = data.getStringExtra (CreateTaskActivity.EXTRA_TITLE);
            String Des = data.getStringExtra (CreateTaskActivity.EXTRA_DESC);
            String Date = data.getStringExtra (CreateTaskActivity.EXTRA_TIME);

            MyTask myTask = new MyTask (title, Des, Date);
            taskViewModel.insert (myTask);
            //display ads
            if(mInterstitialAd.isLoaded ()){
                mInterstitialAd.show ();
            }

            StyleableToast.makeText (Objects.requireNonNull (getContext ()), "Task created", R.style.myToast).show ();
            EndOfPage.setVisibility (View.VISIBLE);

        } else if (requestCode == EDIT_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            int id = data.getIntExtra (CreateTaskActivity.EXTRA_ID, -1);

            if (id == -1) {
                StyleableToast.makeText (Objects.requireNonNull (getContext ()), "Task can't be updated", R.style.myToast1).show ();
                return;
            }
            String title = data.getStringExtra (CreateTaskActivity.EXTRA_TITLE);
            String Des = data.getStringExtra (CreateTaskActivity.EXTRA_DESC);
            String Date = data.getStringExtra (CreateTaskActivity.EXTRA_TIME);

            MyTask myTask = new MyTask (title, Des, Date);
            myTask.setId (id);
            taskViewModel.update (myTask);
            StyleableToast.makeText (Objects.requireNonNull (getContext ()), "Task updated", R.style.myToast).show ();

        }
    }


    @Override
    public void onResume() {
        super.onResume ();
        Objects.requireNonNull (((AppCompatActivity) Objects.requireNonNull (getActivity ())).getSupportActionBar ()).hide ();

    }

    @Override
    public void onStop() {
        super.onStop ();
        Objects.requireNonNull (((AppCompatActivity) Objects.requireNonNull (getActivity ())).getSupportActionBar ()).show ();
    }
}
