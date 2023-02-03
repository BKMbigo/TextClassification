package com.github.bkmbigo.textclassification.di

import android.content.Context
import androidx.room.Room
import com.github.bkmbigo.textclassification.data.PredictionResultDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNotificationDatabase(
        @ApplicationContext context: Context
    ): PredictionResultDatabase {
        return Room.databaseBuilder(
            context,
            PredictionResultDatabase::class.java,
            "NotificationDatabase.db"
        ).build()
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ActivityNLClassifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ServiceNLClassifier