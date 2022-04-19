package com.vonox7.skipbobot.tests

import com.vonox7.skipbobot.*
import com.vonox7.skipbobot.strategies.BaseStrategy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AlgorithmTests {

    @Test
    fun cardsBetweenWithoutWrap() {
        val baseStrategy = BaseStrategy()
        assertEquals(listOf(), baseStrategy.cardsBetween(1, 1))
        assertEquals(listOf(), baseStrategy.cardsBetween(1, 2))
        assertEquals(listOf(2), baseStrategy.cardsBetween(1, 3))
        assertEquals(listOf(2, 3), baseStrategy.cardsBetween(1, 4))

        assertEquals(listOf(), baseStrategy.cardsBetween(2, 2))
        assertEquals(listOf(), baseStrategy.cardsBetween(2, 3))
        assertEquals(listOf(3), baseStrategy.cardsBetween(2, 4))
        assertEquals(listOf(), baseStrategy.cardsBetween(11, 11))
        assertEquals(listOf(), baseStrategy.cardsBetween(10, 11))
        assertEquals(listOf(10), baseStrategy.cardsBetween(9, 11))

        assertEquals(listOf(11), baseStrategy.cardsBetween(10, 12))
        assertEquals(listOf(), baseStrategy.cardsBetween(11, 12))
        assertEquals(listOf(), baseStrategy.cardsBetween(12, 12))
        assertEquals((1..12).map { it }, baseStrategy.cardsBetween(1, 12))
    }

    @Test
    fun cardsBetweenWithWrap() {
        val baseStrategy = BaseStrategy()
        assertEquals(listOf(), baseStrategy.cardsBetween(12, 1))
        assertEquals(listOf(12), baseStrategy.cardsBetween(11, 1))
        assertEquals(listOf(1), baseStrategy.cardsBetween(12, 2))
        assertEquals(listOf(12, 1), baseStrategy.cardsBetween(11, 2))
        assertEquals((1..10).map { it }, baseStrategy.cardsBetween(12, 11))
        assertEquals(listOf(10, 11, 12, 1, 2, 3, 4), baseStrategy.cardsBetween(9, 5))
    }

    @Test
    fun discardPileCanPlayCard() {
        val cardsToPlay = ((1..12).map { it } + (1..12).map { it }).map { NumberCard(it) }
        val pile = DiscardPile(Game())

        cardsToPlay.forEach { cardToPlay ->
            val cardsNotToPlay = ((1..12).map { it } - cardToPlay.value).map { NumberCard(it) }
            cardsNotToPlay.forEach { cardNotToPlay ->
                assertFalse(pile.canPlayCard(cardNotToPlay), "$cardNotToPlay -!-> $pile")
            }

            assertTrue(pile.canPlayCard(cardToPlay), "$cardToPlay --> $pile")
            pile.playCard(cardToPlay, EndlessTestCardSource())
            assertEquals(pile.topCardValue, cardToPlay.value)
        }
    }

    class EndlessTestCardSource : CardSource {
        override fun removeCard(card: Card, endOfTurn: Boolean) {
        }
    }
}