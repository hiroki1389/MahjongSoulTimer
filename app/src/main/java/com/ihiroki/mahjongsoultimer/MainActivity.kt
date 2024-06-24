package com.ihiroki.mahjongsoultimer

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.ceil
import android.graphics.Color

class MainActivity : AppCompatActivity() {

    // タイマーの時間
    private var mainTime = 20.0f
    private var subTime = 5.0f

    // タイマーの時間を格納する変数
    private var player1Time = mainTime
    private var player2Time = mainTime
    private var tempTime = subTime

    // 実際に動作するタイマー
    private lateinit var turnCountDownTimer: CountDownTimer
    private var playerCountDownTimer: CountDownTimer? = null

    private var currentPlayer = 1 // どっちのプレイヤーのターンか
    private var gamePaused = false // ゲームが一時停止しているか
    private var isEnded = false // ゲームが終了しているか

    // ボタンとかテキストとか（これらはonCreateで初期化しないとアプリが落ちる）
    private lateinit var player1TimerView: TextView
    private lateinit var player2TimerView: TextView
    private lateinit var turnButton: Button
    private lateinit var pauseButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // ボタンとかテキストとかの初期化
        player1TimerView = findViewById(R.id.player1Timer)
        player2TimerView = findViewById(R.id.player2Timer)
        turnButton = findViewById(R.id.turnButton)
        pauseButton = findViewById(R.id.pauseButton)

        // 初期表示
        player1TimerView.text = "Player 1 Timer\n${player1Time.toInt()} + ${ceil(subTime).toInt()}"
        player2TimerView.text = "Player 2 Timer\n${player2Time.toInt()} + ${ceil(subTime).toInt()}"

        // タイマーの開始
        startTurnTimer()

        // ターン交代（リスタート）ボタン押された時
        turnButton.setOnClickListener {
            if (isEnded) restartGame()
            else switchTurn()
        }

        // 一時停止（再開）ボタン押された時
        pauseButton.setOnClickListener {
            // ゲーム中のみ反応
            if (!isEnded) {
                if (gamePaused) resumeGame()
                else pauseGame()
            }
        }
    }

    // タイマーの開始
    private fun startTurnTimer() {
        // 一時停止してる時は何もしない
        if (!gamePaused) {
            // タイマーの作成
            turnCountDownTimer = object : CountDownTimer((tempTime * 1000).toLong(), 100) {
                // countDownInterval毎に呼び出される関数
                override fun onTick(millisUntilFinished: Long) {
                    if (tempTime <= 0.1) return // サブタイマーがほぼ終了なら表示更新しない
                    tempTime = millisUntilFinished / 1000.0f // 表示用変数の更新

                    // タイマーの表示を更新する
                    if (currentPlayer == 1) {
                        if(player1Time + tempTime < subTime) player1TimerView.setTextColor(Color.RED) // サブタイマー以下なら警告色（赤）にする
                        player1TimerView.text = "Player 1 Timer\n${ceil(player1Time).toInt()} + ${ceil(tempTime).toInt()}"
                        player2TimerView.text = "Player 2 Timer\n${ceil(player2Time).toInt()} + ${ceil(subTime).toInt()}"
                    } else {
                        if(player2Time + tempTime < subTime) player2TimerView.setTextColor(Color.RED) // サブタイマー以下なら警告色（赤）にする
                        player1TimerView.text = "Player 1 Timer\n${ceil(player1Time).toInt()} + ${ceil(subTime).toInt()}"
                        player2TimerView.text = "Player 2 Timer\n${ceil(player2Time).toInt()} + ${ceil(tempTime).toInt()}"
                    }
                }

                // タイマーが終了したら呼び出される
                override fun onFinish() {
                    startPlayerTimer() // プレイヤー固有のタイマー開始
                }
            }
            turnCountDownTimer.start()
        }
    }

    // プレイヤー固有のタイマー開始
    private fun startPlayerTimer() {
        // 一時停止してる時は何もしない
        if (!gamePaused) {
            val playerTime = if (currentPlayer == 1) player1Time else player2Time // 現在プレイヤーのタイマーを変数を参照する

            // タイマーの作成
            playerCountDownTimer = object : CountDownTimer((playerTime * 1000).toLong(), 100) {
                // countDownInterval毎に呼び出される関数
                override fun onTick(millisUntilFinished: Long) {
                    val remainingTime = millisUntilFinished / 1000.0f
                    // タイマーの表示を更新する
                    if (currentPlayer == 1) {
                        if(player1Time < subTime) player1TimerView.setTextColor(Color.RED) // サブタイマー以下なら警告色（赤）にする
                        player1Time = remainingTime // プレイヤー固有のタイマーの更新
                        player1TimerView.text = "Player 1 Timer\n${ceil(player1Time).toInt()}"
                    } else {
                        if(player2Time < subTime) player2TimerView.setTextColor(Color.RED) // サブタイマー以下なら警告色（赤）にする
                        player2Time = remainingTime
                        player2TimerView.text = "Player 2 Timer\n${ceil(player2Time).toInt()}"
                    }
                }

                // タイマーが終了したら呼び出される
                override fun onFinish() {
                    isEnded = true
                    if (currentPlayer == 1) player1TimerView.text = "Player 1 Lose"
                    else player2TimerView.text = "Player 2 Lose"

                    turnButton.text = "RESTART"

                    // タイマーストップ
                    turnCountDownTimer.cancel()
                    playerCountDownTimer?.cancel()
                }
            }
            playerCountDownTimer?.start()
        }
    }

    // ターン交代ボタン押された時
    private fun switchTurn() {
        // 一時停止してる時は何もしない
        if (!gamePaused) {
            // 今動いてるタイマーを止める
            turnCountDownTimer.cancel()
            playerCountDownTimer?.cancel()

            // プレイヤーの残タイマーを小数点以下切り上げる
            if (currentPlayer == 1) {
                player1Time = ceil(player1Time)
                player1TimerView.setTextColor(Color.BLACK) // 黒色に戻す
            }
            else {
                player2Time = ceil(player2Time)
                player2TimerView.setTextColor(Color.BLACK) // 黒色に戻す
            }

            currentPlayer = if (currentPlayer == 1) 2 else 1 // ターン交代
            tempTime = subTime // サブタイマーを初期化
            startTurnTimer() // ターン開始
        }
    }

    // リスタートボタンが押された時
    private fun restartGame() {
        // 変数の初期化
        player1Time = mainTime
        player2Time = mainTime
        tempTime = subTime
        currentPlayer = 1
        gamePaused = false
        isEnded = false

        // 黒色に戻す
        player1TimerView.setTextColor(Color.BLACK)
        player2TimerView.setTextColor(Color.BLACK)

        startTurnTimer()
    }

    // 一時停止ボタン押された時
    private fun pauseGame() {
        gamePaused = true
        pauseButton.text = "RESUME"

        // 今動いてるタイマーを止める
        turnCountDownTimer.cancel()
        playerCountDownTimer?.cancel()
    }

    // 再開ボタン押された時
    private fun resumeGame() {
        gamePaused = false
        pauseButton.text = "PAUSE"
        startTurnTimer() // タイマー開始する（前の変数初期化してないから前から継続される）
    }
}
