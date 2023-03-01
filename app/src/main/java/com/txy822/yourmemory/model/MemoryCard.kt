package com.txy822.yourmemory.model

data class MemoryCard(
    val identifier: Int,
    var isFaceUp : Boolean  = false,
    var isMatched: Boolean =false,
){

}