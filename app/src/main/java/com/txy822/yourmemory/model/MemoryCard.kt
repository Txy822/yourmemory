package com.txy822.yourmemory.model

data class MemoryCard(
    val identifier: Int,
    val imageUrl: String? = null,
    var isFaceUp : Boolean  = false,
    var isMatched: Boolean =false,
){

}