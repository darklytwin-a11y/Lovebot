package com.example.lovebot

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.materialswitch.MaterialSwitch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1
            )
        }

        val prefs = getSharedPreferences("settings", MODE_PRIVATE)

        val toggle = findViewById<MaterialSwitch>(R.id.toggle)
        toggle.isChecked = prefs.getBoolean("enabled", false)
        toggle.setOnCheckedChangeListener { _, isOn ->
            prefs.edit().putBoolean("enabled", isOn).apply()
            if (isOn) {
                MessageScheduler.scheduleNext(this)
                Toast.makeText(this, "Включено ❤️", Toast.LENGTH_SHORT).show()
            } else {
                MessageScheduler.cancel(this)
                Toast.makeText(this, "Выключено", Toast.LENGTH_SHORT).show()
            }
        }

        val quietToggle = findViewById<MaterialSwitch>(R.id.quietToggle)
        val quietStart = findViewById<NumberPicker>(R.id.quietStart)
        val quietEnd = findViewById<NumberPicker>(R.id.quietEnd)

        quietStart.minValue = 0
        quietStart.maxValue = 23
        quietEnd.minValue = 0
        quietEnd.maxValue = 23
        quietStart.value = prefs.getInt("quiet_start", 23)
        quietEnd.value = prefs.getInt("quiet_end", 8)

        quietToggle.isChecked = prefs.getBoolean("quiet_enabled", true)
        quietToggle.setOnCheckedChangeListener { _, isOn ->
            prefs.edit().putBoolean("quiet_enabled", isOn).apply()
        }

        quietStart.setOnValueChangedListener { _, _, newVal ->
            prefs.edit().putInt("quiet_start", newVal).apply()
        }
        quietEnd.setOnValueChangedListener { _, _, newVal ->
            prefs.edit().putInt("quiet_end", newVal).apply()
        }

        findViewById<Button>(R.id.btnTest).setOnClickListener {
            startActivity(
                Intent(this, FullScreenMessageActivity::class.java)
                    .putExtra("message", "Тестовое сообщение ❤️")
            )
        }
    }
}
