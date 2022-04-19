package com.vonox7.skipbobot

interface CardSource {
    fun removeCard(card: Card, endOfTurn: Boolean = false)
}