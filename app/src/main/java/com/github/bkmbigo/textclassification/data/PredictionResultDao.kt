package com.github.bkmbigo.textclassification.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PredictionResultDao {
    @Insert
    suspend fun insert(predictionResult: PredictionResult)

    @Delete
    suspend fun delete(predictionResult: PredictionResult)

    @Query("SELECT * FROM prediction_result")
    fun getAll(): LiveData<List<PredictionResult>>

    @Query("DELETE FROM prediction_result")
    suspend fun clear()
}