package com.example.viacep.db;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public abstract class MainDao {

    @Insert(onConflict = REPLACE)
    public abstract void insert(MainData mainData);

    @Delete
    public abstract void delete(MainData mainData);

    @Delete
    abstract void reset(List<MainData> mainData);

    @Query("UPDATE table_name SET text = :sText WHERE ID = :sID")
    abstract void update(int sID, String sText);

    @Query("SELECT * FROM table_name")
    public abstract List<MainData> getAll();
}
