package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.components.ClaimSubmissionDialog
import com.example.ui.components.ContactActionsRow
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentAmberContainer
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.PrimaryIndigoContainer
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.StatusFoundContainer
import com.example.ui.theme.StatusFoundGreen
import com.example.ui.theme.StatusLostContainer
import com.example.ui.theme.StatusLostOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    item: Any,
    currentUser: UserEntity,
    onBack: () -> Unit,
    onUpdateLostStatus: (Long, String) -> Unit,
    onUpdateFoundStatus: (Long, String) -> Unit,
    onDeleteLostItem: (LostItemEntity) -> Unit,
    onDeleteFoundItem: (FoundItemEntity) -> Unit,
    onSubmitClaim: (FoundItemEntity, String, (Boolean, String) -> Unit) -> Unit
) {
    val context = LocalContext.current
    var showClaimDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showRecoveredConfirmDialog by remember { mutableStateOf(false) }

    val isLost = item is LostItemEntity
    val lostItem = item as? LostItemEntity
    val foundItem = item as? FoundItemEntity

    val itemName = lostItem?.itemName ?: foundItem?.itemName ?: ""
    val categoryName = lostItem?.categoryName ?: foundItem?.categoryName ?: ""
    val description = lostItem?.description ?: foundItem?.description ?: ""
    val location = lostItem?.lostLocation ?: foundItem?.foundLocation ?: ""
    val date = lostItem?.lostDate ?: foundItem?.foundDate ?: ""
    val contact = lostItem?.contact ?: foundItem?.contact ?: ""
    val reporterName = lostItem?.userName ?: foundItem?.userName ?: ""
    val reporterEmail = lostItem?.userEmail ?: foundItem?.userEmail ?: ""
    val status = lostItem?.status ?: foundItem?.status ?: ""
    val itemOwnerId = lostItem?.userId ?: foundItem?.userId ?: 0L

    val isOwner = currentUser.userId == itemOwnerId
    val isAdmin = currentUser.role == "admin"
    val canModerate = isOwner || isAdmin

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Item Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (canModerate) {
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.testTag("delete_item_btn")
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete Item",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
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
            // Hero Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = if (isLost) StatusLostContainer else StatusFoundContainer,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isLost) Icons.Default.Search else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (isLost) StatusLostOrange else StatusFoundGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isLost) "LOST REPORT" else "FOUND ITEM",
                                    color = if (isLost) StatusLostOrange else StatusFoundGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        StatusBadge(status = status)
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = itemName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Category: $categoryName",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (lostItem != null && lostItem.rewardNote.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = AccentAmberContainer,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "⭐ ", fontSize = 16.sp)
                                Text(
                                    text = "Reward / Note: ${lostItem.rewardNote}",
                                    color = AccentAmber,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    if (foundItem != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = PrimaryIndigoContainer,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Security,
                                        contentDescription = null,
                                        tint = PrimaryIndigo,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Current Handover / Deposit Location",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = PrimaryIndigo
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = foundItem.storageLocation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Location & Date Rows
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.outline)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Campus Location", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            }
                            Text(text = location, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.outline)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Date", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            }
                            Text(text = date, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // DEDICATED OPTION TO REMOVE FROM LOST LIST ONCE OBJECT IS RECEIVED
            if (isLost && canModerate) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (status != "Resolved") StatusFoundContainer else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("recovery_management_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (status != "Resolved") StatusFoundGreen else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (status != "Resolved") "Did you get your object back?" else "Object Recovered & Removed",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (status != "Resolved") StatusFoundGreen else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (status != "Resolved")
                                "Once you receive your lost $itemName back from campus custody or the finder, tap below to remove it from the active lost items list."
                            else
                                "This object has been marked as recovered and removed from the active campus lost listings.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        if (status != "Resolved") {
                            Button(
                                onClick = { showRecoveredConfirmDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = StatusFoundGreen),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_remove_from_lost_list")
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("I Got My Object Back (Remove from Lost List)", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    if (lostItem != null) {
                                        onUpdateLostStatus(lostItem.lostId, "Lost")
                                        Toast.makeText(context, "Item re-listed in active lost list", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().testTag("btn_reopen_lost_item")
                            ) {
                                Text("Re-open Lost Listing")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Reporter Info Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isLost) "Reported by" else "Found & Submitted by",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = reporterName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = reporterEmail,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contact Actions (Call, SMS, Email)
            ContactActionsRow(
                context = context,
                phone = contact,
                email = reporterEmail,
                itemName = itemName
            )

            // Claim Action (Only if Found Item, not owned by current user, and still Found)
            if (foundItem != null && !isOwner && foundItem.status == "Found") {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { showClaimDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("detail_claim_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                ) {
                    Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("I am the Owner (Claim Item)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Moderation Actions for owner / admin
            if (canModerate) {
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Item Management",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        if (status != "Resolved") {
                            FilledTonalButton(
                                onClick = {
                                    if (lostItem != null) {
                                        onUpdateLostStatus(lostItem.lostId, "Resolved")
                                    } else if (foundItem != null) {
                                        onUpdateFoundStatus(foundItem.foundId, "Resolved")
                                    }
                                    Toast.makeText(context, "Item marked as resolved / recovered!", Toast.LENGTH_SHORT).show()
                                    onBack()
                                },
                                modifier = Modifier.fillMaxWidth().testTag("mark_resolved_btn")
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Mark as Resolved / Returned")
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    if (lostItem != null) {
                                        onUpdateLostStatus(lostItem.lostId, "Lost")
                                    } else if (foundItem != null) {
                                        onUpdateFoundStatus(foundItem.foundId, "Found")
                                    }
                                    Toast.makeText(context, "Item re-opened!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Re-open Item Listing")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    if (showClaimDialog && foundItem != null) {
        ClaimSubmissionDialog(
            foundItem = foundItem,
            onDismiss = { showClaimDialog = false },
            onSubmitClaim = { proof ->
                showClaimDialog = false
                onSubmitClaim(foundItem, proof) { success, msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Listing?") },
            text = { Text("Are you sure you want to remove this item report from the college database?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        if (lostItem != null) onDeleteLostItem(lostItem)
                        if (foundItem != null) onDeleteFoundItem(foundItem)
                        Toast.makeText(context, "Item removed.", Toast.LENGTH_SHORT).show()
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showRecoveredConfirmDialog && lostItem != null) {
        AlertDialog(
            onDismissRequest = { showRecoveredConfirmDialog = false },
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
                    "Great news! Have you received your lost \"$itemName\" back?\n\nThis will mark the item as recovered and immediately remove it from the active campus lost devices catalog."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRecoveredConfirmDialog = false
                        onUpdateLostStatus(lostItem.lostId, "Resolved")
                        Toast.makeText(
                            context,
                            "🎉 Congratulations! \"$itemName\" marked as recovered and removed from lost list.",
                            Toast.LENGTH_LONG
                        ).show()
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusFoundGreen)
                ) {
                    Text("Yes, Remove from Lost List")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRecoveredConfirmDialog = false }) {
                    Text("Not Yet")
                }
            }
        )
    }
}
