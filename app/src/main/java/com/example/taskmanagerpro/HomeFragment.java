package com.example.taskmanagerpro;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.Calendar;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class HomeFragment extends Fragment {
    private static final int ADD_NOTE_REQUEST = 1;
    private static final int EDIT_NOTE_REQUEST = 2;
    private TaskViewModel taskViewModel;
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private TextView DisplayName, DisplayQuote, time;
    private int TimeOfDay;
    private TextView endpage;
    private Calendar calendar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate (R.layout.home_activity, container, false);

        FloatingActionButton buttonAddTask = v.findViewById (R.id.button_add_task);
        SearchView searchView = v.findViewById (R.id.SearchView);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance ();
        DatabaseReference databaseReference = firebaseDatabase.getReference ("Users");
        DisplayName = v.findViewById (R.id.UserNAME);
        DisplayQuote = v.findViewById (R.id.quotepage);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance ();
        FirebaseUser user = firebaseAuth.getCurrentUser ();
        calendar = Calendar.getInstance ();
        TimeOfDay = calendar.get (Calendar.HOUR_OF_DAY);
        endpage = v.findViewById (R.id.endPage);
        time = v.findViewById (R.id.timeofday);


        //gets user's username from database and display in accordance with the current time
        Query query = null;
        if (user != null) {
            query = databaseReference.orderByChild ("email").equalTo (user.getEmail ());
        }
        if (query != null) {
            query.addValueEventListener (new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot d : dataSnapshot.getChildren ()) {
                        String username = "" + d.child ("username").getValue ();
                        if (TimeOfDay >= 0 && TimeOfDay < 12) {
                            time.setText (getString (R.string.goodmorning));
                            DisplayName.setText (username.toUpperCase ());
                            time.setVisibility (View.VISIBLE);
                            DisplayName.setVisibility (View.VISIBLE);
                            DisplayQuote.setVisibility (View.VISIBLE);

                        } else if (TimeOfDay >= 12 && TimeOfDay < 16) {
                            time.setText (getString (R.string.goodafternoon));
                            DisplayName.setText (username.toUpperCase ());
                            time.setVisibility (View.VISIBLE);
                            DisplayName.setVisibility (View.VISIBLE);
                            DisplayQuote.setVisibility (View.VISIBLE);

                        } else if (TimeOfDay >= 16 && TimeOfDay < 24) {
                            DisplayName.setText (username.toUpperCase ());
                            time.setText (getString (R.string.goodevening));
                            time.setVisibility (View.VISIBLE);
                            DisplayName.setVisibility (View.VISIBLE);
                            DisplayQuote.setVisibility (View.VISIBLE);
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
            //check if the list containing our model object is null
            //then display the "end of page" text if the list contains something
            if (myTasks.size () <= 0) {
                endpage.setVisibility (View.INVISIBLE);
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
                                endpage.setVisibility (View.VISIBLE);
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
                        .addBackgroundColor (ContextCompat.getColor (getActivity (), R.color.red))
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
            intent1.putExtra (CreateTaskActivity.EXTRA_DATE, task.getTaskTime ());

            HomeFragment.this.startActivityForResult (intent1, EDIT_NOTE_REQUEST);

        });
        return v;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult (requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            String title = data.getStringExtra (CreateTaskActivity.EXTRA_TITLE);
            String Des = data.getStringExtra (CreateTaskActivity.EXTRA_DESC);
            String Date = data.getStringExtra (CreateTaskActivity.EXTRA_DATE);

            MyTask myTask = new MyTask (title, Des, Date);
            taskViewModel.insert (myTask);

            StyleableToast.makeText (getContext (), "Task created", R.style.myToast).show ();
            endpage.setVisibility (View.VISIBLE);

        } else if (requestCode == EDIT_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            int id = data.getIntExtra (CreateTaskActivity.EXTRA_ID, -1);

            if (id == -1) {
                StyleableToast.makeText (getContext (), "Task can't be updated", R.style.myToast1).show ();
                return;
            }
            String title = data.getStringExtra (CreateTaskActivity.EXTRA_TITLE);
            String Des = data.getStringExtra (CreateTaskActivity.EXTRA_DESC);
            String Date = data.getStringExtra (CreateTaskActivity.EXTRA_DATE);

            MyTask myTask = new MyTask (title, Des, Date);
            myTask.setId (id);
            taskViewModel.update (myTask);
            StyleableToast.makeText (getContext (), "Task updated", R.style.myToast).show ();

        }
    }


    @Override
    public void onResume() {
        super.onResume ();
        ((AppCompatActivity) getActivity ()).getSupportActionBar ().hide ();
    }

    @Override
    public void onStop() {
        super.onStop ();
        ((AppCompatActivity) getActivity ()).getSupportActionBar ().show ();
    }
}
