package com.example.introductiontoroom

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePerson(person: Person)

    @Update
    suspend fun updatePerson(person: Person)

    @Delete
    suspend fun deletePerson(person: Person)

    @Query("DELETE FROM person_table WHERE pId = :pId")
    suspend fun deletePersonById(pId : Int)

    @Query("SELECT * FROM person_table")
    fun getAllData() : Flow<List<Person>>

    @Query("SELECT * FROM person_table WHERE person_name LIKE :query || '%'")
    fun getSearchFromStartData(query : String) : Flow<List<Person>>

    @Query("SELECT * FROM person_table WHERE person_name LIKE '%' || :query")
    fun getSearchFromEndData(query : String) : Flow<List<Person>>
    @Query("SELECT * FROM person_table WHERE person_name LIKE '%' || :query || '%' or person_age LIKE '%' || :query || '%' or person_city LIKE '%' || :query || '%'")
    fun getSearchedData(query : String) : Flow<List<Person>>

    @Query("SELECT * FROM person_table WHERE person_name NOT LIKE '%' || :query || '%'")
    fun getSearchedExceptQueryData(query : String) : Flow<List<Person>>

}