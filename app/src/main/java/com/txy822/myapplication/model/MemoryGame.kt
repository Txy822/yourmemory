package com.txy822.myapplication.model

import com.txy822.myapplication.util.DEFAULT_ICONS

class MemoryGame(private val boardSize: BoardSize) {
    val cards: List<MemoryCard>
    val numPairsFound =0

    init {
        val chosenImages= DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        val randomisedImages = (chosenImages+ chosenImages).shuffled()
        cards = randomisedImages.map { MemoryCard(it, false, false) }

    }
}