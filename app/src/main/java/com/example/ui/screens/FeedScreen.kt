package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity
import com.example.data.model.FoundItemEntity
import com.example.data.model.LostItemEntity
import com.example.ui.components.FoundItemCard
import com.example.ui.components.LostItemCard
import com.example.ui.components.getCategoryIcon
import java.util.Locale

@Composable
fun FeedScreen(
    lostItems: List<LostItemEntity>,
    foundItems: List<FoundItemEntity>,
    categories: List<CategoryEntity>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: Long?,
    onCategorySelect: (Long?) -> Unit,
    selectedLocation: String?,
    onLocationSelect: (String?) -> Unit,
    selectedType: String, // "ALL", "LOST", "FOUND"
    onTypeSelect: (String) -> Unit,
    onSelectItem: (item: Any) -> Unit,
    onClaimFoundItem: (FoundItemEntity) -> Unit,
    onNavigateToPost: (isLost: Boolean) -> Unit
) {
    var showOnlyActive by remember { mutableStateOf(true) }

    val campusLocations = listOf(
        "Library 2nd Floor",
        "Campus Cafeteria",
        "Main Auditorium",
        "Computer Lab 2",
        "Sports Complex",
        "Room 304 (Math Block)",
        "Student Hostel Block"
    )

    // Filtered lists
    val filteredLost by remember(lostItems, searchQuery, selectedCategory, selectedLocation, showOnlyActive) {
        derivedStateOf {
            lostItems.filter { item ->
                val matchesQuery = searchQuery.isBlank() ||
                        item.itemName.contains(searchQuery, ignoreCase = true) ||
                        item.description.contains(searchQuery, ignoreCase = true) ||
                        item.lostLocation.contains(searchQuery, ignoreCase = true)

                val matchesCat = selectedCategory == null || item.categoryId == selectedCategory
                val matchesLoc = selectedLocation == null || item.lostLocation.contains(selectedLocation, ignoreCase = true)
                val matchesActive = !showOnlyActive || item.status == "Lost"

                matchesQuery && matchesCat && matchesLoc && matchesActive
            }
        }
    }

    val filteredFound by remember(foundItems, searchQuery, selectedCategory, selectedLocation, showOnlyActive) {
        derivedStateOf {
            foundItems.filter { item ->
                val matchesQuery = searchQuery.isBlank() ||
                        item.itemName.contains(searchQuery, ignoreCase = true) ||
                        item.description.contains(searchQuery, ignoreCase = true) ||
                        item.foundLocation.contains(searchQuery, ignoreCase = true) ||
                        item.storageLocation.contains(searchQuery, ignoreCase = true)

                val matchesCat = selectedCategory == null || item.categoryId == selectedCategory
                val matchesLoc = selectedLocation == null || item.foundLocation.contains(selectedLocation, ignoreCase = true)
                val matchesActive = !showOnlyActive || item.status == "Found"

                matchesQuery && matchesCat && matchesLoc && matchesActive
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search & Filter Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_field"),
                    placeholder = { Text("Search by item name, keyword, location...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Type Tabs: ALL, LOST, FOUND
                TabRow(
                    selectedTabIndex = when (selectedType) {
                        "LOST" -> 1
                        "FOUND" -> 2
                        else -> 0
                    },
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = selectedType == "ALL",
                        onClick = { onTypeSelect("ALL") },
                        text = { Text("All (${filteredLost.size + filteredFound.size})", fontWeight = FontWeight.SemiBold) },
                        modifier = Modifier.testTag("tab_all")
                    )
                    Tab(
                        selected = selectedType == "LOST",
                        onClick = { onTypeSelect("LOST") },
                        text = { Text("Lost (${filteredLost.size})", fontWeight = FontWeight.SemiBold) },
                        modifier = Modifier.testTag("tab_lost")
                    )
                    Tab(
                        selected = selectedType == "FOUND",
                        onClick = { onTypeSelect("FOUND") },
                        text = { Text("Found (${filteredFound.size})", fontWeight = FontWeight.SemiBold) },
                        modifier = Modifier.testTag("tab_found")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Categories horizontal scroller
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { onCategorySelect(null) },
                            label = { Text("All Categories") },
                            modifier = Modifier.testTag("cat_all")
                        )
                    }
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat.categoryId,
                            onClick = { onCategorySelect(cat.categoryId) },
                            leadingIcon = {
                                Icon(
                                    imageVector = getCategoryIcon(cat.iconType),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = { Text(cat.categoryName) },
                            modifier = Modifier.testTag("cat_${cat.categoryId}")
                        )
                    }
                }

                // Locations quick filter
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(top = 4.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedLocation == null,
                            onClick = { onLocationSelect(null) },
                            label = { Text("All Locations", fontSize = 12.sp) }
                        )
                    }
                    items(campusLocations) { loc ->
                        FilterChip(
                            selected = selectedLocation == loc,
                            onClick = { onLocationSelect(loc) },
                            leadingIcon = {
                                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp))
                            },
                            label = { Text(loc, fontSize = 12.sp) }
                        )
                    }
                }
            }
        }

        // Results list
        val totalCount = when (selectedType) {
            "LOST" -> filteredLost.size
            "FOUND" -> filteredFound.size
            else -> filteredLost.size + filteredFound.size
        }

        if (totalCount == 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Items Found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try adjusting your search terms or filters, or post a new lost/found report.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = { onNavigateToPost(true) }) {
                            Text("Post Lost")
                        }
                        Button(onClick = { onNavigateToPost(false) }) {
                            Text("Post Found")
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // If ALL or LOST selected
                if (selectedType == "ALL" || selectedType == "LOST") {
                    if (selectedType == "ALL" && filteredLost.isNotEmpty()) {
                        item {
                            Text(
                                text = "Lost Reports (${filteredLost.size})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                    items(filteredLost, key = { "lost_${it.lostId}" }) { item ->
                        LostItemCard(
                            item = item,
                            onClick = { onSelectItem(item) }
                        )
                    }
                }

                // If ALL or FOUND selected
                if (selectedType == "ALL" || selectedType == "FOUND") {
                    if (selectedType == "ALL" && filteredFound.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Found & Deposited (${filteredFound.size})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                    items(filteredFound, key = { "found_${it.foundId}" }) { item ->
                        FoundItemCard(
                            item = item,
                            onClick = { onSelectItem(item) },
                            onClaimClick = { onClaimFoundItem(item) }
                        )
                    }
                }
            }
        }
    }
}
