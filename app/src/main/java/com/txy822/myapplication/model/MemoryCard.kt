package com.txy822.myapplication.model

data class MemoryCard(
    val identifier: Int,
    var isFaceUp : Boolean  = false,
    var isMatched: Boolean =false,
){

}