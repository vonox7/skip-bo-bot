package com.vonox7.skipbobot

class DiscardPile(private val game: Game) : Pile {
    var playedCards: MutableList<Card> = mutableListOf()

    var topCardValue = 12
        private set

    override fun playCard(card: Card, cardSource: CardSource, endOfTurn: Boolean) {
        require(canPlayCard(card)) {
            "$card can not be played on $this"
        }
        topCardValue = (topCardValue % 12) + 1

        if (playedCards.count() == 12) {
            game.addCardsToReserveDeck(playedCards)
            playedCards.clear()
        }

        playedCards += card
        cardSource.removeCard(card)
    }

    override fun canPlayCard(card: Card): Boolean {
        return when (card) {
            is NumberCard -> (topCardValue % 12) + 1 == card.value
            is JokerCard -> true
        }
    }

    override fun toString(): String {
        return "｢${topCardValue.toString().padStart(2, ' ')}｣"
    }
}