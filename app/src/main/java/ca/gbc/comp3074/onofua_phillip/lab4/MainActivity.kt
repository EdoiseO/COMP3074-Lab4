package ca.gbc.comp3074.onofua_phillip.lab4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.gbc.comp3074.onofua_phillip.lab4.data.AppDatabase
import ca.gbc.comp3074.onofua_phillip.lab4.data.Customer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel = ViewModelProvider(
            this, CustomerViewModelFactory(AppDatabase.getInstance(this).customerDao())
        )[CustomerViewModel::class.java]
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(
                primary = Color(0xFF333333), onPrimary = Color.White,
                secondary = Color(0xFF555555), background = Color.White,
                surface = Color.White, surfaceContainer = Color(0xFFF4F4F4),
                onSurface = Color(0xFF222222), outline = Color(0xFF777777)
            )) {
                Surface(Modifier.fillMaxSize()) { CustomerApp(viewModel) }
            }
        }
    }
}

@Composable
fun CustomerApp(viewModel: CustomerViewModel) {
    val customers by viewModel.customers.collectAsStateWithLifecycle(initialValue = emptyList())
    var name by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().safeDrawingPadding().imePadding().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Customers", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = name, onValueChange = { name = it }, label = { Text("Customer name") },
            modifier = Modifier.fillMaxWidth().testTag("name"), singleLine = true,
            enabled = !viewModel.saving, shape = RoundedCornerShape(0.dp)
        )
        OutlinedTextField(
            value = age, onValueChange = { age = it.filter(Char::isDigit) }, label = { Text("Age") },
            modifier = Modifier.fillMaxWidth().testTag("age"), singleLine = true,
            enabled = !viewModel.saving, shape = RoundedCornerShape(0.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Active customer", modifier = Modifier.padding(top = 12.dp))
            Switch(checked = active, onCheckedChange = { active = it },
                enabled = !viewModel.saving,
                modifier = Modifier.testTag("active").semantics { contentDescription = "Active customer" })
        }
        Button(
            onClick = {
                viewModel.addCustomer(name, age, active) { name = ""; age = ""; active = false }
            },
            modifier = Modifier.fillMaxWidth(), enabled = !viewModel.saving,
            shape = RoundedCornerShape(0.dp)
        ) { Text(if (viewModel.saving) "Saving…" else "Add Customer") }
        if (viewModel.message.isNotEmpty()) {
            Text(viewModel.message, style = MaterialTheme.typography.bodyMedium)
        }
        Text("Saved customers (${customers.size})", style = MaterialTheme.typography.titleMedium)
        if (customers.isEmpty()) {
            Text("No customers in the database.")
        }
        LazyColumn(
            modifier = Modifier.weight(1f).testTag("customers"),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(customers, key = { it.id }) { customer ->
                CustomerRow(customer, onDelete = { viewModel.deleteCustomer(customer) })
            }
        }
    }
}

@Composable
private fun CustomerRow(customer: Customer, onDelete: () -> Unit) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(Modifier.fillMaxWidth().padding(12.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(customer.name, style = MaterialTheme.typography.titleMedium)
                Text("ID: ${customer.id}  •  Age: ${customer.age}")
                Text(if (customer.isActive) "Active customer" else "Inactive customer")
            }
            TextButton(onClick = onDelete,
                modifier = Modifier.semantics { contentDescription = "Delete ${customer.name}" }) {
                Text("Delete")
            }
        }
    }
}
