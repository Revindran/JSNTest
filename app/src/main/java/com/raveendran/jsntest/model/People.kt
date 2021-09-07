package com.raveendran.jsntest.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "people"
)

data class People(
    val avatar: String,
    val email: String,
    val first_name: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val last_name: String,
    val deleted: Boolean
) : Serializable