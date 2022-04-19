package com.vonox7.skipbobot

class Player(private val game: Game, private val name: String, val strategy: Strategy) {
    private val playerDeck: PlayerDeck = PlayerDeck(game)

    private val helperPiles: List<HelperPile> = (1..4).map { HelperPile() }

    private val handCards = HandCards(game)

    fun play(gameTick: Int) {
        // First draw cards
        handCards.refillCards()

        // Play out the strategy
        val gameState = Strategy.StrategyGameState(
            playerDeck = this.playerDeck,
            playerHelperPiles = this.helperPiles,
            playerHandCards = this.handCards,
            discardPiles = game.discardPiles,
            gameTick = gameTick,
            otherPlayers = game.players.indexOf(this).let { currentPlayerIndex ->
                game.players.drop(currentPlayerIndex + 1) + game.players.take(currentPlayerIndex)
            }.map { player ->
                Strategy.StrategyGameState.OtherPlayer(
                    playerDeck = player.playerDeck,
                    playerHelperPiles = player.helperPiles
                )
            }
        )
        strategy.play(gameState)
        val playResult = strategy.getResult(gameState)

        require(playResult.helperPile in helperPiles)
        require(playResult.handCard in handCards.cards)

        // End: Put one card to helper pile
        playResult.helperPile.playCard(playResult.handCard, handCards, endOfTurn = true)
    }

    fun hasWon(): Boolean {
        return playerDeck.getOpenCard() == null
    }

    override fun toString(): String {
        return "$name - Deck: $playerDeck - Hand: ${handCards.toString().padStart(30)} - Helper: $helperPiles"
    }
}