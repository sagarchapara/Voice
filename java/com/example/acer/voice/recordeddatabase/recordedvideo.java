package com.example.acer.voice.recordeddatabase;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;
@Entity(tableName = "RecordedVideo")
public class recordedvideo {
private Date date;
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
//    final String a ="Video ";

    public recordedvideo(Date date,String name) {
        this.date = date;
        this.name = name;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {

        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
