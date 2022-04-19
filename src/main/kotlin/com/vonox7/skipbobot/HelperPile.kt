package com.vonox7.skipbobot

import java.util.*

class HelperPile : Pile, CardSource {
    private var stack: Stack<Card> = Stack()

    fun stackSize(): Int = stack.count()

    fun getTopStackCard(): Card? = if (stack.isEmpty()) null else stack.peek()

    override fun playCard(card: Card, cardSource: CardSource, endOfTurn: Boolean) {
        stack.push(card)
        cardSource.removeCard(card, endOfTurn)
    }

    override fun canPlayCard(card: Card): Boolean {
        return true // Always possible, no restrictions
    }

    override fun removeCard(card: Card, endOfTurn: Boolean) {
        require(card == getTopStackCard())
        stack.pop()
    }

    override fun toString(): String {
        return stack.toString().padStart(30)
    }
}