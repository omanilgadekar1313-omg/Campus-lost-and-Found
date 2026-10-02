package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.CategoryDao
import com.example.data.dao.ClaimDao
import com.example.data.dao.FoundItemDao
import com.example.data.dao.LostItemDao
import com.example.data.dao.MatchDao
import com.example.data.dao.NotificationDao
import com.example.data.dao.UserDao
import com.example.data.model.CategoryEntity
import com.example.data.model.ClaimEntity
import com.example.data.model.FoundItemEntity
import com.example.data.model.LostItemEntity
import com.example.data.model.MatchEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        LostItemEntity::class,
        FoundItemEntity::class,
        ClaimEntity::class,
        MatchEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun lostItemDao(): LostItemDao
    abstract fun foundItemDao(): FoundItemDao
    abstract fun claimDao(): ClaimDao
    abstract fun matchDao(): MatchDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "campus_lost_found_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        private suspend fun populateInitialData(database: AppDatabase) {
            val categoryDao = database.categoryDao()
            val userDao = database.userDao()
            val lostDao = database.lostItemDao()
            val foundDao = database.foundItemDao()
            val matchDao = database.matchDao()
            val notifDao = database.notificationDao()

            // Pre-seed Categories
            val categories = listOf(
                CategoryEntity(1, "Electronics & Phones", "phone"),
                CategoryEntity(2, "ID Cards & Documents", "card"),
                CategoryEntity(3, "Wallets & Purses", "wallet"),
                CategoryEntity(4, "Keys & Lanyards", "keys"),
                CategoryEntity(5, "Bags & Backpacks", "bag"),
                CategoryEntity(6, "Books & Stationery", "book"),
                CategoryEntity(7, "Other Belongings", "other")
            )
            categoryDao.insertAll(categories)

            // Pre-seed Users (Student, Staff, Admin)
            val studentId = userDao.insertUser(
                UserEntity(
                    userId = 1,
                    name = "Aarav Patel",
                    email = "aarav@campus.edu",
                    password = "password123",
                    phone = "9876543210",
                    role = "student",
                    department = "Computer Science (TY BCS)"
                )
            )

            val staffId = userDao.insertUser(
                UserEntity(
                    userId = 2,
                    name = "Prof. Priya Sharma",
                    email = "priya@campus.edu",
                    password = "password123",
                    phone = "9812345678",
                    role = "staff",
                    department = "Central Library Staff"
                )
            )

            userDao.insertUser(
                UserEntity(
                    userId = 3,
                    name = "Campus Admin",
                    email = "admin@campus.edu",
                    password = "admin123",
                    phone = "9900011223",
                    role = "admin",
                    department = "Campus Security & Administration"
                )
            )

            // Pre-seed Lost Items
            val lostWalletId = lostDao.insertLostItem(
                LostItemEntity(
                    lostId = 1,
                    userId = studentId,
                    userName = "Aarav Patel",
                    userPhone = "9876543210",
                    userEmail = "aarav@campus.edu",
                    categoryId = 3,
                    categoryName = "Wallets & Purses",
                    itemName = "Titan Black Leather Wallet",
                    description = "Black leather wallet with college ID and driving license. Lost during afternoon lunch break.",
                    lostDate = "2026-10-01",
                    lostLocation = "Campus Cafeteria",
                    contact = "9876543210",
                    status = "Lost",
                    rewardNote = "Gratitude & Treat in Cafeteria"
                )
            )

            val lostCardId = lostDao.insertLostItem(
                LostItemEntity(
                    lostId = 2,
                    userId = studentId,
                    userName = "Aarav Patel",
                    userPhone = "9876543210",
                    userEmail = "aarav@campus.edu",
                    categoryId = 2,
                    categoryName = "ID Cards & Documents",
                    itemName = "Student Smart RFID ID Card",
                    description = "TY BCS Roll No. 2024CS042 with college blue lanyard and barcode.",
                    lostDate = "2026-09-30",
                    lostLocation = "Library 2nd Floor",
                    contact = "9876543210",
                    status = "Lost",
                    rewardNote = "Important for semester exams!"
                )
            )

            lostDao.insertLostItem(
                LostItemEntity(
                    lostId = 3,
                    userId = studentId,
                    userName = "Aarav Patel",
                    userPhone = "9876543210",
                    userEmail = "aarav@campus.edu",
                    categoryId = 1,
                    categoryName = "Electronics & Phones",
                    itemName = "Casio FX-991EX Scientific Calculator",
                    description = "ClassWiz calculator in protective cover with initials 'AP' written in marker.",
                    lostDate = "2026-10-01",
                    lostLocation = "Room 304 (Math Block)",
                    contact = "9876543210",
                    status = "Lost"
                )
            )

            // Pre-seed Found Items
            val foundWalletId = foundDao.insertFoundItem(
                FoundItemEntity(
                    foundId = 1,
                    userId = staffId,
                    userName = "Prof. Priya Sharma",
                    userPhone = "9812345678",
                    userEmail = "priya@campus.edu",
                    categoryId = 3,
                    categoryName = "Wallets & Purses",
                    itemName = "Titan Leather Wallet (Dark Color)",
                    description = "Found under table #6 in Cafeteria. Contains cards and currency. Safely kept.",
                    foundDate = "2026-10-01",
                    foundLocation = "Campus Cafeteria",
                    storageLocation = "Central Library Help Desk Room 102",
                    contact = "9812345678",
                    status = "Found",
                    identifyingQuestion = "Tell us the name on the ID card inside or specific cards."
                )
            )

            val foundCardId = foundDao.insertFoundItem(
                FoundItemEntity(
                    foundId = 2,
                    userId = staffId,
                    userName = "Prof. Priya Sharma",
                    userPhone = "9812345678",
                    userEmail = "priya@campus.edu",
                    categoryId = 2,
                    categoryName = "ID Cards & Documents",
                    itemName = "College Student ID Card (Computer Dept)",
                    description = "Found near the reference book racks on the 2nd floor reading hall.",
                    foundDate = "2026-09-30",
                    foundLocation = "Library 2nd Floor",
                    storageLocation = "Library Circulation Counter",
                    contact = "9812345678",
                    status = "Found",
                    identifyingQuestion = "Verify your Student Roll number and surname."
                )
            )

            foundDao.insertFoundItem(
                FoundItemEntity(
                    foundId = 3,
                    userId = 3,
                    userName = "Campus Admin",
                    userPhone = "9900011223",
                    userEmail = "admin@campus.edu",
                    categoryId = 1,
                    categoryName = "Electronics & Phones",
                    itemName = "Apple AirPods Pro 2 in Case",
                    description = "White charging case found on seat G-14 after the guest lecture.",
                    foundDate = "2026-10-02",
                    foundLocation = "Main Auditorium",
                    storageLocation = "Campus Security Control Room 01",
                    contact = "9900011223",
                    status = "Found",
                    identifyingQuestion = "Pairing name or serial number / case scratch details."
                )
            )

            // Pre-seed Matches connecting Lost and Found records
            matchDao.insertMatch(
                MatchEntity(
                    matchId = 1,
                    lostId = lostWalletId,
                    foundId = foundWalletId,
                    lostItemName = "Titan Black Leather Wallet",
                    foundItemName = "Titan Leather Wallet (Dark Color)",
                    categoryName = "Wallets & Purses",
                    matchDate = "2026-10-01",
                    matchScore = 94,
                    matchReason = "Same Category 'Wallets & Purses' and matching location 'Campus Cafeteria'",
                    matchStatus = "Suggested"
                )
            )

            matchDao.insertMatch(
                MatchEntity(
                    matchId = 2,
                    lostId = lostCardId,
                    foundId = foundCardId,
                    lostItemName = "Student Smart RFID ID Card",
                    foundItemName = "College Student ID Card (Computer Dept)",
                    categoryName = "ID Cards & Documents",
                    matchDate = "2026-09-30",
                    matchScore = 91,
                    matchReason = "Same Category 'ID Cards' and exact location 'Library 2nd Floor'",
                    matchStatus = "Suggested"
                )
            )

            // Pre-seed Notifications
            notifDao.insertNotification(
                NotificationEntity(
                    notificationId = 1,
                    userId = studentId,
                    title = "Potential Match Detected!",
                    message = "A found item 'Titan Leather Wallet (Dark Color)' matches your reported lost item in Campus Cafeteria.",
                    date = "2026-10-01",
                    status = "Unread",
                    type = "match"
                )
            )

            notifDao.insertNotification(
                NotificationEntity(
                    notificationId = 2,
                    userId = studentId,
                    title = "ID Card Found in Library",
                    message = "A Student ID Card has been submitted to Library Circulation Counter that closely matches your report.",
                    date = "2026-09-30",
                    status = "Read",
                    type = "match"
                )
            )
        }
    }
}
