package com.github.bkmbigo.textclassification.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prediction_result")
data class PredictionResult(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val source: PredictionResultSource,
    val message: String,
    val title: String? = null,
    val positiveScore: Float = 0.0f,
    val negativeScore: Float = 0.0f
) {

    /**
     * List of sources where the [PredictionResult] was obtained from
     */
    enum class PredictionResultSource {
        INPUT_TEXT,
        NOTIFICATION
    }
}
