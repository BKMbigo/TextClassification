package com.github.bkmbigo.textclassification.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.datetime.Instant

@Database(entities = [PredictionResult::class], version = 1, exportSchema = false)
@TypeConverters(PredictionResultDatabase.NotificationConverters::class)
abstract class PredictionResultDatabase: RoomDatabase() {
    abstract val dao: PredictionResultDao


    class NotificationConverters {
        @TypeConverter
        fun fromEpochSeconds(ms: Long): Instant {
            return Instant.fromEpochSeconds(ms)
        }

        @TypeConverter
        fun toEpochSeconds(instant: Instant): Long {
            return instant.epochSeconds
        }

    }
}