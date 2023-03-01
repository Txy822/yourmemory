package com.txy822.memorygame.model

enum class BoardSize(val numCards: Int) {
    EASY(8),
    MEDIUM(18),
    HARD(28),
    TOUGH(40);


    companion object {
        fun getByValue(value: Int) = values().first { it.numCards == value }
    }

    fun getWidth(): Int {
        return when (this) {
            EASY -> 2
            MEDIUM -> 3
            HARD -> 4
            TOUGH -> 5
        }
    }

    fun getHeight(): Int {
        return numCards / getWidth()
    }

    fun getNumPairs(): Int {
        return numCards / 2
    }
}