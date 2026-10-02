package com.example.data.repository

import com.example.data.AppDatabase
import com.example.data.model.CategoryEntity
import com.example.data.model.ClaimEntity
import com.example.data.model.FoundItemEntity
import com.example.data.model.LostItemEntity
import com.example.data.model.MatchEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LostAndFoundRepository(private val database: AppDatabase) {

    private val userDao = database.userDao()
    private val lostDao = database.lostItemDao()
    private val foundDao = database.foundItemDao()
    private val categoryDao = database.categoryDao()
    private val claimDao = database.claimDao()
    private val matchDao = database.matchDao()
    private val notificationDao = database.notificationDao()

    // Users
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()

    suspend fun getUserByEmail(email: String): UserEntity? = userDao.getUserByEmail(email)

    suspend fun registerUser(user: UserEntity): Result<Long> {
        val existing = userDao.getUserByEmail(user.email.trim())
        if (existing != null) {
            return Result.failure(Exception("Email is already registered!"))
        }
        val id = userDao.insertUser(user)
        return Result.success(id)
    }

    suspend fun getUserById(id: Long): UserEntity? = userDao.getUserById(id)

    // Categories
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    suspend fun addCategory(category: CategoryEntity) = categoryDao.insertCategory(category)

    // Lost Items
    val allLostItems: Flow<List<LostItemEntity>> = lostDao.getAllLostItems()

    fun getLostItemsByUser(userId: Long): Flow<List<LostItemEntity>> = lostDao.getLostItemsByUser(userId)

    suspend fun addLostItem(item: LostItemEntity): Long {
        val lostId = lostDao.insertLostItem(item)
        val createdItem = item.copy(lostId = lostId)
        runMatcherForLostItem(createdItem)
        return lostId
    }

    suspend fun updateLostItemStatus(lostId: Long, status: String) {
        lostDao.updateStatus(lostId, status)
    }

    suspend fun deleteLostItem(item: LostItemEntity) {
        lostDao.deleteLostItem(item)
    }

    // Found Items
    val allFoundItems: Flow<List<FoundItemEntity>> = foundDao.getAllFoundItems()

    fun getFoundItemsByUser(userId: Long): Flow<List<FoundItemEntity>> = foundDao.getFoundItemsByUser(userId)

    suspend fun addFoundItem(item: FoundItemEntity): Long {
        val foundId = foundDao.insertFoundItem(item)
        val createdItem = item.copy(foundId = foundId)
        runMatcherForFoundItem(createdItem)
        return foundId
    }

    suspend fun updateFoundItemStatus(foundId: Long, status: String) {
        foundDao.updateStatus(foundId, status)
    }

    suspend fun deleteFoundItem(item: FoundItemEntity) {
        foundDao.deleteFoundItem(item)
    }

    // Claims
    val allClaims: Flow<List<ClaimEntity>> = claimDao.getAllClaims()

    fun getClaimsByUser(userId: Long): Flow<List<ClaimEntity>> = claimDao.getClaimsByUser(userId)

    fun getClaimsForFoundItem(foundId: Long): Flow<List<ClaimEntity>> = claimDao.getClaimsForFoundItem(foundId)

    suspend fun submitClaim(claim: ClaimEntity): Long {
        val id = claimDao.insertClaim(claim)
        val foundItem = foundDao.getFoundItemById(claim.foundId)
        if (foundItem != null) {
            // Notify finder of new claim
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            notificationDao.insertNotification(
                NotificationEntity(
                    userId = foundItem.userId,
                    title = "New Claim for '${claim.itemName}'",
                    message = "${claim.claimantName} has submitted a claim with proof of ownership.",
                    date = today,
                    type = "claim"
                )
            )
        }
        return id
    }

    suspend fun updateClaimStatus(claimId: Long, status: String, note: String, foundId: Long, claimantId: Long, itemName: String) {
        claimDao.updateClaimStatus(claimId, status, note)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        if (status == "Approved") {
            foundDao.updateStatus(foundId, "Claimed")
            notificationDao.insertNotification(
                NotificationEntity(
                    userId = claimantId,
                    title = "Claim Approved! 🎉",
                    message = "Your claim for '$itemName' has been approved! Check item details to coordinate handover.",
                    date = today,
                    type = "claim"
                )
            )
        } else if (status == "Rejected") {
            notificationDao.insertNotification(
                NotificationEntity(
                    userId = claimantId,
                    title = "Claim Update for '$itemName'",
                    message = "Your claim could not be verified. Note: $note",
                    date = today,
                    type = "claim"
                )
            )
        }
    }

    // Matches
    val allMatches: Flow<List<MatchEntity>> = matchDao.getAllMatches()

    suspend fun updateMatchStatus(matchId: Long, status: String) {
        matchDao.updateMatchStatus(matchId, status)
    }

    // Notifications
    fun getNotificationsForUser(userId: Long): Flow<List<NotificationEntity>> =
        notificationDao.getNotificationsForUser(userId)

    suspend fun markNotificationAsRead(id: Long) = notificationDao.markAsRead(id)

    suspend fun markAllNotificationsAsRead(userId: Long) = notificationDao.markAllAsRead(userId)

    // Smart Matcher logic
    private suspend fun runMatcherForLostItem(lost: LostItemEntity) {
        val foundItems = foundDao.getAllFoundItemsList()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        for (found in foundItems) {
            if (found.status == "Resolved") continue
            val existing = matchDao.findExistingMatch(lost.lostId, found.foundId)
            if (existing.isNotEmpty()) continue

            val score = calculateMatchScore(
                lost.categoryName, found.categoryName,
                lost.itemName, found.itemName,
                lost.lostLocation, found.foundLocation,
                lost.description, found.description
            )

            if (score >= 40) {
                val reason = buildReason(lost.categoryName == found.categoryName, lost.lostLocation, found.foundLocation)
                matchDao.insertMatch(
                    MatchEntity(
                        lostId = lost.lostId,
                        foundId = found.foundId,
                        lostItemName = lost.itemName,
                        foundItemName = found.itemName,
                        categoryName = lost.categoryName,
                        matchDate = today,
                        matchScore = score,
                        matchReason = reason
                    )
                )
                notificationDao.insertNotification(
                    NotificationEntity(
                        userId = lost.userId,
                        title = "Smart Match Found ($score%)",
                        message = "Found item '${found.itemName}' may match your reported lost item!",
                        date = today,
                        type = "match"
                    )
                )
            }
        }
    }

    private suspend fun runMatcherForFoundItem(found: FoundItemEntity) {
        val lostItems = lostDao.getAllLostItemsList()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        for (lost in lostItems) {
            if (lost.status == "Resolved") continue
            val existing = matchDao.findExistingMatch(lost.lostId, found.foundId)
            if (existing.isNotEmpty()) continue

            val score = calculateMatchScore(
                lost.categoryName, found.categoryName,
                lost.itemName, found.itemName,
                lost.lostLocation, found.foundLocation,
                lost.description, found.description
            )

            if (score >= 40) {
                val reason = buildReason(lost.categoryName == found.categoryName, lost.lostLocation, found.foundLocation)
                matchDao.insertMatch(
                    MatchEntity(
                        lostId = lost.lostId,
                        foundId = found.foundId,
                        lostItemName = lost.itemName,
                        foundItemName = found.itemName,
                        categoryName = lost.categoryName,
                        matchDate = today,
                        matchScore = score,
                        matchReason = reason
                    )
                )
                notificationDao.insertNotification(
                    NotificationEntity(
                        userId = lost.userId,
                        title = "Smart Match Detected ($score%)",
                        message = "A new found item '${found.itemName}' matches your lost item report!",
                        date = today,
                        type = "match"
                    )
                )
            }
        }
    }

    private fun calculateMatchScore(
        cat1: String, cat2: String,
        name1: String, name2: String,
        loc1: String, loc2: String,
        desc1: String, desc2: String
    ): Int {
        var score = 0
        if (cat1.equals(cat2, ignoreCase = true)) {
            score += 40
        }
        val words1 = name1.lowercase(Locale.ROOT).split(" ", ",", "-", "_").filter { it.length > 2 }
        val words2 = name2.lowercase(Locale.ROOT).split(" ", ",", "-", "_").filter { it.length > 2 }
        val commonNameWords = words1.intersect(words2.toSet())
        if (commonNameWords.isNotEmpty()) {
            score += 35
        } else {
            // Check substring
            if (name1.contains(name2, ignoreCase = true) || name2.contains(name1, ignoreCase = true)) {
                score += 25
            }
        }

        if (loc1.isNotBlank() && loc2.isNotBlank()) {
            if (loc1.equals(loc2, ignoreCase = true)) {
                score += 25
            } else if (loc1.contains(loc2, ignoreCase = true) || loc2.contains(loc1, ignoreCase = true)) {
                score += 15
            }
        }

        return score.coerceAtMost(99)
    }

    private fun buildReason(sameCategory: Boolean, loc1: String, loc2: String): String {
        val parts = mutableListOf<String>()
        if (sameCategory) parts.add("Matching Category")
        if (loc1.equals(loc2, ignoreCase = true)) {
            parts.add("Same Location ($loc1)")
        } else if (loc1.contains(loc2, ignoreCase = true) || loc2.contains(loc1, ignoreCase = true)) {
            parts.add("Nearby Location")
        }
        return if (parts.isEmpty()) "Keyword and Attribute Similarity" else parts.joinToString(" & ")
    }
}
