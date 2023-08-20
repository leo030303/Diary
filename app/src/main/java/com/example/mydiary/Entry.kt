package com.example.mydiary

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Entry(
    @PrimaryKey(autoGenerate = true) val eid: Int,
    @ColumnInfo(name = "date_created") val dateCreated: Long,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "mood") val mood: Int,
)
