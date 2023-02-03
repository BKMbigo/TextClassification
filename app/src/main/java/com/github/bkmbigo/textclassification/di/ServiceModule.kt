package com.github.bkmbigo.textclassification.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import org.tensorflow.lite.task.text.nlclassifier.NLClassifier

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceNLClassifier
    @Provides
    @ServiceScoped
    fun provideServiceClassifier(@ApplicationContext context: Context): NLClassifier {
        return NLClassifier.createFromFile(context, "sentiment_analysis.tflite")
    }
}