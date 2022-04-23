package com.vonox7.skipbobot.strategies.vonox7

import com.vonox7.skipbobot.*

// Basic strategy with useful helper functions
open class BaseStrategy : Strategy() {

    // start + end exclusive.
    // end == start -> 11 numbers.
    // end == start + 1 -> 0 numbers.
    fun cardsBetween(start: Int, end: Int): List<Int> {
        return if (start < end) {
            (start + 1 until end).map { it }
        } else {
            (start + 1..12).map { it } + (1 until end).map { it }
        }
    }

    fun DiscardPile.playCardOptimized(card: Card, cardSource: CardSource, gameState: StrategyGameState) {
        val openDeckCard = gameState.playerDeck.getOpenCard()
        if (openDeckCard is JokerCard) {
            require(card == openDeckCard) // If this fails, we didn't play the deck card immediately, something is wrong
            this.playCard(card, cardSource)
        } else if (card is NumberCard) {
            this.playCard(card, cardSource)
        } else if (card is JokerCard) {
            // If we play a joker, play it there were it could help the most for our openDeckCard (= nearest pile value)
            gameState.discardPiles
                .minByOrNull { cardsBetween(it.topCardValue, (openDeckCard as NumberCard).value).count() }!!
                .playCard(card, cardSource)
        }
    }

    // Play as many openDeckCards onto discardPile as possible
    fun playOpenDeckCards(gameState: StrategyGameState): Boolean {
        var didSomething = false
        var playedCard: Boolean
        do {
            playedCard = false
            gameState.discardPiles.forEach { discardPile ->
                if (discardPile.canPlayCard(gameState.playerDeck.getOpenCard()!!)) {
                    discardPile.playCardOptimized(gameState.playerDeck.getOpenCard()!!, gameState.playerDeck, gameState)
                    didSomething = true
                    playedCard = true
                }
            }
        } while (playedCard)

        return didSomething
    }

    override fun play(gameState: StrategyGameState) {
        // Strategy: Try to play as many deck cards as possible
        var didSomething: Boolean
        do {
            didSomething = false
            if (playOpenDeckCards(gameState)) {
                didSomething = true
            }
        } while (didSomething)
    }

    override fun getResult(gameState: StrategyGameState): PlayResult {
        // Pick first card to discard, favour non-joker cards
        return PlayResult(
            helperPile = gameState.playerHelperPiles.minByOrNull { it.stackSize() }!!,
            handCard = gameState.playerHandCards.cards.let { cards ->
                cards.firstOrNull { it is NumberCard } ?: cards.first()
            }
        )
    }
}