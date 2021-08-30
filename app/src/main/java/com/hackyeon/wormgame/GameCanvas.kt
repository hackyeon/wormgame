package com.hackyeon.wormgame

import android.content.Context
import android.graphics.*
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale

class GameCanvas(context: Context) : View(context) {
    private var paint = Paint()
    private var bitShoes: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.shoes)
    private var bitItem: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.item)
    private var bitPlayerCharacter: Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.player_character)
    private var bitEnemyCharacter: Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.enemy_character)
    var playerList = mutableListOf<Character>()
    var enemyListOfList = mutableListOf<MutableList<Character>>()
    var itemList = mutableListOf<Item>()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setBackgroundResource(R.drawable.background_space)

        // 아이템 그리기
        synchronized(itemList) {
            for (i in itemList) {
                drawItem(canvas, i)
            }
        }

        // 적 그리기
        paint.color = ContextCompat.getColor(context, R.color.enemy_color)
        for (i in enemyListOfList.indices) {
            synchronized(enemyListOfList){
                for (j in enemyListOfList[i].size - 1 downTo 0) {
                    synchronized(enemyListOfList[i]) {
                        if (j == 0) {
                            canvas?.drawBitmap(
                                bitEnemyCharacter.rotateBitmap(enemyListOfList[i][j].degree).scale(
                                    bitEnemyCharacter.width, bitEnemyCharacter.height),
                                Rect(0, 0, bitEnemyCharacter.width, bitEnemyCharacter.height),
                                Rect(
                                    ((enemyListOfList[i][j].x - enemyListOfList[i][j].size).toInt()),
                                    ((enemyListOfList[i][j].y - enemyListOfList[i][j].size).toInt()),
                                    ((enemyListOfList[i][j].x + enemyListOfList[i][j].size).toInt()),
                                    ((enemyListOfList[i][j].y + enemyListOfList[i][j].size).toInt()),
                                ),
                                paint
                            )
                        } else {
                            canvas?.drawOval(
                                (enemyListOfList[i][j].x - enemyListOfList[i][j].size),
                                (enemyListOfList[i][j].y - enemyListOfList[i][j].size),
                                (enemyListOfList[i][j].x + enemyListOfList[i][j].size),
                                (enemyListOfList[i][j].y + enemyListOfList[i][j].size),
                                paint
                            )
                        }
                    }
                }
            }
        }

        // 플레이어 그리기
        paint.color = ContextCompat.getColor(context, R.color.player_color)
        for (i in playerList.size - 1 downTo 0) {
            synchronized(playerList) {
                if (i == 0) {
                    canvas?.drawBitmap(
                        bitPlayerCharacter.rotateBitmap(playerList[i].degree)
                            .scale(bitPlayerCharacter.width, bitPlayerCharacter.height),
                        Rect(0, 0, bitPlayerCharacter.width, bitPlayerCharacter.height),
                        Rect(
                            (playerList[i].x - playerList[i].size).toInt(),
                            (playerList[i].y - playerList[i].size).toInt(),
                            (playerList[i].x + playerList[i].size).toInt(),
                            (playerList[i].y + playerList[i].size).toInt()
                        ),
                        paint
                    )
                } else {
                    canvas?.drawOval(
                        playerList[i].x - playerList[i].size,
                        playerList[i].y - playerList[i].size,
                        playerList[i].x + playerList[i].size,
                        playerList[i].y + playerList[i].size,
                        paint
                    )
                }
            }
        }
    }

    private fun drawItem(canvas: Canvas?, item: Item) {
        var bit = if (item.type == 1) bitItem else bitShoes
        canvas?.drawBitmap(
            bit,
            Rect(0, 0, bit.width, bit.height),
            Rect(
                ((item.x - item.size).toInt()),
                ((item.y - item.size).toInt()),
                ((item.x + item.size).toInt()),
                ((item.y + item.size).toInt())
            ),
            paint
        )
    }

    private fun Bitmap.rotateBitmap(degree: Float): Bitmap {
        var matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

}