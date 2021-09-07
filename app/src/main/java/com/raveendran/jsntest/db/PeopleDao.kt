package com.raveendran.jsntest.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.raveendran.jsntest.model.People

@Dao
interface PeopleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(people: People): Long

    @Query("SELECT * FROM people WHERE deleted==0")
    fun getAllPeoples(): LiveData<List<People>>

    @Delete
    suspend fun delete(people: People)

    @Update
    suspend fun update(people: People)

    @Query("SELECT * FROM people WHERE deleted==1")
    fun getAllDeletedPeoples(): LiveData<List<People>>

}