package com.ihiroki.mahjongsoultimer

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.ceil

class MainActivity : AppCompatActivity() {

    private var player1Time = 20.0f
    private var player2Time = 20.0f
    private var turnTime = 5.0f

    private var currentPlayer = 1
    private var gamePaused = false

    private lateinit var player1TimerView: TextView
    private lateinit var player2TimerView: TextView
    private lateinit var turnButton: Button
    private lateinit var pauseButton: Button

    private lateinit var turnCountDownTimer: CountDownTimer
    private var playerCountDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        player1TimerView = findViewById(R.id.player1Timer)
        player2TimerView = findViewById(R.id.player2Timer)
        turnButton = findViewById(R.id.turnButton)
        pauseButton = findViewById(R.id.pauseButton)

        // 初期表示
        player1TimerView.text = "Player 1 Timer\n${player1Time.toInt()}"
        player2TimerView.text = "Player 2 Timer\n${player2Time.toInt()}"

        startTurnTimer()

        turnButton.setOnClickListener {
            switchTurn()
        }

        pauseButton.setOnClickListener {
            if (gamePaused) {
                resumeGame()
            } else {
                pauseGame()
            }
        }
    }

    private fun startTurnTimer() {
        turnCountDownTimer = object : CountDownTimer((turnTime * 1000).toLong(), 100) {
            override fun onTick(millisUntilFinished: Long) {
                if (!gamePaused) {
                    turnTime = millisUntilFinished / 1000.0f
                    updateTurnTimerView(turnTime)
                }
            }

            override fun onFinish() {
                if (!gamePaused) {
                    turnTime = 0.0f
                    updateTurnTimerView(turnTime)
                    startPlayerTimer()
                }
            }
        }
        turnCountDownTimer.start()
    }

    private fun startPlayerTimer() {
        if (!gamePaused) {
            val playerTime = if (currentPlayer == 1) player1Time else player2Time

            playerCountDownTimer = object : CountDownTimer((playerTime * 1000).toLong(), 100) {
                override fun onTick(millisUntilFinished: Long) {
                    if (!gamePaused) {
                        val remainingTime = millisUntilFinished / 1000.0f
                        if (currentPlayer == 1) {
                            player1Time = remainingTime
                            player1TimerView.text = "Player 1 Timer\n${ceil(player1Time).toInt()}"
                        } else {
                            player2Time = remainingTime
                            player2TimerView.text = "Player 2 Timer\n${ceil(player2Time).toInt()}"
                        }
                    }
                }

                override fun onFinish() {
                    if (!gamePaused) {
                        if (currentPlayer == 1) {
                            player1Time = 0.0f
                            player1TimerView.text = "Player 1 Timer\n${ceil(player1Time).toInt()}"
                            // Player 1 loses
                        } else {
                            player2Time = 0.0f
                            player2TimerView.text = "Player 2 Timer\n${ceil(player2Time).toInt()}"
                            // Player 2 loses
                        }
                    }
                }
            }
            playerCountDownTimer?.start()
        }
    }

    private fun switchTurn() {
        if (!gamePaused) {
            turnCountDownTimer.cancel()
            playerCountDownTimer?.cancel()
            currentPlayer = if (currentPlayer == 1) 2 else 1
            turnTime = 5.0f
            if (currentPlayer == 1) {
                player2Time = ceil(player2Time).toFloat()
                player2TimerView.text = "Player 2 Timer\n${ceil(player2Time).toInt()}"
            } else {
                player1Time = ceil(player1Time).toFloat()
                player1TimerView.text = "Player 1 Timer\n${ceil(player1Time).toInt()}"
            }
            startTurnTimer()
        }
    }

    private fun updateTurnTimerView(time: Float) {
        if (currentPlayer == 1) {
            player1TimerView.text = "Player 1 Timer\n${ceil(player1Time).toInt()} + ${ceil(time).toInt()}"
            player2TimerView.text = "Player 2 Timer\n${ceil(player2Time).toInt()} + 5"
        } else {
            player1TimerView.text = "Player 1 Timer\n${ceil(player1Time).toInt()} + 5"
            player2TimerView.text = "Player 2 Timer\n${ceil(player2Time).toInt()} + ${ceil(time).toInt()}"
        }
    }

    private fun pauseGame() {
        gamePaused = true
        turnCountDownTimer.cancel()
        playerCountDownTimer?.cancel()
        pauseButton.text = "Restart"
    }

    private fun resumeGame() {
        gamePaused = false
        startTurnTimer()
        pauseButton.text = "Pause"
    }
}
