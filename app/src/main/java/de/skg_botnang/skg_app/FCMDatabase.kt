package de.skg_botnang.skg_app

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Entity
data class FCMMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val body: String
)

@Database(entities = [FCMMessage::class], version = 1)
abstract class FCMDatabase : RoomDatabase() {
    abstract fun messageDao(): FCMMessageDao

    companion object {
        @Volatile
        private var INSTANCE: FCMDatabase? = null

        fun getDatabase(context: Context): FCMDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FCMDatabase::class.java,
                    "fcm_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@Dao
interface FCMMessageDao {
    @Query("SELECT * FROM FCMMessage")
    suspend fun getAllMessages(): List<FCMMessage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: FCMMessage): Long

    @Query("SELECT * FROM FCMMessage WHERE id =:id")
    fun get(id: Long): FCMMessage
}
