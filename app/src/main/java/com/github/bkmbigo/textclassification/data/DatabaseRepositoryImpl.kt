package com.github.bkmbigo.textclassification.data

import androidx.lifecycle.LiveData
import com.github.bkmbigo.textclassification.domain.DatabaseRepository
import javax.inject.Inject

class DatabaseRepositoryImpl @Inject constructor(
    private val database: PredictionResultDatabase
) : DatabaseRepository {

    override suspend fun insertPredictionResult(predictionResult: PredictionResult) =
        database.dao.insert(predictionResult)

    override suspend fun deletePredictionResult(predictionResult: PredictionResult) =
        database.dao.delete(predictionResult)

    override fun getAll(): LiveData<List<PredictionResult>> = database.dao.getAll()
    override suspend fun clearDatabase() = database.dao.clear()
}