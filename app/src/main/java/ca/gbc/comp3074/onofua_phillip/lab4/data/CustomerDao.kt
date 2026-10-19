package ca.gbc.comp3074.onofua_phillip.lab4.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Insert suspend fun insert(customer: Customer)
    @Delete suspend fun delete(customer: Customer)
    @Query("SELECT * FROM customers ORDER BY id")
    fun getAllCustomers(): Flow<List<Customer>>
}
