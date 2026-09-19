package com.example.lovebot

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

object MessageScheduler {

    private const val MIN_DELAY_MIN = 60
    private const val MAX_DELAY_MIN = 180

    fun scheduleNext(context: Context) {
        val delayMin = Random.nextInt(MIN_DELAY_MIN, MAX_DELAY_MIN)
        val request = OneTimeWorkRequestBuilder<MessageWorker>()
            .setInitialDelay(delayMin.toLong(), TimeUnit.MINUTES)
            .addTag("love_message")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "love_message_next",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork("love_message_next")
    }
}
