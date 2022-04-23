package com.vonox7.skipbobot

abstract class Strategy {

    // One round is play() followed by getResult()
    abstract fun play(gameState: StrategyGameState)
    abstract fun getResult(gameState: StrategyGameState): PlayResult

    // The game state what the current player can observe
    class StrategyGameState(
        val playerDeck: PlayerDeck,
        val playerHelperPiles: List<HelperPile>,
        val playerHandCards: HandCards,
        val discardPiles: List<DiscardPile>,
        val gameTick: Int,
        val otherPlayers: List<OtherPlayer>,
    ) {
        // TODO pass here un-modifiable PlayerDeck & HelperPile, so a strategy can't cheat
        class OtherPlayer(val playerDeck: PlayerDeck, val playerHelperPiles: List<HelperPile>)
    }

    // Play at the end of the turn a hand card to the given helper pile
    class PlayResult(val helperPile: HelperPile, val handCard: Card)
}