package com.vonox7.skipbobot

class HandCards(private val game: Game) : CardSource {
    var cards: List<Card> = emptyList()
        private set

    fun refillCards() {
        while (cards.count() < 5) {
            cards = cards + game.drawCard()
        }
    }

    override fun removeCard(card: Card, endOfTurn: Boolean) {
        require(card in cards)
        cards = cards - card

        // Refill hand cards immediately if player has no cards left on hand
        if (cards.isEmpty() && !endOfTurn) {
            refillCards()
        }
    }

    override fun toString(): String {
        return cards.toString()
    }
}