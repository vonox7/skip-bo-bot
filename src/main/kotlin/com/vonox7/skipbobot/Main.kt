package com.vonox7.skipbobot

import com.vonox7.skipbobot.strategies.ExampleStrategy
import com.vonox7.skipbobot.strategies.vonox7.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

fun main() {
    // Add here your own strategies
    val strategies: Set<KClass<out Strategy>> = setOf(
        ExampleStrategy::class,
        BaseStrategyWithHelperPile::class,
        StraightOrSameBaseStrategy::class,
        SameOrStraightBaseStrategy::class,
        StraightOrSameJokerWhenNeededStrategy::class,
        SameOrStraightJokerWhenNeededStrategy::class,
    )

    // Rounds between two strategies. Adapt this depending on your CPU and strategy count.
    val battleRounds = 10_000

    val winningStrategies = strategies.associateWith { AtomicInteger(0) }

    strategies.forEach { strategy1 ->
        strategies.forEach { strategy2 ->
            val currentStrategies = listOf(strategy1, strategy2)
            // BaseStrategy alone just loses, but play against BaseStrategy to find out how good other strategies are
            if (currentStrategies.any { it != BaseStrategy::class }) {
                val strategyWins = StrategyBattle(currentStrategies.map {
                    it.java.constructors.first().newInstance() as Strategy
                }).battle(battleRounds)
                strategyWins.forEach { (strategy, wins) ->
                    winningStrategies[strategy::class]!!.addAndGet(wins)
                }
            }
        }
    }

    println("Total wins: ")
    winningStrategies.forEach {
        println(it.key.simpleName!!.padStart(50) + ": " + it.value.toString().padEnd(4))
    }
}