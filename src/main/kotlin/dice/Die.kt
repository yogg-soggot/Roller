package dice

import kotlin.random.Random
import kotlin.random.nextInt

class Die(
    private val d: Int,
) {
    fun roll(): Int {
        return Random.nextInt(1..d)
    }

    fun roll(modifier: Int): Int {
        return (roll() + modifier).coerceAtLeast(1)
    }
}