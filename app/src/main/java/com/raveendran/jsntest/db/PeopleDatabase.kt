package com.raveendran.jsntest.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.raveendran.jsntest.model.People

@Database(
    entities = [People::class],
    version = 1
)

abstract class PeopleDatabase : RoomDatabase() {
    abstract fun peopleDao(): PeopleDao
}