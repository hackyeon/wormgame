package com.hackyeon.wormgame

class Item(
    x: Float,
    y: Float,
    var type: Int, // 1: 길이증가 // 2: 이속증가
    size: Int = 40
) : Coordinate(x, y, size)