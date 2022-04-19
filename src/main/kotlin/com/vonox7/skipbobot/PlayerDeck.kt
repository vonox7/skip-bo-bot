package com.vonox7.skipbobot

import java.util.*

class PlayerDeck(game: Game) : CardSource {
    private val deck: Stack<Card> = Stack<Card>().apply { repeat(20) { this.push(game.drawCard()) } }
    fun getOpenCard(): Card? {
        return deck.firstOrNull()
    }

    override fun removeCard(card: Card, endOfTurn: Boolean) {
        require(card == getOpenCard())
        deck.pop()

        if (deck.isEmpty()) throw Game.PlayerHasWonException()
    }

    override fun toString(): String {
        return "${getOpenCard() ?: "----"} (+${(deck.size - 1).coerceAtLeast(0).toString().padStart(2)})"
    }
}