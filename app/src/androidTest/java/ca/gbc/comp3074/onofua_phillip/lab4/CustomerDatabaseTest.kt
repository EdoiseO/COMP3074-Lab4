package ca.gbc.comp3074.onofua_phillip.lab4

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ca.gbc.comp3074.onofua_phillip.lab4.data.AppDatabase
import ca.gbc.comp3074.onofua_phillip.lab4.data.Customer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CustomerDatabaseTest {
    @Test fun insertReopenAndDeleteCustomers() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val filename = "customer-persistence-test"
        context.deleteDatabase(filename)
        var database = Room.databaseBuilder(context, AppDatabase::class.java, filename).build()
        try {
            val dao = database.customerDao()
            assertTrue(dao.getAllCustomers().first().isEmpty())
            dao.insert(Customer(name = "Alice", age = 21, isActive = true))
            dao.insert(Customer(name = "Bob", age = 34, isActive = false))
            val inserted = dao.getAllCustomers().first()
            assertEquals(2, inserted.size)
            assertTrue(inserted[0].id > 0)
            assertTrue(inserted[1].id > inserted[0].id)
            assertTrue(inserted[0].isActive)
            assertFalse(inserted[1].isActive)
            database.close()
            database = Room.databaseBuilder(context, AppDatabase::class.java, filename).build()
            assertEquals(inserted, database.customerDao().getAllCustomers().first())
            database.customerDao().delete(inserted.first())
            assertEquals(listOf(inserted.last()), database.customerDao().getAllCustomers().first())
        } finally {
            database.close()
            context.deleteDatabase(filename)
        }
    }
}
