package com.vonox7.skipbobot.strategies.vonox7

import com.vonox7.skipbobot.JokerCard
import com.vonox7.skipbobot.NumberCard
import com.vonox7.skipbobot.Strategy


// Strategy: Try to play as many deck & hand & helper cards as possible
open class BaseStrategyWithHelperPile : BaseStrategy() {

    // Returns if a card was played
    protected fun playCard(gameState: StrategyGameState, playJokers: Boolean, playHelper: Boolean = true): Boolean {
        // Deck card
        if (playOpenDeckCards(gameState)) {
            return true
        }

        // Hand card
        gameState.playerHandCards.cards.forEach { handCard ->
            gameState.discardPiles.forEach { discardPile ->
                if (discardPile.canPlayCard(handCard) && (playJokers || handCard is NumberCard)) {
                    discardPile.playCardOptimized(handCard, gameState.playerHandCards, gameState)
                    return true
                }
            }
        }

        // Try now to play one helper card
        if (playHelper) {
            gameState.playerHelperPiles.forEach helperPile@{ helperPile ->
                val helperPileCard = helperPile.getTopStackCard() ?: return@helperPile
                gameState.discardPiles.forEach { discardPile ->
                    if (discardPile.canPlayCard(helperPileCard) && (playJokers || helperPileCard is NumberCard)) {
                        discardPile.playCardOptimized(helperPileCard, helperPile, gameState)
                        return true
                    }
                }
            }
        }

        return false
    }

    override fun play(gameState: StrategyGameState) {
        while (playCard(gameState, playJokers = false) || playCard(gameState, playJokers = true)) Unit
    }
}

open class PlayJokerWhenNeededStrategy : BaseStrategyWithHelperPile() {
    private fun playWithJokers(gameState: StrategyGameState): Boolean {
        val openValue = (gameState.playerDeck.getOpenCard()!! as NumberCard).value
        gameState.discardPiles.forEach { discardPile ->
            val neededCards = cardsBetween(discardPile.topCardValue, openValue).toMutableSet()
            val playableCards =
                (gameState.playerHandCards.cards + gameState.playerHelperPiles.mapNotNull { it.getTopStackCard() })
            val playableCardValues = playableCards.mapNotNull { (it as? NumberCard)?.value }.toSet()
            val payableJokerCount = playableCards.count { it is JokerCard }
            neededCards -= playableCardValues
            if (neededCards.count() <= payableJokerCount) {
                playCard(gameState, playJokers = true)
                return true
            }
        }
        return false
    }

    override fun play(gameState: StrategyGameState) {
        while (playCard(gameState, playJokers = false) || playWithJokers(gameState)) Unit
    }
}

// TODO something is wrong with this strategy? why so bad?
open class PlayJokerHelperWhenNeededStrategy : BaseStrategyWithHelperPile() {
    private fun playWithJokersAndHelper(gameState: StrategyGameState): Boolean {
        val openValue = (gameState.playerDeck.getOpenCard()!! as NumberCard).value
        gameState.discardPiles.forEach { discardPile ->
            val neededCards = cardsBetween(discardPile.topCardValue, openValue).toMutableSet()
            val playableCards =
                (gameState.playerHandCards.cards + gameState.playerHelperPiles.mapNotNull { it.getTopStackCard() })
            val playableCardValues = playableCards.mapNotNull { (it as? NumberCard)?.value }.toSet()
            val payableJokerCount = playableCards.count { it is JokerCard }
            neededCards -= playableCardValues
            if (neededCards.count() <= payableJokerCount) {
                while (playCard(gameState, playJokers = true, playHelper = true)) Unit
                return true
            }
        }
        return false
    }

    override fun play(gameState: StrategyGameState) {
        while (playCard(gameState, playJokers = false, playHelper = false) || playWithJokersAndHelper(gameState)) Unit
    }
}

open class StraightOrSameBaseStrategy : BaseStrategyWithHelperPile() {
    override fun getResult(gameState: StrategyGameState) = getStraightOrSame(gameState)
}

open class SameOrStraightBaseStrategy : BaseStrategyWithHelperPile() {
    override fun getResult(gameState: StrategyGameState) = getSameOrStraight(gameState)
}

open class StraightOrSameJokerWhenNeededStrategy : PlayJokerWhenNeededStrategy() {
    override fun getResult(gameState: StrategyGameState) = getStraightOrSame(gameState)
}

open class SameOrStraightJokerWhenNeededStrategy : PlayJokerWhenNeededStrategy() {
    override fun getResult(gameState: StrategyGameState) = getSameOrStraight(gameState)
}

open class StraightOrSameJokerHelperWhenNeededStrategy : PlayJokerHelperWhenNeededStrategy() {
    override fun getResult(gameState: StrategyGameState) = getStraightOrSame(gameState)
}

open class SameOrStraightJokerHelperWhenNeededStrategy : PlayJokerHelperWhenNeededStrategy() {
    override fun getResult(gameState: StrategyGameState) = getSameOrStraight(gameState)
}

private fun getSameOrStraight(gameState: Strategy.StrategyGameState): Strategy.PlayResult {
    // Same
    gameState.playerHelperPiles.shuffled().forEach { helperPile ->
        val helperPileTopCardValue = (helperPile.getTopStackCard() as? NumberCard)?.value
        val handCard = gameState.playerHandCards.cards.firstOrNull { card ->
            helperPileTopCardValue == (card as? NumberCard)?.value
        }
        if (handCard != null && helperPileTopCardValue != null) {
            return Strategy.PlayResult(helperPile, handCard)
        }
    }

    // Straight
    gameState.playerHelperPiles.shuffled().forEach { helperPile ->
        val helperPileTopCardValue = (helperPile.getTopStackCard() as? NumberCard)?.value
        val handCard = gameState.playerHandCards.cards.firstOrNull { card ->
            helperPileTopCardValue == (card as? NumberCard)?.value?.let { it + 1 }
        }
        if (handCard != null && helperPileTopCardValue != null) {
            return Strategy.PlayResult(helperPile, handCard)
        }
    }

    // Random
    return Strategy.PlayResult(
        helperPile = gameState.playerHelperPiles.minByOrNull { it.stackSize() }!!,
        handCard = gameState.playerHandCards.cards.first()
    )
}


private fun getStraightOrSame(gameState: Strategy.StrategyGameState): Strategy.PlayResult {
    // Straight
    gameState.playerHelperPiles.shuffled().forEach { helperPile ->
        val helperPileTopCardValue = (helperPile.getTopStackCard() as? NumberCard)?.value
        val handCard = gameState.playerHandCards.cards.firstOrNull { card ->
            helperPileTopCardValue == (card as? NumberCard)?.value?.let { it + 1 }
        }
        if (handCard != null && helperPileTopCardValue != null) {
            return Strategy.PlayResult(helperPile, handCard)
        }
    }

    // Same
    gameState.playerHelperPiles.shuffled().forEach { helperPile ->
        val helperPileTopCardValue = (helperPile.getTopStackCard() as? NumberCard)?.value
        val handCard = gameState.playerHandCards.cards.firstOrNull { card ->
            helperPileTopCardValue == (card as? NumberCard)?.value
        }
        if (handCard != null && helperPileTopCardValue != null) {
            return Strategy.PlayResult(helperPile, handCard)
        }
    }

    // Random
    return Strategy.PlayResult(
        helperPile = gameState.playerHelperPiles.minByOrNull { it.stackSize() }!!,
        handCard = gameState.playerHandCards.cards.first()
    )
}