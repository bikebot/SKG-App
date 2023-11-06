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
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object Converters {
    @TypeConverter
    @JvmStatic
    fun stringToZonedDateTime(isoString: String?): ZonedDateTime? {
        return isoString?.let {
            ZonedDateTime.parse(it, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        }
    }

    @TypeConverter
    @JvmStatic
    fun zonedDateTimeToISOString(zonedDateTime: ZonedDateTime?): String? {
        return zonedDateTime?.let {
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(it)
        }
    }
}


@Entity
@TypeConverters(Converters::class)
data class FCMMessage(
    val title: String,
    val body: String,
    val time: ZonedDateTime = ZonedDateTime.now(),
    @PrimaryKey(autoGenerate = true) val id: Long = 0
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
    suspend fun getAll(): List<FCMMessage>

    @Query("SELECT * FROM FCMMessage ORDER BY time DESC LIMIT :n")
    suspend fun getSome(n: Int): List<FCMMessage>

    @Query("SELECT * FROM FCMMessage WHERE id =:id")
    fun get(id: Long): FCMMessage

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: FCMMessage): Long
}
