package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity
import com.example.data.model.UserEntity
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.StatusFoundGreen
import com.example.ui.theme.StatusLostOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostItemScreen(
    initialIsLost: Boolean,
    currentUser: UserEntity,
    categories: List<CategoryEntity>,
    onBack: () -> Unit,
    onSubmitLost: (itemName: String, categoryId: Long, categoryName: String, location: String, date: String, contact: String, desc: String, reward: String) -> Unit,
    onSubmitFound: (itemName: String, categoryId: Long, categoryName: String, location: String, storage: String, date: String, contact: String, desc: String, question: String) -> Unit
) {
    val context = LocalContext.current
    val todayDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    var isLost by remember { mutableStateOf(initialIsLost) }
    var itemName by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableLongStateOf(categories.firstOrNull()?.categoryId ?: 1L) }
    var location by remember { mutableStateOf("") }
    var storageLocation by remember { mutableStateOf("Campus Security Desk Room 102") }
    var date by remember { mutableStateOf(todayDate) }
    var contact by remember { mutableStateOf(currentUser.phone) }
    var description by remember { mutableStateOf("") }
    var rewardNote by remember { mutableStateOf("") }
    var identifyingQuestion by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val campusSuggestions = listOf(
        "Library 2nd Floor",
        "Campus Cafeteria",
        "Main Auditorium",
        "Computer Lab 2",
        "Sports Ground",
        "Exam Hall",
        "Admin Block"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isLost) "Report Lost Item" else "Report Found Item",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Mode Selector: Lost vs Found
            TabRow(
                selectedTabIndex = if (isLost) 0 else 1,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = isLost,
                    onClick = { isLost = true },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = StatusLostOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("I Lost Something", fontWeight = FontWeight.Bold, color = if (isLost) StatusLostOrange else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    modifier = Modifier.testTag("tab_post_lost")
                )
                Tab(
                    selected = !isLost,
                    onClick = { isLost = false },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusFoundGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("I Found Something", fontWeight = FontWeight.Bold, color = if (!isLost) StatusFoundGreen else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    modifier = Modifier.testTag("tab_post_found")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // Item Name
            OutlinedTextField(
                value = itemName,
                onValueChange = {
                    itemName = it
                    if (errorMessage.isNotEmpty()) errorMessage = ""
                },
                label = { Text("Item Name *") },
                placeholder = { Text(if (isLost) "e.g., Titan Black Leather Wallet" else "e.g., Apple AirPods Pro White Case") },
                modifier = Modifier.fillMaxWidth().testTag("input_item_name"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Category Selection
            Text(
                text = "Select Category *",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategoryId == cat.categoryId,
                        onClick = { selectedCategoryId = cat.categoryId },
                        leadingIcon = {
                            Icon(
                                imageVector = getCategoryIcon(cat.iconType),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        label = { Text(cat.categoryName, fontSize = 12.sp) },
                        modifier = Modifier.testTag("select_cat_${cat.categoryId}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Location
            OutlinedTextField(
                value = location,
                onValueChange = {
                    location = it
                    if (errorMessage.isNotEmpty()) errorMessage = ""
                },
                label = { Text(if (isLost) "Where was it lost? *" else "Where was it found? *") },
                placeholder = { Text("e.g., Library 2nd Floor Reading Hall") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().testTag("input_location"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(campusSuggestions) { place ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable { location = place }
                    ) {
                        Text(
                            text = "+ $place",
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Found-only field: Storage / Handover location
            if (!isLost) {
                OutlinedTextField(
                    value = storageLocation,
                    onValueChange = { storageLocation = it },
                    label = { Text("Where is the item currently kept? *") },
                    placeholder = { Text("e.g., Handed over to Campus Security Desk") },
                    leadingIcon = { Icon(Icons.Default.Security, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("input_storage_location"),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Date & Contact
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (YYYY-MM-DD)") },
                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    modifier = Modifier.weight(1f).testTag("input_date"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text("Contact Phone") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    modifier = Modifier.weight(1f).testTag("input_contact"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                    if (errorMessage.isNotEmpty()) errorMessage = ""
                },
                label = { Text("Detailed Description *") },
                placeholder = { Text("Provide color, brand, distinct markings, cover stickers, contents...") },
                modifier = Modifier.fillMaxWidth().testTag("input_description"),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Specific Conditional Fields
            if (isLost) {
                OutlinedTextField(
                    value = rewardNote,
                    onValueChange = { rewardNote = it },
                    label = { Text("Reward / Urgency Note (Optional)") },
                    placeholder = { Text("e.g., Reward offered / Needed urgently for exams") },
                    modifier = Modifier.fillMaxWidth().testTag("input_reward")
                )
            } else {
                OutlinedTextField(
                    value = identifyingQuestion,
                    onValueChange = { identifyingQuestion = it },
                    label = { Text("Verification Question for Claimants (Recommended)") },
                    placeholder = { Text("e.g., What is written on the back sticker? / Describe lock screen") },
                    modifier = Modifier.fillMaxWidth().testTag("input_question"),
                    supportingText = {
                        Text("This question will be shown to users who try to claim this found item.")
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = {
                    if (itemName.isBlank() || location.isBlank() || description.isBlank()) {
                        errorMessage = "Please fill in all required fields (Item Name, Location, Description)."
                    } else {
                        val category = categories.find { it.categoryId == selectedCategoryId }
                        val categoryName = category?.categoryName ?: "Other Belongings"

                        if (isLost) {
                            onSubmitLost(
                                itemName,
                                selectedCategoryId,
                                categoryName,
                                location,
                                date,
                                contact,
                                description,
                                rewardNote
                            )
                            Toast.makeText(context, "Lost report submitted! Automatic matcher activated.", Toast.LENGTH_LONG).show()
                        } else {
                            onSubmitFound(
                                itemName,
                                selectedCategoryId,
                                categoryName,
                                location,
                                storageLocation,
                                date,
                                contact,
                                description,
                                identifyingQuestion
                            )
                            Toast.makeText(context, "Found item posted! Automatic matcher checked for matching lost items.", Toast.LENGTH_LONG).show()
                        }
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_item_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLost) StatusLostOrange else StatusFoundGreen
                )
            ) {
                Icon(
                    imageVector = if (isLost) Icons.Default.Search else Icons.Default.CheckCircle,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isLost) "Submit Lost Report" else "Submit Found Item",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
