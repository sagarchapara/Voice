package com.example.acer.voice.recordeddatabase;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;
@Database(entities = {recordeddata.class,recordedvideo.class},version = 1,exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class recordedDatadatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "recordedlist";
    private static recordedDatadatabase sInstance;

    public static recordedDatadatabase getsInstance(Context context){
        if(sInstance==null){
            synchronized (LOCK){
                sInstance = Room.databaseBuilder(context.getApplicationContext(),recordedDatadatabase.class
                            ,DATABASE_NAME)
                            .build();
            }
        }
        return sInstance;
    }
    public abstract recordeddataDao recordeddatadao();
    public abstract recordedviedoDao recordedvideodao();
}
