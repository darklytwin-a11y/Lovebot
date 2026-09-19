package com.example.lovebot

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.Calendar

class MessageWorker(
    ctx: Context,
    params: WorkerParameters
) : Worker(ctx, params) {

    // ===== Утро =====
    private val morning = listOf(
        "Доброе утро ☀️ Ты сегодня прекрасна",
        "Просыпайся, солнце. Я тебя люблю",
        "Пусть этот день будет добрым к тебе",
        "Утро с мыслью о тебе — уже хорошее",
        "Кофе тебе и моя улыбка ☕",
        "Юля, доброе утро! Ты моё солнышко ☀️"
    )

    // ===== День =====
    private val day = listOf(
        "Помни, что Женя тебя любит ❤️",
        "У тебя красивая улыбка 🙂",
        "Ты сегодня классно выглядишь",
        "Ты справляешься лучше, чем тебе кажется",
        "Я думаю о тебе прямо сейчас",
        "Ты умница. Серьёзно",
        "Обними себя от меня 🤗",
        "Ты делаешь этот мир теплее",
        "Хочу увидеть твою улыбку",
        "Родная, скучаю по тебе 💕",
        "Милая, улыбнись — и мне станет теплее",
        "Юля + Женя = Любовь ❤️"
    )

    // ===== Вечер =====
    private val evening = listOf(
        "Как прошёл твой день? Я горжусь тобой 🌇",
        "Ты сегодня много сделала. Отдохни",
        "Вечер — время для тебя. Ты заслужила",
        "Скоро увидимся, и я обниму тебя",
        "Ты — моё любимое время суток",
        "Родная, скучаю по тебе 💕",
        "Милая, улыбнись — и мне станет теплее"
    )

    // ===== Ночь =====
    private val night = listOf(
        "Сладких снов, моя хорошая 🌙",
        "Пусть тебе приснюсь я",
        "Засыпай с мыслью, что ты любима",
        "Обнимаю тебя перед сном",
        "До завтра, солнце ✨",
        "Юля, пусть тебе приснятся самые тёплые сны 🌙"
    )

    override fun doWork(): Result {
        val ctx = applicationContext

        if (isQuietHours(ctx)) {
            MessageScheduler.scheduleNext(ctx)
            return Result.success()
        }

        showMessage(ctx)
        MessageScheduler.scheduleNext(ctx)
        return Result.success()
    }

    private fun isQuietHours(ctx: Context): Boolean {
        val prefs = ctx.getSharedPreferences("settings", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("quiet_enabled", true)) return false

        val startHour = prefs.getInt("quiet_start", 23)
        val endHour = prefs.getInt("quiet_end", 8)
        val now = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        return if (startHour > endHour) {
            now >= startHour || now < endHour
        } else {
            now >= startHour && now < endHour
        }
    }

    private fun pickMessage(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> morning.random()
            in 12..17 -> day.random()
            in 18..22 -> evening.random()
            else -> night.random()
        }
    }

    private fun showMessage(ctx: Context) {
        val text = pickMessage()
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        val title = when (hour) {
            in 5..11 -> "Доброе утро ☀️"
            in 12..17 -> "Тёплое сообщение"
            in 18..22 -> "Хорошего вечера 🌇"
            else -> "Спокойной ночи 🌙"
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Тёплые слова",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { enableVibration(true) }
            ctx.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }

        val fullScreenIntent = Intent(ctx, FullScreenMessageActivity::class.java)
            .putExtra("message", text)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val fullScreenPending = PendingIntent.getActivity(
            ctx, 0, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_heart)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setContentIntent(fullScreenPending)
            .setFullScreenIntent(fullScreenPending, true)
            .build()

        try {
            NotificationManagerCompat.from(ctx).notify(NOTIF_ID, notification)
        } catch (_: SecurityException) {}
    }

    companion object {
        const val CHANNEL_ID = "love_channel"
        const val NOTIF_ID = 1001
    }
}
