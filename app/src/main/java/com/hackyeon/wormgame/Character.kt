package com.hackyeon.wormgame

import kotlin.math.abs

class Character(
    x: Float,
    y: Float,
    size: Int,
    var speedX: Float = 0f,
    var speedY: Float = 0f,
    var degree: Float = 0f
): Coordinate(x, y, size){

    fun headRotate() {
        synchronized(this){
            degree = if (speedX >= 0 && speedY >= 0) {
                if (speedX == 0f) {
                    0f
                } else if (speedY == 0f) {
                    -90f
                } else if (speedX >= speedY) {
                    var tempDegree = speedY / speedX
                    -(((1 - tempDegree) * 45) + 45)
                } else {
                    var tempDegree = speedX / speedY
                    -(tempDegree * 45)
                }
            } else if (speedX > 0 && speedY < 0) {
                if (speedX >= -(speedY)) {
                    var tempDegree = -(speedY / speedX)
                    -((tempDegree * 45) + 90)
                } else {
                    var tempDegree = -(speedX / speedY)
                    -(((1 - tempDegree) * 45) + 135)
                }
            } else if (speedX < 0 && speedY > 0) {
                if (-(speedX) >= speedY) {
                    var tempDegree = -(speedY / speedX)
                    (((1 - tempDegree) * 45) + 45)
                } else {
                    var tempDegree = -(speedX / speedY)
                    (tempDegree * 45)
                }
            } else {
                if (speedX == 0f) {
                    180f
                } else if (speedY == 0f) {
                    90f
                } else if (-(speedX) >= -(speedY)) {
                    var tempDegree = speedY / speedX
                    ((tempDegree * 45) + 90)
                } else {
                    var tempDegree = speedX / speedY
                    (((1 - tempDegree) * 45) + 135)
                }
            }
        }
    }

    fun changeSpeed(eventX: Float, eventY: Float, speed: Int, canvas: GameCanvas) {
        if (x - size > 0 && y - size > 0 && x + size < canvas.width && y + size < canvas.height) {
            synchronized(this) {
                var intervalX = eventX - x
                var intervalY = eventY - y
                speedX = if (abs(intervalX) >= abs(intervalY)) intervalX * speed / abs(intervalX)
                else intervalX * speed / abs(intervalY)
                speedY = if (abs(intervalX) >= abs(intervalY)) intervalY * speed / abs(intervalX)
                else intervalY * speed / abs(intervalY)
            }
        }
    }

    fun moveHead(canvas: GameCanvas) {
            if (x + size > canvas.width || x - size < 0) speedX *= -1
            if (y + size > canvas.height || y - size < 0) speedY *= -1
            x += speedX
            y += speedY
    }

    fun moveBody(character: Character) {
            x = character.x
            y = character.y
    }

    fun speedUp(speed: Int) {
        synchronized(this) {
            if (abs(speedX) >= abs(speedY)) {
                if (speedX > 0) speedX += 1 else speedX -= 1
                speedY += speedY / speed
            } else {
                if (speedY > 0) speedY += 1 else speedY -= 1
                speedX += speedX / speed
            }
        }
    }

}