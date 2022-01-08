package com.vanchi.roomdbplayground

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {

    @Insert
    suspend fun insert(employee: EmployeeEntity)

    @Update
    suspend fun update(employee: EmployeeEntity)

    @Delete
    suspend fun  delete(employee: EmployeeEntity)

    @Query("SELECT * from `employee`")
    fun loadAllEmployees(): Flow<List<EmployeeEntity>>

    @Query("SELECT * from `employee` where id=:id")
    fun loadEmployeeById(id: Int): Flow<EmployeeEntity>
}