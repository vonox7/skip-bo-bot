package com.vonox7.skipbobot

sealed class Card

class NumberCard(val value: Int) : Card() {
    init {
        require(value in 1..12)
    }

    override fun toString(): String {
        return "｢${value.toString().padStart(2, ' ')}｣"
    }
}

class JokerCard : Card() {

    override fun toString(): String {
        return "｢JO｣"
    }

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun hashCode(): Int {
        return System.identityHashCode(this)
    }
}