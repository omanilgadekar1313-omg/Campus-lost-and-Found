package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoundItemEntity
import com.example.data.model.LostItemEntity
import com.example.ui.components.ClaimSubmissionDialog
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ClaimsAndMatchesScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.ItemDetailScreen
import com.example.ui.screens.MyItemsScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.PostItemScreen
import com.example.ui.screens.WebPortalScreen
import androidx.compose.material.icons.filled.Language
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.PrimaryIndigoContainer
import com.example.ui.viewmodel.LostAndFoundViewModel

enum class Screen {
    DASHBOARD,
    FEED,
    MATCHES_CLAIMS,
    MY_ITEMS,
    ADMIN,
    POST_ITEM,
    ITEM_DETAIL,
    NOTIFICATIONS,
    WEB_PORTAL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: LostAndFoundViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val lostItems by viewModel.allLostItems.collectAsState()
    val foundItems by viewModel.allFoundItems.collectAsState()
    val categories by viewModel.allCategories.collectAsState()
    val claims by viewModel.allClaims.collectAsState()
    val matches by viewModel.allMatches.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val users by viewModel.allUsers.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategoryId.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val selectedType by viewModel.selectedTypeFilter.collectAsState()

    var currentScreen by remember { mutableStateOf(Screen.DASHBOARD) }
    var selectedItemForDetail by remember { mutableStateOf<Any?>(null) }
    var initialPostIsLost by remember { mutableStateOf(true) }
    var claimTargetFoundItem by remember { mutableStateOf<FoundItemEntity?>(null) }

