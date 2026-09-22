package com.example.clubfocus

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val imgLogo = findViewById<ImageView>(R.id.imgLogo)

        val pulse = AnimationUtils.loadAnimation(this, R.anim.pulse)

        pulse.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                // Evita volver al Splash al presionar Atrás
                finish()
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })

        imgLogo.startAnimation(pulse)
    }
}
