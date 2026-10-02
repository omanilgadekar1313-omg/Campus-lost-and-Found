package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val userId: Long = 0,
    val name: String,
    val email: String,
    val password: String,
    val phone: String,
    val role: String = "student", // "student", "staff", "admin"
    val department: String = "Computer Science"
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val categoryId: Long = 0,
    val categoryName: String,
    val iconType: String = "other" // "phone", "card", "keys", "wallet", "bag", "book", "other"
)

@Entity(tableName = "lost_items")
data class LostItemEntity(
    @PrimaryKey(autoGenerate = true) val lostId: Long = 0,
    val userId: Long,
    val userName: String,
    val userPhone: String,
    val userEmail: String,
    val categoryId: Long,
    val categoryName: String,
    val itemName: String,
    val description: String,
    val lostDate: String,
    val lostLocation: String,
    val contact: String,
    val status: String = "Lost", // "Lost", "Claimed", "Resolved"
    val rewardNote: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "found_items")
data class FoundItemEntity(
    @PrimaryKey(autoGenerate = true) val foundId: Long = 0,
    val userId: Long,
    val userName: String,
    val userPhone: String,
    val userEmail: String,
    val categoryId: Long,
    val categoryName: String,
    val itemName: String,
    val description: String,
    val foundDate: String,
    val foundLocation: String,
    val storageLocation: String = "Security Desk / Main Office",
    val contact: String,
    val status: String = "Found", // "Found", "Claimed", "Resolved"
    val identifyingQuestion: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "claims")
data class ClaimEntity(
    @PrimaryKey(autoGenerate = true) val claimId: Long = 0,
    val foundId: Long,
    val itemName: String,
    val userId: Long, // claimant
    val claimantName: String,
    val claimantEmail: String,
    val claimantPhone: String,
    val claimDate: String,
    val proofDescription: String,
    val claimStatus: String = "Pending", // "Pending", "Approved", "Rejected", "Resolved"
    val responseNote: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey(autoGenerate = true) val matchId: Long = 0,
    val lostId: Long,
    val foundId: Long,
    val lostItemName: String,
    val foundItemName: String,
    val categoryName: String,
    val matchDate: String,
    val matchScore: Int, // 0 - 100%
    val matchReason: String,
    val matchStatus: String = "Suggested", // "Suggested", "Accepted", "Dismissed"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val notificationId: Long = 0,
    val userId: Long,
    val title: String,
    val message: String,
    val date: String,
    val status: String = "Unread", // "Unread", "Read"
    val type: String = "system", // "match", "claim", "status_change", "system"
    val createdAt: Long = System.currentTimeMillis()
)
