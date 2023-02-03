package com.github.bkmbigo.textclassification.services

import android.Manifest
import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.bkmbigo.textclassification.R
import com.github.bkmbigo.textclassification.data.PredictionResult
import com.github.bkmbigo.textclassification.di.ServiceNLClassifier
import com.github.bkmbigo.textclassification.domain.DatabaseRepository
import com.github.bkmbigo.textclassification.utils.MyNotificationChannel
import com.github.bkmbigo.textclassification.utils.createNotificationChannel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.task.text.nlclassifier.NLClassifier
import javax.inject.Inject
import android.app.Notification as SysNotification

@AndroidEntryPoint
class NotificationListener : NotificationListenerService() {
    @Inject
    lateinit var databaseRepository: DatabaseRepository

    @ServiceNLClassifier
    @Inject
    lateinit var nlClassifier: NLClassifier

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        if (sbn?.notification?.flags == 24) {
            //Ignore the notification
            return;
        }


        CoroutineScope(context = SupervisorJob()).launch {
            sbn?.let { statusBarNotification ->
                val extras = statusBarNotification.notification.extras
                val title = extras.getString(SysNotification.EXTRA_TITLE, "")
                val message = extras.getString(SysNotification.EXTRA_TEXT, "")

                val results = withContext(Dispatchers.IO) {
                    nlClassifier.classify(message)
                }

                if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_DENIED) {
                    val builder = NotificationCompat.Builder(
                        applicationContext,
                        MyNotificationChannel.NOTIFICATION_LISTENER_CHANNEL.channelId
                    )
                        .setSmallIcon(
                            if (results[1].score > 0.5f) {
                                R.drawable.ic_positive_feedback
                            } else {
                                R.drawable.ic_negative_feedback
                            }
                        )
                        .setContentTitle(
                            if (results[1].score > 0.5f) {
                                getString(
                                    R.string.positive_confidence_value,
                                    results[1].score * 100f
                                )
                            } else {
                                getString(
                                    R.string.negative_confidence_value,
                                    results[0].score * 100f
                                )
                            }
                        )
                        .setContentText(message)
                        .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                        .apply { priority = NotificationCompat.PRIORITY_LOW }

                    createNotificationChannel(MyNotificationChannel.NOTIFICATION_LISTENER_CHANNEL)

                    with(NotificationManagerCompat.from(applicationContext)) {
                        notify(1, builder.build())
                    }
                }

                databaseRepository.insertPredictionResult(
                    PredictionResult(
                        source = PredictionResult.PredictionResultSource.NOTIFICATION,
                        message = message,
                        title = title,
                        positiveScore = results[1].score,
                        negativeScore = results[0].score
                    )
                )
            }
        }
    }
}