package com.vonox7.skipbobot

interface Pile {
    fun playCard(card: Card, cardSource: CardSource, endOfTurn: Boolean = false)

    fun canPlayCard(card: Card): Boolean
}