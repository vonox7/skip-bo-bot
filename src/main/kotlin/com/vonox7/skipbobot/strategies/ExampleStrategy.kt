package com.vonox7.skipbobot.strategies

import com.vonox7.skipbobot.NumberCard
import com.vonox7.skipbobot.Strategy

// Basic example strategy to explain how a strategy can work
open class ExampleStrategy : Strategy() {

    private fun playDiscardPile(gameState: StrategyGameState) {
        gameState.discardPiles.forEach { discardPile ->
            if (discardPile.canPlayCard(gameState.playerDeck.getOpenCard()!!)) {
                discardPile.playCard(gameState.playerDeck.getOpenCard()!!, gameState.playerDeck)
                return
            }
        }
    }

    private fun playHandCard(gameState: StrategyGameState, playAlsoJoker: Boolean) {
        // Play non-joker hand cards
        gameState.playerHandCards.cards
            .filter { card -> if (playAlsoJoker) true else card is NumberCard }
            .forEach { card ->
                gameState.discardPiles.forEach { discardPile ->
                    if (discardPile.canPlayCard(card)) {
                        discardPile.playCard(card, gameState.playerHandCards)
                        return
                    }
                }
            }
    }

    private fun playHelperCard(gameState: StrategyGameState) {
        gameState.playerHelperPiles.forEach { helperPile ->
            val helperCard = helperPile.getTopStackCard()
            if (helperCard != null) {
                gameState.discardPiles.forEach { discardPile ->
                    if (discardPile.canPlayCard(helperCard)) {
                        discardPile.playCard(helperCard, helperPile)
                        return
                    }
                }
            }
        }
    }


    override fun play(gameState: StrategyGameState) {
        playDiscardPile(gameState)

        playHandCard(gameState, playAlsoJoker = false)
        playHandCard(gameState, playAlsoJoker = true)

        playHelperCard(gameState)

        playDiscardPile(gameState)
    }

    override fun getResult(gameState: StrategyGameState): PlayResult {
        return PlayResult(
            helperPile = gameState.playerHelperPiles.random(),
            handCard = gameState.playerHandCards.cards.random()
        )
    }
}