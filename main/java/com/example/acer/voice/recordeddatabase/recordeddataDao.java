package com.example.acer.voice.recordeddatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;
@Dao
public interface recordeddataDao {
    @Query("SELECT * FROM recordeddata")
    List<recordeddata> loadAlldata();

    @Insert
    void insertData(recordeddata RecordedData);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateData(recordeddata RecordedData);

    @Delete
    void deleteData(recordeddata RecordedData);
    @Query("SELECT * FROM recordeddata WHERE name=:s")
    recordeddata getbyname(String s);

}

