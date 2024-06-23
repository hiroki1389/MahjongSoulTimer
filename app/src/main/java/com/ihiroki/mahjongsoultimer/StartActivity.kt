package com.ihiroki.mahjongsoultimer

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.Button


class StartActivity : AppCompatActivity() {
    private lateinit var startButton: Button // 開始後に初期化しないとエラーが出て落ちる

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_start)

        // findViewByIdメソッドをここで呼び出す
        startButton = findViewById(R.id.startButton)
        startButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // StartActivityを終了することで、戻るボタンでStartActivityに戻らないようにします
        }
    }
}
