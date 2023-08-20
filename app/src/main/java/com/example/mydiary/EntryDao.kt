package com.example.mydiary

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Query("SELECT * FROM entry")
    fun getAll(): Flow<List<Entry>>

    @Query("SELECT * FROM entry WHERE eid = :id LIMIT 1")
    suspend fun getEntryByID(id: Int): Entry

    @Query("SELECT * FROM entry WHERE content LIKE :text")
    fun search(text: String): Flow<List<Entry>>

    @Query("SELECT * FROM entry WHERE date_created >= :testMonthMillis AND date_created < :nextMonthMillis ORDER BY date_created DESC")
    fun loadByMonthAndYear(testMonthMillis: Long, nextMonthMillis: Long): Flow<List<Entry>>

    @Insert
    suspend fun insert(vararg entry: Entry): List<Long>

    @Insert
    suspend fun insertAll(entries: List<Entry>): List<Long>

    @Update
    suspend fun update(vararg entry: Entry)

    @Delete
    suspend fun delete(entry: Entry)
}