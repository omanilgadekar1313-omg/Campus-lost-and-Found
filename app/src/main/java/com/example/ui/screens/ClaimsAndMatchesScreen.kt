package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClaimEntity
import com.example.data.model.FoundItemEntity
import com.example.data.model.MatchEntity
import com.example.data.model.UserEntity
import com.example.ui.components.ContactActionsRow
import com.example.ui.components.MatchCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.PrimaryIndigoContainer
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.StatusFoundGreen
import com.example.ui.theme.StatusLostOrange

@Composable
fun ClaimsAndMatchesScreen(
    currentUser: UserEntity,
    matches: List<MatchEntity>,
    claims: List<ClaimEntity>,
    foundItems: List<FoundItemEntity>,
    onUpdateMatchStatus: (Long, String) -> Unit,
    onResolveClaim: (ClaimEntity, Boolean, String) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Smart Matches, 1: Incoming Claims, 2: My Claims

    // Relevant claims
    val isAdmin = currentUser.role == "admin"
    val myFoundItemIds = foundItems.filter { it.userId == currentUser.userId }.map { it.foundId }.toSet()

    val incomingClaims = claims.filter {
        isAdmin || myFoundItemIds.contains(it.foundId)
    }

    val myFiledClaims = claims.filter {
        it.userId == currentUser.userId
    }

    // Claim decision dialog
    var claimToReview by remember { mutableStateOf<ClaimEntity?>(null) }
    var reviewDecisionIsApprove by remember { mutableStateOf(true) }
    var decisionNote by remember { mutableStateOf("") }

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
                    text = "Matches & Item Claims",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Automated matching engine and ownership verification workflow",
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
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Matches (${matches.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_matches")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Incoming (${incomingClaims.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_incoming_claims")
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text("My Claims (${myFiledClaims.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        },
                        modifier = Modifier.testTag("tab_my_claims")
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> {
                // SMART MATCHES
                if (matches.isEmpty()) {
                    EmptySection(
                        icon = Icons.Default.AutoAwesome,
                        title = "No Matches Found Yet",
                        subtitle = "When a reported lost item aligns with a newly found item in category, name, or location, smart pairings will appear here automatically."
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(matches, key = { it.matchId }) { match ->
                            MatchCard(
                                match = match,
                                onAccept = {
                                    onUpdateMatchStatus(match.matchId, "Accepted")
                                    Toast.makeText(context, "Match confirmed!", Toast.LENGTH_SHORT).show()
                                },
                                onDismiss = {
                                    onUpdateMatchStatus(match.matchId, "Dismissed")
                                    Toast.makeText(context, "Match dismissed.", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
            1 -> {
                // INCOMING CLAIMS (Verification workflow)
                if (incomingClaims.isEmpty()) {
                    EmptySection(
                        icon = Icons.Default.AssignmentTurnedIn,
                        title = "No Incoming Claims",
                        subtitle = "No students have submitted claims for items you deposited or that require verification."
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(incomingClaims, key = { it.claimId }) { claim ->
                            ClaimReviewCard(
                                claim = claim,
                                onApprove = {
                                    claimToReview = claim
                                    reviewDecisionIsApprove = true
                                    decisionNote = "Proof verified successfully. Ready for collection."
                                },
                                onReject = {
                                    claimToReview = claim
                                    reviewDecisionIsApprove = false
                                    decisionNote = "Proof provided does not match item attributes."
                                }
                            )
                        }
                    }
                }
            }
            2 -> {
                // MY FILED CLAIMS
                if (myFiledClaims.isEmpty()) {
                    EmptySection(
                        icon = Icons.Default.Person,
                        title = "You Haven't Claimed Any Items",
                        subtitle = "If you spot your missing belonging in the Found Items catalog, open it and tap 'Claim This Item'."
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(myFiledClaims, key = { it.claimId }) { claim ->
                            MyClaimCard(claim = claim)
                        }
                    }
                }
            }
        }
    }

    if (claimToReview != null) {
        val claim = claimToReview!!
        AlertDialog(
            onDismissRequest = { claimToReview = null },
            title = {
                Text(if (reviewDecisionIsApprove) "Approve Claim?" else "Reject Claim?")
            },
            text = {
                Column {
                    Text(
                        text = if (reviewDecisionIsApprove)
                            "Approving will update '${claim.itemName}' to Claimed status and notify ${claim.claimantName} with handover instructions."
                        else
                            "Rejecting will inform ${claim.claimantName} that verification was unsuccessful."
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = decisionNote,
                        onValueChange = { decisionNote = it },
                        label = { Text("Note to Claimant") },
                        modifier = Modifier.fillMaxWidth().testTag("claim_decision_note"),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResolveClaim(claim, reviewDecisionIsApprove, decisionNote)
                        claimToReview = null
                        Toast.makeText(
                            context,
                            if (reviewDecisionIsApprove) "Claim approved!" else "Claim rejected.",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (reviewDecisionIsApprove) StatusFoundGreen else MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.testTag("confirm_claim_decision_btn")
                ) {
                    Text(if (reviewDecisionIsApprove) "Confirm Approval" else "Confirm Rejection")
                }
            },
            dismissButton = {
                TextButton(onClick = { claimToReview = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ClaimReviewCard(
    claim: ClaimEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("claim_card_${claim.claimId}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Claim for: ${claim.itemName}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Submitted on ${claim.claimDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusBadge(status = claim.claimStatus)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Claimant's Proof of Ownership:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryIndigo
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"${claim.proofDescription}\"",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Claimant: ${claim.claimantName} (${claim.claimantPhone} • ${claim.claimantEmail})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (claim.responseNote.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Decision Note: ${claim.responseNote}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            if (claim.claimStatus == "Pending") {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f).testTag("reject_claim_btn_${claim.claimId}")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reject", color = MaterialTheme.colorScheme.error)
                    }
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = StatusFoundGreen),
                        modifier = Modifier.weight(1f).testTag("approve_claim_btn_${claim.claimId}")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Approve")
                    }
                }
            }
        }
    }
}

@Composable
fun MyClaimCard(claim: ClaimEntity) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = claim.itemName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                StatusBadge(status = claim.claimStatus)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Submitted Proof: ${claim.proofDescription}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Claim Date: ${claim.claimDate}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )

            if (claim.responseNote.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = if (claim.claimStatus == "Approved") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Feedback from Finder: ${claim.responseNote}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptySection(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(PrimaryIndigoContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryIndigo,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
