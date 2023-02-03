package com.github.bkmbigo.textclassification.domain

import androidx.lifecycle.LiveData
import com.github.bkmbigo.textclassification.data.PredictionResult

interface DatabaseRepository {
    suspend fun insertPredictionResult(predictionResult: PredictionResult)
    suspend fun deletePredictionResult(predictionResult: PredictionResult)
    fun getAll(): LiveData<List<PredictionResult>>
    suspend fun clearDatabase()
}