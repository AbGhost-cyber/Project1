package com.example.taskmanagerpro.data;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.text.DateFormat;
import java.util.Calendar;

@Database(entities = MyTask.class,version =6,exportSchema = false)
public abstract class TaskDatabase extends RoomDatabase {

    private static TaskDatabase instance;
    public  abstract TaskDao taskDao();
    public  static synchronized TaskDatabase getInstance(Context context)
    {
        if(instance==null)
        {
            instance= Room.databaseBuilder(context.getApplicationContext(),
                    TaskDatabase.class, "Task_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private  static Callback roomCallback=new Callback()
    {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static  class PopulateDbAsyncTask extends AsyncTask<Void,Void,Void>
    {
        private TaskDao taskDao;

        private  PopulateDbAsyncTask(TaskDatabase sb)
        {
            taskDao=sb.taskDao();
        }
        @Override
        protected Void doInBackground(Void... voids)
        {
            Calendar c = Calendar.getInstance ();
            String time= DateFormat.getTimeInstance (DateFormat.SHORT).format (c.getTime ());
            taskDao.insert(new MyTask("Doing Something",
                "Doing something i like",time));

            return null;
        }
    }
}
