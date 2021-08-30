package com.hackyeon.wormgame

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.DialogFragment
import kotlin.math.abs
import kotlin.random.Random

class MainActivity : AppCompatActivity(),
    EndGameDialogFragment.NoticeDialogListener {
    private lateinit var gameCanvas: GameCanvas
    private var isPlay = false
    private var speed = 20
    private lateinit var player: MutableList<Character>
    private lateinit var enemy: MutableList<MutableList<Character>>
    private lateinit var item: MutableList<Item>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameCanvas = GameCanvas(this)
        setContentView(gameCanvas)

        initView()
        handler.sendEmptyMessage(0)
        gameCanvas.setOnTouchListener {  _: View, event: MotionEvent ->
            player[0].changeSpeed(event.x, event.y, speed, gameCanvas)
            true
        }
    }

    private fun initView() {
        player = gameCanvas.playerList
        enemy = gameCanvas.enemyListOfList
        item = gameCanvas.itemList
    }

    override fun onBackPressed() {
        isPlay = false
        EndGameDialogFragment().show(supportFragmentManager, "endMessage")
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        finish()
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        player.clear()
        enemy.clear()
        item.clear()
        handler.sendEmptyMessage(0)

        dialog.dismiss()
    }

    private var handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 0) {
                if (gameCanvas.width > 0) {
                    player.add(
                        Character(
                            (gameCanvas.width / 2).toFloat(),
                            (gameCanvas.height / 2).toFloat(),
                            50,
                            20f,
                            20f,
                            -45f
                        )
                    )
                    isPlay = true
                    PlayThread().start()
                    MoveWorm().start()
                    CreateItemThread().start()
                    CrashThread().start()
                    CreateEnemyThread().start()
                } else {
                    sendEmptyMessageDelayed(0, 200)
                }
            }
        }
    }

    inner class MoveWorm : Thread() {
        override fun run() {
            super.run()
            while (isPlay) {
                // 플레이어 이동 시작
                var i = player.size - 1
                while (i > 0) {
                    synchronized(player) {
                        player[i].moveBody(player[i - 1])
                    }
                    i--
                }
                synchronized(player) {
                    player[0].moveHead(gameCanvas)
                }
                // 플레이어 이동 끝

                // 적 이동 시작
                for (i in enemy) {
                    var cnt = i.size - 1
                    while (cnt > 0) {
                        synchronized(i) {
                            i[cnt].moveBody(i[cnt - 1])
                        }
                        cnt--
                    }
                    synchronized(i) {
                        i[0].moveHead(gameCanvas)
                    }
                }
                // 적 이동 끝

                // 캐릭터 머리 각도
                player[0].headRotate()
                for (i in enemy) {
                    i[0].headRotate()
                }
                sleep(50)
            }
        }
    }

    inner class PlayThread : Thread() {
        override fun run() {
            super.run()
            while (isPlay) {
                runOnUiThread {
                    gameCanvas.invalidate()
                }
                sleep(50)
            }
        }
    }

    inner class CreateEnemyThread : Thread() {
        override fun run() {
            super.run()
            var count = 0
            while (isPlay) {
                if (count >= 1000) {
                    var tempEnemyList = mutableListOf<Character>()
                    var x = Random.nextInt(50, gameCanvas.width - 50).toFloat()
                    var y = Random.nextInt(50, gameCanvas.height - 50).toFloat()
                    var size = Random.nextInt(25, 50)
                    var speedX = Random.nextInt(5, 20).toFloat()
                    var speedY = Random.nextInt(5, 20).toFloat()

                    tempEnemyList.add(Character(x, y, size, speedX, speedY, 0f))
                    var maxNum = Random.nextInt(10, 100)
                    for (i in 1 until maxNum) {
                        tempEnemyList.add(
                            Character(
                                x - (speedX * i),
                                y - (speedY * i),
                                size
                            )
                        )
                    }
                    enemy.add(tempEnemyList)
                    count = 0
                }
                count++
                // 생성 주기 10초~20초
                sleep(Random.nextInt(10, 20).toLong())
            }
        }
    }

    inner class CreateItemThread : Thread() {
        override fun run() {
            super.run()
            var count = 0
            while (isPlay) {
                if (count >= 100) {
                    var x = Random.nextInt(10, gameCanvas.width - 10).toFloat()
                    var y = Random.nextInt(10, gameCanvas.height - 10).toFloat()
                    var tempType = Random.nextInt(1, 15) // 1~10 길이 증가 // 11~15 이속 증가
                    var type = if (tempType in 1..10) 1 else 2
                    synchronized(item) {
                        item.add(Item(x, y, type))
                    }
                    count = 0
                }
                count++
                sleep(Random.nextInt(10, 40).toLong())
            }
        }
    }

    inner class CrashThread : Thread() {
        override fun run() {
            super.run()
            while (isPlay) {
                // 아이템 획득 시작
                var cnt = 0
                while (cnt < item.size) {
                    synchronized(item) {
                        var itemRect = Rect(
                            (item[cnt].x - (item[cnt].size / 2)).toInt(),
                            (item[cnt].y - (item[cnt].size / 2)).toInt(),
                            (item[cnt].x + (item[cnt].size / 2)).toInt(),
                            (item[cnt].y + (item[cnt].size / 2)).toInt()
                        )
                        var playerRect = Rect(
                            (player[0].x - (player[0].size / 2)).toInt(),
                            (player[0].y - (player[0].size / 2)).toInt(),
                            (player[0].x + (player[0].size / 2)).toInt(),
                            (player[0].y + (player[0].size / 2)).toInt()
                        )

                        if (playerRect.intersect(itemRect)) {
                            if (item[cnt].type == 1) {
                                // type 1: 길이 증가
                                item.removeAt(cnt)
                                synchronized(player) {
                                    player.add(
                                        Character(
                                            player[player.lastIndex].x,
                                            player[player.lastIndex].y,
                                            player[player.lastIndex].size
                                        )
                                    )
                                }
                            } else if (item[cnt].type == 2) {
                                // type 2: 이속 증가
                                item.removeAt(cnt)
                                if (speed <= 30) {
                                    player[0].speedUp(speed)
                                    speed++
                                }
                            }
                            cnt--
                        }
                        cnt++
                    }
                }
                // 아이템 획득 끝

                // 플레이어->적 충돌 시작
                var i = 0
                while (i < enemy.size) {
                    cnt = 0
                    while (cnt < enemy[i].size) {
                        var enemyRect = Rect(
                            (enemy[i][cnt].x - (enemy[i][cnt].size / 2)).toInt(),
                            (enemy[i][cnt].y - (enemy[i][cnt].size / 2)).toInt(),
                            (enemy[i][cnt].x + (enemy[i][cnt].size / 2)).toInt(),
                            (enemy[i][cnt].y + (enemy[i][cnt].size / 2)).toInt()
                        )
                        var playerRect = Rect(
                            (player[0].x - (player[0].size / 2)).toInt(),
                            (player[0].y - (player[0].size / 2)).toInt(),
                            (player[0].x + (player[0].size / 2)).toInt(),
                            (player[0].y + (player[0].size / 2)).toInt()
                        )

                        if (playerRect.intersect(enemyRect)) {
                            isPlay = false
                            EndGameDialogFragment().show(supportFragmentManager, "endMessage")
                            break
                        }
                        cnt++
                    }
                    i++
                }
                // 플레이어->적 충돌 끝

                // 적->플레이어 충돌 시작
                i = 0
                while (i < enemy.size) {
                    cnt = 0
                    while (cnt < player.size) {
                        var enemyRect = Rect(
                            (enemy[i][0].x - (enemy[i][0].size / 2)).toInt(),
                            (enemy[i][0].y - (enemy[i][0].size / 2)).toInt(),
                            (enemy[i][0].x + (enemy[i][0].size / 2)).toInt(),
                            (enemy[i][0].y + (enemy[i][0].size / 2)).toInt()
                        )
                        var playerRect = Rect(
                            (player[cnt].x - (player[cnt].size / 2)).toInt(),
                            (player[cnt].y - (player[cnt].size / 2)).toInt(),
                            (player[cnt].x + (player[cnt].size / 2)).toInt(),
                            (player[cnt].y + (player[cnt].size / 2)).toInt()
                        )

                        if (playerRect.intersect(enemyRect)) {
                            for (en in enemy[i]) {
                                synchronized(item) {
                                    item.add(Item(en.x, en.y, 1))
                                }
                            }
                            synchronized(enemy) {
                                enemy.removeAt(i)
                            }
                            i--
                            break
                        }
                        cnt++
                    }
                    i++
                }
                // 적->플레이어 충돌 끝
                sleep(1)
            }
        }
    }
}