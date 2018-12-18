package com.example.acer.voice.recordeddatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface recordedviedoDao {
    @Query("SELECT * FROM recordedvideo")
    List<recordedvideo> loadAlldata();

    @Insert
    void insertData(recordedvideo recordedvideo);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateData(recordedvideo recordedVideo);

    @Delete
    void deleteData(recordedvideo recordedVideo);

    @Query("SELECT * FROM recordedvideo WHERE name=:s")
    recordeddata getbyname(String s);
}
