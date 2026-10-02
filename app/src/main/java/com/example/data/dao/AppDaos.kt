package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CategoryEntity
import com.example.data.model.ClaimEntity
import com.example.data.model.FoundItemEntity
import com.example.data.model.LostItemEntity
import com.example.data.model.MatchEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE userId = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM users ORDER BY userId DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY categoryId ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories")
    suspend fun getAllCategoriesList(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)
}

@Dao
interface LostItemDao {
    @Query("SELECT * FROM lost_items ORDER BY createdAt DESC")
    fun getAllLostItems(): Flow<List<LostItemEntity>>

    @Query("SELECT * FROM lost_items ORDER BY createdAt DESC")
    suspend fun getAllLostItemsList(): List<LostItemEntity>

    @Query("SELECT * FROM lost_items WHERE userId = :userId ORDER BY createdAt DESC")
    fun getLostItemsByUser(userId: Long): Flow<List<LostItemEntity>>

    @Query("SELECT * FROM lost_items WHERE lostId = :id LIMIT 1")
    suspend fun getLostItemById(id: Long): LostItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLostItem(item: LostItemEntity): Long

    @Update
    suspend fun updateLostItem(item: LostItemEntity)

    @Delete
    suspend fun deleteLostItem(item: LostItemEntity)

    @Query("UPDATE lost_items SET status = :status WHERE lostId = :id")
    suspend fun updateStatus(id: Long, status: String)
}

@Dao
interface FoundItemDao {
    @Query("SELECT * FROM found_items ORDER BY createdAt DESC")
    fun getAllFoundItems(): Flow<List<FoundItemEntity>>

    @Query("SELECT * FROM found_items ORDER BY createdAt DESC")
    suspend fun getAllFoundItemsList(): List<FoundItemEntity>

    @Query("SELECT * FROM found_items WHERE userId = :userId ORDER BY createdAt DESC")
    fun getFoundItemsByUser(userId: Long): Flow<List<FoundItemEntity>>

    @Query("SELECT * FROM found_items WHERE foundId = :id LIMIT 1")
    suspend fun getFoundItemById(id: Long): FoundItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoundItem(item: FoundItemEntity): Long

    @Update
    suspend fun updateFoundItem(item: FoundItemEntity)

    @Delete
    suspend fun deleteFoundItem(item: FoundItemEntity)

    @Query("UPDATE found_items SET status = :status WHERE foundId = :id")
    suspend fun updateStatus(id: Long, status: String)
}

@Dao
interface ClaimDao {
    @Query("SELECT * FROM claims ORDER BY createdAt DESC")
    fun getAllClaims(): Flow<List<ClaimEntity>>

    @Query("SELECT * FROM claims WHERE userId = :userId ORDER BY createdAt DESC")
    fun getClaimsByUser(userId: Long): Flow<List<ClaimEntity>>

    @Query("SELECT * FROM claims WHERE foundId = :foundId ORDER BY createdAt DESC")
    fun getClaimsForFoundItem(foundId: Long): Flow<List<ClaimEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClaim(claim: ClaimEntity): Long

    @Update
    suspend fun updateClaim(claim: ClaimEntity)

    @Delete
    suspend fun deleteClaim(claim: ClaimEntity)

    @Query("UPDATE claims SET claimStatus = :status, responseNote = :note WHERE claimId = :claimId")
    suspend fun updateClaimStatus(claimId: Long, status: String, note: String)
}

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches ORDER BY createdAt DESC")
    fun getAllMatches(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE lostId = :lostId OR foundId = :foundId")
    suspend fun findExistingMatch(lostId: Long, foundId: Long): List<MatchEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity): Long

    @Update
    suspend fun updateMatch(match: MatchEntity)

    @Query("UPDATE matches SET matchStatus = :status WHERE matchId = :id")
    suspend fun updateMatchStatus(id: Long, status: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId OR userId = 0 ORDER BY createdAt DESC")
    fun getNotificationsForUser(userId: Long): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET status = 'Read' WHERE notificationId = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notifications SET status = 'Read' WHERE userId = :userId OR userId = 0")
    suspend fun markAllAsRead(userId: Long)
}
