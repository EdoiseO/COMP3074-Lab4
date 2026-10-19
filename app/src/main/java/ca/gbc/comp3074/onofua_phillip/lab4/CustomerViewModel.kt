package ca.gbc.comp3074.onofua_phillip.lab4

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ca.gbc.comp3074.onofua_phillip.lab4.data.Customer
import ca.gbc.comp3074.onofua_phillip.lab4.data.CustomerDao
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class CustomerViewModel(private val dao: CustomerDao) : ViewModel() {
    val customers = dao.getAllCustomers()
    var saving by mutableStateOf(false)
        private set
    var message by mutableStateOf("")
        private set

    fun addCustomer(name: String, ageText: String, isActive: Boolean, onSaved: () -> Unit) {
        if (saving) return
        val age = ageText.toIntOrNull()
        if (name.isBlank() || age == null || age < 0) {
            message = "Enter a name and a valid age."
            return
        }
        saving = true
        viewModelScope.launch {
            try {
                dao.insert(Customer(name = name.trim(), age = age, isActive = isActive))
                message = "Customer added."
                onSaved()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                message = "Could not save customer. Please try again."
            } finally {
                saving = false
            }
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            try {
                dao.delete(customer)
                message = "Customer deleted."
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                message = "Could not delete customer. Please try again."
            }
        }
    }
}

class CustomerViewModelFactory(private val dao: CustomerDao) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(CustomerViewModel::class.java))
        return CustomerViewModel(dao) as T
    }
}
