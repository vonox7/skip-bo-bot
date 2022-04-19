package com.vonox7.skipbobot

class Game(val printEachRound: Boolean = false, val printFinalResult: Boolean = false) {

    class EmptyDeckException : Exception()

    // Normally exceptions should not be used for control flow. But in this case we really don't want to continue with
    // strategy handling if a player has already won, so throwing simplifies strategy writing.
    class PlayerHasWonException : Exception()

    lateinit var players: List<Player>

    val discardPiles: List<DiscardPile> = (1..4).map { DiscardPile(this) }

    private var gameTick = 0

    private val deck: MutableList<Card> =
        ((1..12).flatMap { number -> (1..12).map { NumberCard(number) } } + (1..18).map { JokerCard() }).shuffled()
            .toMutableList().apply { require(count() == 162) }

    // Deck that will be shuffled if we have no more cards to draw
    private val reserveDeck: MutableList<Card> = mutableListOf()

    fun drawCard(): Card {
        if (deck.isEmpty()) {
            deck += reserveDeck.shuffled()
            reserveDeck.clear()
        }
        if (deck.isEmpty()) {
            throw EmptyDeckException()
        }
        return deck.removeLast()
    }

    // Returns winning player
    fun play(): Player {
        if (printEachRound) printGameState()

        repeat(1000) {
            gameTick++
            players.forEach { player ->
                try {
                    player.play(gameTick)
                } catch (e: PlayerHasWonException) {
                    require(player.hasWon()) // Sanity check
                    if (printFinalResult) {
                        println("A PLAYER WON!!!!")
                        printGameState()
                        println("Winning Player:")
                        println(player)
                        println("")
                    }
                    return player
                }
            }
            if (printEachRound) printGameState()
        }

        throw IllegalStateException("Game did not end after 1000 turns, something is wrong with your strategies!")
    }

    private fun printGameState() {
        println("")
        println("-----------------------------------------------------")
        println("Tick $gameTick - " + discardPiles.joinToString(separator = "  |  ") { it.toString() })
        println("")
        players.forEach { println(it) }
        println("")
    }

    fun addCardsToReserveDeck(cards: List<Card>) {
        reserveDeck += cards
    }
}