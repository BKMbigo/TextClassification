package com.github.bkmbigo.textclassification.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import org.tensorflow.lite.task.text.nlclassifier.NLClassifier

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @ActivityNLClassifier
    @ActivityScoped
    @Provides
    fun provideActivityClassifier(@ActivityContext context: Context): NLClassifier {
        return NLClassifier.createFromFile(context, "sentiment_analysis.tflite")
    }
}