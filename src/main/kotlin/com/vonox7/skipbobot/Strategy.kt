package com.vonox7.skipbobot

abstract class Strategy {

    // One round is play() followed by getResult()
    abstract fun play(gameState: StrategyGameState)
    abstract fun getResult(gameState: StrategyGameState): PlayResult

    class StrategyGameState(
        val playerDeck: PlayerDeck,
        val playerHelperPiles: List<HelperPile>,
        val playerHandCards: HandCards,
        val discardPiles: List<DiscardPile>,
        val gameTick: Int,
        val otherPlayers: List<OtherPlayer>,
    ) {
        class OtherPlayer(val playerDeck: PlayerDeck, val playerHelperPiles: List<HelperPile>)
    }

    class PlayResult(val helperPile: HelperPile, val handCard: Card)
}