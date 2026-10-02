package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoundItemEntity
import com.example.data.model.LostItemEntity
import com.example.data.model.UserEntity
import com.example.ui.components.FoundItemCard
import com.example.ui.components.LostItemCard
import com.example.ui.theme.StatusFoundContainer
import com.example.ui.theme.StatusFoundGreen
import com.example.ui.theme.StatusLostOrange

@Composable
fun MyItemsScreen(
    currentUser: UserEntity,
    allLostItems: List<LostItemEntity>,
    allFoundItems: List<FoundItemEntity>,
    onSelectItem: (Any) -> Unit,
    onNavigateToPost: (isLost: Boolean) -> Unit,
    onUpdateLostStatus: (Long, String) -> Unit = { _, _ -> },
    onDeleteLostItem: (LostItemEntity) -> Unit = {}
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Lost, 1: Found
    var itemToRecover by remember { mutableStateOf<LostItemEntity?>(null) }
    var itemToDelete by remember { mutableStateOf<LostItemEntity?>(null) }

    val myLostItems = allLostItems.filter { it.userId == currentUser.userId }
    val myFoundItems = allFoundItems.filter { it.userId == currentUser.userId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "My Reported Items",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Manage your reported lost belongings and deposited found items",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Search, contentDescription = null, tint = StatusLostOrange)
                                Spacer(modifier = Modifier.padding(2.dp))
                                Text("My Lost Items (${myLostItems.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        },
                        modifier = Modifier.testTag("tab_my_lost")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusFoundGreen)
                                Spacer(modifier = Modifier.padding(2.dp))
                                Text("My Found Deposits (${myFoundItems.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        },
                        modifier = Modifier.testTag("tab_my_found")
                    )
                }
            }
        }

        if (selectedTab == 0) {
            if (myLostItems.isEmpty()) {
                EmptySection(
                    icon = Icons.Default.Search,
                    title = "No Lost Items Reported",
                    subtitle = "If you misplace an item on campus, report it here so the college community can help locate it."
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(myLostItems, key = { it.lostId }) { item ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                LostItemCard(
                                    item = item,
                                    onClick = { onSelectItem(item) }
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // DEDICATED OPTION TO REMOVE FROM LOST LIST
                                if (item.status == "Lost") {
                                    Button(
                                        onClick = { itemToRecover = item },
                                        colors = ButtonDefaults.buttonColors(containerColor = StatusFoundGreen),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("my_items_remove_lost_${item.lostId}")
                                    ) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "🎉 I Got My Object Back (Remove from List)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                } else {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(StatusFoundContainer, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = StatusFoundGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                "Object Recovered & Removed from Active Lost List",
                                                color = StatusFoundGreen,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 11.sp
                                            )
                                        }
                                        TextButton(onClick = { itemToDelete = item }) {
                                            Text("Delete", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (myFoundItems.isEmpty()) {
                EmptySection(
                    icon = Icons.Default.CheckCircle,
                    title = "No Found Items Deposited",
                    subtitle = "Found something on campus? Report it to help it find its way back to its owner."
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(myFoundItems, key = { it.foundId }) { item ->
                        FoundItemCard(
                            item = item,
                            onClick = { onSelectItem(item) }
                        )
                    }
                }
            }
        }
    }

    // Confirmation dialog for "I Got My Object Back"
    if (itemToRecover != null) {
        val target = itemToRecover!!
        AlertDialog(
            onDismissRequest = { itemToRecover = null },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = StatusFoundGreen,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text("Confirm Object Received", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Great news! Have you received your \"${target.itemName}\" back?\n\nThis will mark the item as recovered and immediately remove it from the active campus lost listings."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        itemToRecover = null
                        onUpdateLostStatus(target.lostId, "Resolved")
                        Toast.makeText(
                            context,
                            "🎉 Congratulations! \"${target.itemName}\" removed from active lost list.",
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusFoundGreen)
                ) {
                    Text("Yes, Remove from Lost List")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToRecover = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (itemToDelete != null) {
        val target = itemToDelete!!
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete Report?") },
            text = { Text("Remove \"${target.itemName}\" permanently from your account history?") },
            confirmButton = {
                Button(
                    onClick = {
                        itemToDelete = null
                        onDeleteLostItem(target)
                        Toast.makeText(context, "Report removed.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