    // If user is not logged in, show AuthScreen with adaptive width
    if (currentUser == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.widthIn(max = 520.dp)) {
                AuthScreen(
                    onLogin = { email, pass, onResult -> viewModel.login(email, pass, onResult) },
                    onRegister = { name, email, pass, phone, role, dept, onResult ->
                        viewModel.register(name, email, pass, phone, role, dept, onResult)
                    },
                    onQuickLogin = { role -> viewModel.quickLogin(role) }
                )
            }
        }
        return
    }

    val user = currentUser!!
    val unreadNotifs = notifications.count { it.status == "Unread" }
    val pendingMatches = matches.count { it.matchStatus == "Suggested" }

    // Navigation back handlers
    if (currentScreen != Screen.DASHBOARD) {
        BackHandler {
            currentScreen = Screen.DASHBOARD
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 760.dp

        if (isWideScreen) {
            // DESKTOP & WEB WIDE LAYOUT (NavigationRail + Content Pane / Dual-Pane)
            Row(modifier = Modifier.fillMaxSize()) {
                // Desktop Navigation Rail
                NavigationRail(
                    modifier = Modifier.fillMaxHeight(),
                    containerColor = MaterialTheme.colorScheme.surface,
                    header = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryIndigo),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Logo",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Campus\nPortal",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 13.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            FloatingActionButton(
                                onClick = {
                                    initialPostIsLost = true
                                    currentScreen = Screen.POST_ITEM
                                },
                                containerColor = PrimaryIndigo,
                                contentColor = Color.White,
                                modifier = Modifier.size(48.dp).testTag("desktop_fab_post")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Post Item")
                            }
                        }
                    }
                ) {
                    NavigationRailItem(
                        selected = currentScreen == Screen.DASHBOARD,
                        onClick = { currentScreen = Screen.DASHBOARD },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 11.sp) },
                        modifier = Modifier.testTag("rail_home")
                    )
                    NavigationRailItem(
                        selected = currentScreen == Screen.FEED,
                        onClick = { currentScreen = Screen.FEED },
                        icon = { Icon(Icons.Default.Search, contentDescription = "Browse") },
                        label = { Text("Browse", fontSize = 11.sp) },
                        modifier = Modifier.testTag("rail_browse")
                    )
                    NavigationRailItem(
                        selected = currentScreen == Screen.MATCHES_CLAIMS,
                        onClick = { currentScreen = Screen.MATCHES_CLAIMS },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (pendingMatches > 0) {
                                        Badge { Text("$pendingMatches") }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = "Matches")
                            }
                        },
                        label = { Text("Matches", fontSize = 11.sp) },
                        modifier = Modifier.testTag("rail_matches")
                    )
                    NavigationRailItem(
                        selected = currentScreen == Screen.MY_ITEMS,
                        onClick = { currentScreen = Screen.MY_ITEMS },
                        icon = { Icon(Icons.Default.Folder, contentDescription = "My Items") },
                        label = { Text("My Items", fontSize = 11.sp) },
                        modifier = Modifier.testTag("rail_my_items")
                    )
                    if (user.role == "admin") {
                        NavigationRailItem(
                            selected = currentScreen == Screen.ADMIN,
                            onClick = { currentScreen = Screen.ADMIN },
                            icon = { Icon(Icons.Default.Security, contentDescription = "Admin") },
                            label = { Text("Admin", fontSize = 11.sp) },
                            modifier = Modifier.testTag("rail_admin")
                        )
                    }

                    NavigationRailItem(
                        selected = currentScreen == Screen.WEB_PORTAL,
                        onClick = { currentScreen = Screen.WEB_PORTAL },
                        icon = { Icon(Icons.Default.Language, contentDescription = "Web Portal") },
                        label = { Text("Web View", fontSize = 11.sp) },
                        modifier = Modifier.testTag("rail_web_portal")
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Notifications icon in rail
                    NavigationRailItem(
                        selected = currentScreen == Screen.NOTIFICATIONS,
                        onClick = { currentScreen = Screen.NOTIFICATIONS },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (unreadNotifs > 0) {
                                        Badge { Text("$unreadNotifs") }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                            }
                        },
                        label = { Text("Alerts", fontSize = 11.sp) },
                        modifier = Modifier.testTag("rail_notifs")
                    )

                    // Logout in rail
                    NavigationRailItem(
                        selected = false,
                        onClick = { viewModel.logout() },
                        icon = { Icon(Icons.Default.Logout, contentDescription = "Logout") },
                        label = { Text("Logout", fontSize = 11.sp) },
                        modifier = Modifier.testTag("rail_logout")
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Desktop Main Content Area
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    when (currentScreen) {
                        Screen.DASHBOARD -> DashboardScreen(
                            currentUser = user,
                            lostItems = lostItems,
                            foundItems = foundItems,
                            matches = matches,
                            claims = claims,
                            notifications = notifications,
                            onNavigateToPost = { isLost ->
                                initialPostIsLost = isLost
                                currentScreen = Screen.POST_ITEM
                            },
                            onNavigateToFeed = { filter ->
                                viewModel.setTypeFilter(filter)
                                currentScreen = Screen.FEED
                            },
                            onNavigateToMatches = { currentScreen = Screen.MATCHES_CLAIMS },
                            onNavigateToNotifications = { currentScreen = Screen.NOTIFICATIONS },
                            onSelectItem = { item ->
                                selectedItemForDetail = item
                                currentScreen = Screen.ITEM_DETAIL
                            },
                            onClaimFoundItem = { found -> claimTargetFoundItem = found },
                            onNavigateToWebPortal = { currentScreen = Screen.WEB_PORTAL }
                        )

                        Screen.FEED -> {
                            // CANONICAL DUAL-PANE LIST-DETAIL LAYOUT FOR DESKTOP & WEB!
                            Row(modifier = Modifier.fillMaxSize()) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                ) {
                                    FeedScreen(
                                        lostItems = lostItems,
                                        foundItems = foundItems,
                                        categories = categories,
                                        searchQuery = searchQuery,
                                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                        selectedCategory = selectedCategory,
                                        onCategorySelect = { viewModel.setCategoryFilter(it) },
                                        selectedLocation = selectedLocation,
                                        onLocationSelect = { viewModel.setLocationFilter(it) },
                                        selectedType = selectedType,
                                        onTypeSelect = { viewModel.setTypeFilter(it) },
                                        onSelectItem = { item -> selectedItemForDetail = item },
                                        onClaimFoundItem = { found -> claimTargetFoundItem = found },
                                        onNavigateToPost = { isLost ->
                                            initialPostIsLost = isLost
                                            currentScreen = Screen.POST_ITEM
                                        }
                                    )
                                }

                                // Right Detail Pane
                                Box(
                                    modifier = Modifier
                                        .weight(1.1f)
                                        .fillMaxHeight()
                                        .background(MaterialTheme.colorScheme.surface)
                                ) {
                                    val itemToShow = selectedItemForDetail
                                        ?: (foundItems.firstOrNull() ?: lostItems.firstOrNull())

                                    if (itemToShow != null) {
                                        ItemDetailScreen(
                                            item = itemToShow,
                                            currentUser = user,
                                            onBack = { selectedItemForDetail = null },
                                            onUpdateLostStatus = { id, status -> viewModel.updateLostStatus(id, status) },
                                            onUpdateFoundStatus = { id, status -> viewModel.updateFoundStatus(id, status) },
                                            onDeleteLostItem = {
                                                viewModel.deleteLostItem(it)
                                                selectedItemForDetail = null
                                            },
                                            onDeleteFoundItem = {
                                                viewModel.deleteFoundItem(it)
                                                selectedItemForDetail = null
                                            },
                                            onSubmitClaim = { found, proof, callback ->
                                                viewModel.submitClaim(found, proof, callback)
                                            }
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier.fillMaxSize().padding(32.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Select an item to view details",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Screen.POST_ITEM -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Box(modifier = Modifier.widthIn(max = 680.dp)) {
                                    PostItemScreen(
                                        initialIsLost = initialPostIsLost,
                                        currentUser = user,
                                        categories = categories,
                                        onBack = { currentScreen = Screen.DASHBOARD },
                                        onSubmitLost = { name, catId, catName, loc, date, contact, desc, reward ->
                                            viewModel.postLostItem(name, catId, catName, loc, date, contact, desc, reward) {
                                                currentScreen = Screen.FEED
                                            }
                                        },
                                        onSubmitFound = { name, catId, catName, loc, storage, date, contact, desc, question ->
                                            viewModel.postFoundItem(name, catId, catName, loc, storage, date, contact, desc, question) {
                                                currentScreen = Screen.FEED
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        Screen.ITEM_DETAIL -> {
                            val target = selectedItemForDetail
                            if (target != null) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    Box(modifier = Modifier.widthIn(max = 760.dp)) {
                                        ItemDetailScreen(
                                            item = target,
                                            currentUser = user,
                                            onBack = { currentScreen = Screen.FEED },
                                            onUpdateLostStatus = { id, status -> viewModel.updateLostStatus(id, status) },
                                            onUpdateFoundStatus = { id, status -> viewModel.updateFoundStatus(id, status) },
                                            onDeleteLostItem = {
                                                viewModel.deleteLostItem(it)
                                                currentScreen = Screen.FEED
                                            },
                                            onDeleteFoundItem = {
                                                viewModel.deleteFoundItem(it)
                                                currentScreen = Screen.FEED
                                            },
                                            onSubmitClaim = { found, proof, callback ->
                                                viewModel.submitClaim(found, proof, callback)
                                            }
                                        )
                                    }
                                }
                            } else {
                                currentScreen = Screen.FEED
                            }
                        }

                        Screen.MATCHES_CLAIMS -> ClaimsAndMatchesScreen(
                            currentUser = user,
                            matches = matches,
                            claims = claims,
                            foundItems = foundItems,
                            onUpdateMatchStatus = { id, status -> viewModel.updateMatchStatus(id, status) },
                            onResolveClaim = { claim, approved, note ->
                                viewModel.resolveClaim(claim, approved, note)
                            }
                        )

                        Screen.MY_ITEMS -> MyItemsScreen(
                            currentUser = user,
                            allLostItems = lostItems,
                            allFoundItems = foundItems,
                            onSelectItem = { item ->
                                selectedItemForDetail = item
                                currentScreen = Screen.ITEM_DETAIL
                            },
                            onNavigateToPost = { isLost ->
                                initialPostIsLost = isLost
                                currentScreen = Screen.POST_ITEM
                            },
                            onUpdateLostStatus = { id, status -> viewModel.updateLostStatus(id, status) },
                            onDeleteLostItem = { viewModel.deleteLostItem(it) }
                        )

                        Screen.ADMIN -> AdminScreen(
                            users = users,
                            lostItems = lostItems,
                            foundItems = foundItems,
                            categories = categories,
                            claims = claims,
                            onAddCategory = { name, icon -> viewModel.addCategory(name, icon) }
                        )

                        Screen.NOTIFICATIONS -> NotificationsScreen(
                            notifications = notifications,
                            onBack = { currentScreen = Screen.DASHBOARD },
                            onMarkRead = { viewModel.markNotificationRead(it) },
                            onMarkAllRead = { viewModel.markAllNotificationsRead() }
                        )

                        Screen.WEB_PORTAL -> WebPortalScreen(
                            onBack = { currentScreen = Screen.DASHBOARD }
                        )
                    }
                }
            }
        } else {
            // MOBILE COMPACT LAYOUT (Standard Bottom NavigationBar + Floating Action Button)
            Scaffold(
                topBar = {
                    if (currentScreen != Screen.POST_ITEM &&
                        currentScreen != Screen.ITEM_DETAIL &&
                        currentScreen != Screen.NOTIFICATIONS &&
                        currentScreen != Screen.WEB_PORTAL
                    ) {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(PrimaryIndigo),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Campus Lost & Found",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp
                                        )
                                        Text(
                                            text = "${user.name} (${user.role})",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            },
                            actions = {
                                IconButton(
                                    onClick = { currentScreen = Screen.WEB_PORTAL },
                                    modifier = Modifier.testTag("top_web_portal_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = "Web Portal",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(
                                    onClick = { currentScreen = Screen.NOTIFICATIONS },
                                    modifier = Modifier.testTag("top_notif_btn")
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
                                            contentDescription = "Notifications"
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { viewModel.logout() },
                                    modifier = Modifier.testTag("logout_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Logout,
                                        contentDescription = "Log out",
                                        tint = MaterialTheme.colorScheme.outline
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                        )
                    }
                },
                bottomBar = {
                    if (currentScreen != Screen.POST_ITEM &&
                        currentScreen != Screen.ITEM_DETAIL &&
                        currentScreen != Screen.NOTIFICATIONS &&
                        currentScreen != Screen.WEB_PORTAL
                    ) {
                        NavigationBar(
                            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp
                        ) {
                            NavigationBarItem(
                                selected = currentScreen == Screen.DASHBOARD,
                                onClick = { currentScreen = Screen.DASHBOARD },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Home", fontSize = 11.sp) },
                                modifier = Modifier.testTag("nav_home")
                            )
                            NavigationBarItem(
                                selected = currentScreen == Screen.FEED,
                                onClick = { currentScreen = Screen.FEED },
                                icon = { Icon(Icons.Default.Search, contentDescription = "Browse") },
                                label = { Text("Browse", fontSize = 11.sp) },
                                modifier = Modifier.testTag("nav_browse")
                            )
                            NavigationBarItem(
                                selected = currentScreen == Screen.MATCHES_CLAIMS,
                                onClick = { currentScreen = Screen.MATCHES_CLAIMS },
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            if (pendingMatches > 0) {
                                                Badge { Text("$pendingMatches") }
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = "Matches")
                                    }
                                },
                                label = { Text("Matches", fontSize = 11.sp) },
                                modifier = Modifier.testTag("nav_matches")
                            )
                            NavigationBarItem(
                                selected = currentScreen == Screen.MY_ITEMS,
                                onClick = { currentScreen = Screen.MY_ITEMS },
                                icon = { Icon(Icons.Default.Folder, contentDescription = "My Items") },
                                label = { Text("My Items", fontSize = 11.sp) },
                                modifier = Modifier.testTag("nav_my_items")
                            )
                            if (user.role == "admin") {
                                NavigationBarItem(
                                    selected = currentScreen == Screen.ADMIN,
                                    onClick = { currentScreen = Screen.ADMIN },
                                    icon = { Icon(Icons.Default.Security, contentDescription = "Admin") },
                                    label = { Text("Admin", fontSize = 11.sp) },
                                    modifier = Modifier.testTag("nav_admin")
                                )
                            }
                        }
                    }
                },
                floatingActionButton = {
                    if (currentScreen == Screen.DASHBOARD || currentScreen == Screen.FEED) {
                        FloatingActionButton(
                            onClick = {
                                initialPostIsLost = true
                                currentScreen = Screen.POST_ITEM
                            },
                            containerColor = PrimaryIndigo,
                            contentColor = Color.White,
                            modifier = Modifier.testTag("fab_post_item")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Post Item")
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentScreen) {
                        Screen.DASHBOARD -> DashboardScreen(
                            currentUser = user,
                            lostItems = lostItems,
                            foundItems = foundItems,
                            matches = matches,
                            claims = claims,
                            notifications = notifications,
                            onNavigateToPost = { isLost ->
                                initialPostIsLost = isLost
                                currentScreen = Screen.POST_ITEM
                            },
                            onNavigateToFeed = { filter ->
                                viewModel.setTypeFilter(filter)
                                currentScreen = Screen.FEED
                            },
                            onNavigateToMatches = { currentScreen = Screen.MATCHES_CLAIMS },
                            onNavigateToNotifications = { currentScreen = Screen.NOTIFICATIONS },
                            onSelectItem = { item ->
                                selectedItemForDetail = item
                                currentScreen = Screen.ITEM_DETAIL
                            },
                            onClaimFoundItem = { found -> claimTargetFoundItem = found },
                            onNavigateToWebPortal = { currentScreen = Screen.WEB_PORTAL }
                        )

                        Screen.FEED -> FeedScreen(
                            lostItems = lostItems,
                            foundItems = foundItems,
                            categories = categories,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                            selectedCategory = selectedCategory,
                            onCategorySelect = { viewModel.setCategoryFilter(it) },
                            selectedLocation = selectedLocation,
                            onLocationSelect = { viewModel.setLocationFilter(it) },
                            selectedType = selectedType,
                            onTypeSelect = { viewModel.setTypeFilter(it) },
                            onSelectItem = { item ->
                                selectedItemForDetail = item
                                currentScreen = Screen.ITEM_DETAIL
                            },
                            onClaimFoundItem = { found -> claimTargetFoundItem = found },
                            onNavigateToPost = { isLost ->
                                initialPostIsLost = isLost
                                currentScreen = Screen.POST_ITEM
                            }
                        )

                        Screen.MATCHES_CLAIMS -> ClaimsAndMatchesScreen(
                            currentUser = user,
                            matches = matches,
                            claims = claims,
                            foundItems = foundItems,
                            onUpdateMatchStatus = { id, status -> viewModel.updateMatchStatus(id, status) },
                            onResolveClaim = { claim, approved, note ->
                                viewModel.resolveClaim(claim, approved, note)
                            }
                        )

                        Screen.MY_ITEMS -> MyItemsScreen(
                            currentUser = user,
                            allLostItems = lostItems,
                            allFoundItems = foundItems,
                            onSelectItem = { item ->
                                selectedItemForDetail = item
                                currentScreen = Screen.ITEM_DETAIL
                            },
                            onNavigateToPost = { isLost ->
                                initialPostIsLost = isLost
                                currentScreen = Screen.POST_ITEM
                            },
                            onUpdateLostStatus = { id, status -> viewModel.updateLostStatus(id, status) },
                            onDeleteLostItem = { viewModel.deleteLostItem(it) }
                        )

                        Screen.ADMIN -> AdminScreen(
                            users = users,
                            lostItems = lostItems,
                            foundItems = foundItems,
                            categories = categories,
                            claims = claims,
                            onAddCategory = { name, icon -> viewModel.addCategory(name, icon) }
                        )

                        Screen.POST_ITEM -> PostItemScreen(
                            initialIsLost = initialPostIsLost,
                            currentUser = user,
                            categories = categories,
                            onBack = { currentScreen = Screen.DASHBOARD },
                            onSubmitLost = { name, catId, catName, loc, date, contact, desc, reward ->
                                viewModel.postLostItem(name, catId, catName, loc, date, contact, desc, reward) {
                                    currentScreen = Screen.FEED
                                }
                            },
                            onSubmitFound = { name, catId, catName, loc, storage, date, contact, desc, question ->
                                viewModel.postFoundItem(name, catId, catName, loc, storage, date, contact, desc, question) {
                                    currentScreen = Screen.FEED
                                }
                            }
                        )

                        Screen.ITEM_DETAIL -> {
                            val targetItem = selectedItemForDetail
                            if (targetItem != null) {
                                ItemDetailScreen(
                                    item = targetItem,
                                    currentUser = user,
                                    onBack = { currentScreen = Screen.DASHBOARD },
                                    onUpdateLostStatus = { id, status -> viewModel.updateLostStatus(id, status) },
                                    onUpdateFoundStatus = { id, status -> viewModel.updateFoundStatus(id, status) },
                                    onDeleteLostItem = { viewModel.deleteLostItem(it) },
                                    onDeleteFoundItem = { viewModel.deleteFoundItem(it) },
                                    onSubmitClaim = { found, proof, callback ->
                                        viewModel.submitClaim(found, proof, callback)
                                    }
                                )
                            } else {
                                currentScreen = Screen.DASHBOARD
                            }
                        }

                        Screen.NOTIFICATIONS -> NotificationsScreen(
                            notifications = notifications,
                            onBack = { currentScreen = Screen.DASHBOARD },
                            onMarkRead = { viewModel.markNotificationRead(it) },
                            onMarkAllRead = { viewModel.markAllNotificationsRead() }
                        )

                        Screen.WEB_PORTAL -> WebPortalScreen(
                            onBack = { currentScreen = Screen.DASHBOARD }
                        )
                    }
                }
            }
        }
    }

    if (claimTargetFoundItem != null) {
        val target = claimTargetFoundItem!!
        ClaimSubmissionDialog(
            foundItem = target,
            onDismiss = { claimTargetFoundItem = null },
            onSubmitClaim = { proof ->
                claimTargetFoundItem = null
                viewModel.submitClaim(target, proof) { _, _ -> }
            }
        )
    }
}
