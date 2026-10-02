package com.example.ui.screens

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClaimEntity
import com.example.data.model.FoundItemEntity
import com.example.data.model.LostItemEntity
import com.example.data.model.MatchEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.UserEntity
import com.example.ui.components.FoundItemCard
import com.example.ui.components.LostItemCard
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentAmberContainer
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.PrimaryIndigoContainer
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.SecondaryTealContainer
import com.example.ui.theme.StatusFoundGreen
import com.example.ui.theme.StatusLostOrange

@Composable
fun DashboardScreen(
    currentUser: UserEntity,
    lostItems: List<LostItemEntity>,
    foundItems: List<FoundItemEntity>,
    matches: List<MatchEntity>,
    claims: List<ClaimEntity>,
    notifications: List<NotificationEntity>,
    onNavigateToPost: (isLost: Boolean) -> Unit,
    onNavigateToFeed: (filter: String) -> Unit,
    onNavigateToMatches: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onSelectItem: (item: Any) -> Unit,
    onClaimFoundItem: (FoundItemEntity) -> Unit,
    onNavigateToWebPortal: () -> Unit = {}
) {
    val unreadNotifs = notifications.count { it.status == "Unread" }
    val activeLost = lostItems.count { it.status == "Lost" }
    val activeFound = foundItems.count { it.status == "Found" }
    val resolvedTotal = lostItems.count { it.status == "Resolved" } + foundItems.count { it.status == "Resolved" }
    val pendingMatches = matches.count { it.matchStatus == "Suggested" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Welcome Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Welcome, ${currentUser.name.split(" ").first()}! 👋",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${currentUser.role.replaceFirstChar { it.uppercase() }} • ${currentUser.department}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = onNavigateToNotifications,
                modifier = Modifier.testTag("notification_bell_btn")
            ) {
                BadgedBox(
                    badge = {
                        if (unreadNotifs > 0) {
                            Badge { Text("$unreadNotifs") }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // High priority smart match banner if there are suggestions
        if (pendingMatches > 0) {
            Card(
                colors = CardDefaults.cardColors(containerColor = PrimaryIndigoContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToMatches() }
                    .testTag("match_banner")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(PrimaryIndigo),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "$pendingMatches Potential Smart Match${if (pendingMatches > 1) "es" else ""}!",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryIndigo
                        )
                        Text(
                            text = "Automatic matching found possible item owners. Tap to review.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Quick KPI Stats Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KpiStatCard(
                title = "Lost Items",
                value = "$activeLost",
                color = StatusLostOrange,
                icon = Icons.Default.Search,
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToFeed("LOST") }
            )
            KpiStatCard(
                title = "Found Items",
                value = "$activeFound",
                color = StatusFoundGreen,
                icon = Icons.Default.CheckCircle,
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToFeed("FOUND") }
            )
            KpiStatCard(
                title = "Recovered",
                value = "$resolvedTotal",
                color = SecondaryTeal,
                icon = Icons.Default.Assignment,
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToFeed("ALL") }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Action Cards
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionTile(
                title = "Report Lost",
                subtitle = "Post missing item",
                icon = Icons.Default.AddAlert,
                color = StatusLostOrange,
                modifier = Modifier
                    .weight(1f)
                    .testTag("action_report_lost"),
                onClick = { onNavigateToPost(true) }
            )
            ActionTile(
                title = "Report Found",
                subtitle = "Handed in an item",
                icon = Icons.Default.PostAdd,
                color = StatusFoundGreen,
                modifier = Modifier
                    .weight(1f)
                    .testTag("action_report_found"),
                onClick = { onNavigateToPost(false) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionTile(
                title = "Browse All",
                subtitle = "Search & filters",
                icon = Icons.Default.Search,
                color = PrimaryIndigo,
                modifier = Modifier
                    .weight(1f)
                    .testTag("action_browse"),
                onClick = { onNavigateToFeed("ALL") }
            )
            ActionTile(
                title = "Matches & Claims",
                subtitle = "${matches.size} matches • ${claims.size} claims",
                icon = Icons.Default.AutoAwesome,
                color = AccentAmber,
                modifier = Modifier
                    .weight(1f)
                    .testTag("action_matches"),
                onClick = onNavigateToMatches
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Full Web & Laptop Edition Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = PrimaryIndigoContainer),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToWebPortal() }
                .testTag("action_web_portal")
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryIndigo),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Web Portal",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🌐 Laptop Web Portal Edition",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryIndigo
                    )
                    Text(
                        text = "Experience the full desktop web browser interface directly inside this app or on any laptop browser.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Open \u2192",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryIndigo
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Urgent Lost Items Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recently Lost Belongings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = { onNavigateToFeed("LOST") }) {
                Text("See All")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        val recentLost = lostItems.filter { it.status == "Lost" }.take(3)
        if (recentLost.isEmpty()) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No active lost reports. All caught up!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                recentLost.forEach { lost ->
                    LostItemCard(
                        item = lost,
                        onClick = { onSelectItem(lost) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recent Found Items Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recently Found & Deposited",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = { onNavigateToFeed("FOUND") }) {
                Text("See All")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        val recentFound = foundItems.filter { it.status == "Found" }.take(3)
        if (recentFound.isEmpty()) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No newly deposited found items right now.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                recentFound.forEach { found ->
                    FoundItemCard(
                        item = found,
                        onClick = { onSelectItem(found) },
                        onClaimClick = { onClaimFoundItem(found) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Campus Security Notice
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Security",
                    tint = SecondaryTeal,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Campus Security Protocol",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Valuable items like mobile phones and wallets should be deposited at Security Office (Room 102) for safe keeping and verified handover.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun KpiStatCard(
    title: String,
    value: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
