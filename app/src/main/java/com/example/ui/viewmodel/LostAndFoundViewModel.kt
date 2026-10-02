package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.model.CategoryEntity
import com.example.data.model.ClaimEntity
import com.example.data.model.FoundItemEntity
import com.example.data.model.LostItemEntity
import com.example.data.model.MatchEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.UserEntity
import com.example.data.repository.LostAndFoundRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LostAndFoundViewModel(
    application: Application,
    private val repository: LostAndFoundRepository
) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("campus_lost_found_prefs", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    val allLostItems: StateFlow<List<LostItemEntity>> = repository.allLostItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFoundItems: StateFlow<List<FoundItemEntity>> = repository.allFoundItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allClaims: StateFlow<List<ClaimEntity>> = repository.allClaims
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMatches: StateFlow<List<MatchEntity>> = repository.allMatches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search and Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()

    private val _selectedLocation = MutableStateFlow<String?>(null)
    val selectedLocation: StateFlow<String?> = _selectedLocation.asStateFlow()

    private val _selectedTypeFilter = MutableStateFlow("ALL") // "ALL", "LOST", "FOUND"
    val selectedTypeFilter: StateFlow<String> = _selectedTypeFilter.asStateFlow()

    // Notification list dynamic for current user
    private val _notifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val notifications: StateFlow<List<NotificationEntity>> = _notifications.asStateFlow()

    init {
        // Restore session or load default student demo user
        val savedUserId = prefs.getLong("saved_user_id", -1L)
        viewModelScope.launch {
            if (savedUserId != -1L) {
                val user = repository.getUserById(savedUserId)
                if (user != null) {
                    _currentUser.value = user
                    observeNotifications(user.userId)
                } else {
                    quickLogin("student")
                }
            } else {
                quickLogin("student")
            }
        }
    }

    private fun observeNotifications(userId: Long) {
        viewModelScope.launch {
            repository.getNotificationsForUser(userId).collect { list ->
                _notifications.value = list
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(categoryId: Long?) {
        _selectedCategoryId.value = if (_selectedCategoryId.value == categoryId) null else categoryId
    }

    fun setLocationFilter(location: String?) {
        _selectedLocation.value = if (_selectedLocation.value == location) null else location
    }

    fun setTypeFilter(type: String) {
        _selectedTypeFilter.value = type
    }

    fun login(email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email.trim())
            if (user == null) {
                onResult(false, "No account found with this email.")
            } else if (user.password != pass.trim()) {
                onResult(false, "Incorrect password.")
            } else {
                _currentUser.value = user
                prefs.edit().putLong("saved_user_id", user.userId).apply()
                observeNotifications(user.userId)
                onResult(true, "Welcome back, ${user.name}!")
            }
        }
    }

    fun register(
        name: String,
        email: String,
        pass: String,
        phone: String,
        role: String,
        dept: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val newUser = UserEntity(
                name = name.trim(),
                email = email.trim(),
                password = pass.trim(),
                phone = phone.trim(),
                role = role,
                department = dept.trim()
            )
            val result = repository.registerUser(newUser)
            if (result.isSuccess) {
                val id = result.getOrNull() ?: 0L
                val userWithId = newUser.copy(userId = id)
                _currentUser.value = userWithId
                prefs.edit().putLong("saved_user_id", id).apply()
                observeNotifications(id)
                onResult(true, "Registration successful!")
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun quickLogin(role: String) {
        viewModelScope.launch {
            val targetEmail = when (role.lowercase()) {
                "admin" -> "admin@campus.edu"
                "staff" -> "priya@campus.edu"
                else -> "aarav@campus.edu"
            }
            val user = repository.getUserByEmail(targetEmail)
            if (user != null) {
                _currentUser.value = user
                prefs.edit().putLong("saved_user_id", user.userId).apply()
                observeNotifications(user.userId)
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        prefs.edit().remove("saved_user_id").apply()
        _notifications.value = emptyList()
    }

    fun postLostItem(
        itemName: String,
        categoryId: Long,
        categoryName: String,
        location: String,
        date: String,
        contact: String,
        description: String,
        rewardNote: String,
        onComplete: () -> Unit
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val item = LostItemEntity(
                userId = user.userId,
                userName = user.name,
                userPhone = user.phone,
                userEmail = user.email,
                categoryId = categoryId,
                categoryName = categoryName,
                itemName = itemName.trim(),
                description = description.trim(),
                lostDate = date,
                lostLocation = location.trim(),
                contact = if (contact.isNotBlank()) contact.trim() else user.phone,
                rewardNote = rewardNote.trim()
            )
            repository.addLostItem(item)
            onComplete()
        }
    }

    fun postFoundItem(
        itemName: String,
        categoryId: Long,
        categoryName: String,
        location: String,
        storageLocation: String,
        date: String,
        contact: String,
        description: String,
        identifyingQuestion: String,
        onComplete: () -> Unit
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val item = FoundItemEntity(
                userId = user.userId,
                userName = user.name,
                userPhone = user.phone,
                userEmail = user.email,
                categoryId = categoryId,
                categoryName = categoryName,
                itemName = itemName.trim(),
                description = description.trim(),
                foundDate = date,
                foundLocation = location.trim(),
                storageLocation = storageLocation.ifBlank { "Campus Security Desk Room 102" }.trim(),
                contact = if (contact.isNotBlank()) contact.trim() else user.phone,
                identifyingQuestion = identifyingQuestion.trim()
            )
            repository.addFoundItem(item)
            onComplete()
        }
    }

    fun updateLostStatus(lostId: Long, status: String) {
        viewModelScope.launch {
            repository.updateLostItemStatus(lostId, status)
        }
    }

    fun updateFoundStatus(foundId: Long, status: String) {
        viewModelScope.launch {
            repository.updateFoundItemStatus(foundId, status)
        }
    }

    fun deleteLostItem(item: LostItemEntity) {
        viewModelScope.launch {
            repository.deleteLostItem(item)
        }
    }

    fun deleteFoundItem(item: FoundItemEntity) {
        viewModelScope.launch {
            repository.deleteFoundItem(item)
        }
    }

    fun submitClaim(
        foundItem: FoundItemEntity,
        proofDescription: String,
        onComplete: (Boolean, String) -> Unit
    ) {
        val user = _currentUser.value ?: return
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        viewModelScope.launch {
            val claim = ClaimEntity(
                foundId = foundItem.foundId,
                itemName = foundItem.itemName,
                userId = user.userId,
                claimantName = user.name,
                claimantEmail = user.email,
                claimantPhone = user.phone,
                claimDate = today,
                proofDescription = proofDescription.trim()
            )
            repository.submitClaim(claim)
            onComplete(true, "Claim submitted! The finder and administration will verify your details.")
        }
    }

    fun resolveClaim(
        claim: ClaimEntity,
        approved: Boolean,
        adminNote: String
    ) {
        viewModelScope.launch {
            val newStatus = if (approved) "Approved" else "Rejected"
            repository.updateClaimStatus(
                claimId = claim.claimId,
                status = newStatus,
                note = adminNote,
                foundId = claim.foundId,
                claimantId = claim.userId,
                itemName = claim.itemName
            )
        }
    }

    fun updateMatchStatus(matchId: Long, status: String) {
        viewModelScope.launch {
            repository.updateMatchStatus(matchId, status)
        }
    }

    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsRead() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.markAllNotificationsAsRead(user.userId)
        }
    }

    fun addCategory(name: String, iconType: String) {
        viewModelScope.launch {
            repository.addCategory(CategoryEntity(categoryName = name.trim(), iconType = iconType))
        }
    }
}

class LostAndFoundViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = AppDatabase.getDatabase(application, kotlinx.coroutines.GlobalScope)
        val repository = LostAndFoundRepository(database)
        return LostAndFoundViewModel(application, repository) as T
    }
}
