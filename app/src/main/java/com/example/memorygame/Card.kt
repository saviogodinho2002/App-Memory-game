package com.example.memorygame

import androidx.annotation.DrawableRes

data class Card(
    val id:Int,
    @DrawableRes var imgCard:Int
){
    var foundPair:Boolean = false;
    var upTurned:Boolean = false;
}
