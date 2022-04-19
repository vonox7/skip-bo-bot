package com.vonox7.skipbobot

import java.util.concurrent.atomic.AtomicInteger

class StrategyBattle(private val strategies: List<Strategy>) {
    private val playerCount = 4

    private fun createPlayer(game: Game, strategy: Strategy, index: Int): Player {
        return Player(
            game,
            "$index ${strategy::class.simpleName}".padEnd(20).take(20),
            strategy
        )
    }

    // Return wins
    fun battle(totalRounds: Int): Map<Strategy, Int> {
        val strategyWins: Map<Strategy, AtomicInteger> = strategies.associateWith { AtomicInteger(0) }
        var emptyDecks = 0

        // TODO p-value for significant better strategy, or just say "equal"

        repeat(totalRounds) { currentRound ->
            try {
                val winningStrategy = battleOneRound()
                strategyWins[winningStrategy]!!.addAndGet(1)
            } catch (e: Game.EmptyDeckException) {
                emptyDecks += 1
            }
            if (currentRound % (totalRounds / 10) == 0) {
                print(".")
            }
        }

        println("Wins: " + strategyWins.map {
            it.key::class.simpleName.toString().padStart(50) + ": " + it.value.toString().padEnd(4)
        } + (if (emptyDecks > 0) " ($emptyDecks empty decks)" else ""))

        return strategyWins.mapValues { it.value.get() }
    }

    // Return winning strategy
    private fun battleOneRound(): Strategy {
        val game = Game()
        val players = (0 until playerCount).map { playerIndex ->
            createPlayer(game, strategies[playerIndex % strategies.count()], playerIndex)
        }
        game.players = players

        return game.play().strategy
    }
}