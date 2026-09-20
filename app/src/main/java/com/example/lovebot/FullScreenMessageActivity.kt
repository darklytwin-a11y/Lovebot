package com.example.lovebot

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FullScreenMessageActivity : AppCompatActivity() {

    // Через сколько секунд закрыть сообщение (экран погаснет по системному таймауту)
    private val autoCloseMs = 15_000L

    private val handler = Handler(Looper.getMainLooper())
    private val closeRunnable = Runnable { finish() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        // ВАЖНО: убрали FLAG_KEEP_SCREEN_ON — теперь экран может гаснуть сам

        setContentView(R.layout.activity_message)

        val text = intent.getStringExtra("message") ?: "Ты классная ❤️"
        findViewById<TextView>(R.id.messageText).text = text

        findViewById<Button>(R.id.btnClose).setOnClickListener { finish() }

        // Автоматическое закрытие через 15 секунд
        handler.postDelayed(closeRunnable, autoCloseMs)
    }

    override fun onDestroy() {
        handler.removeCallbacks(closeRunnable)
        super.onDestroy()
    }
}
