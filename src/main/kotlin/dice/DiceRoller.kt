package dice

class DiceRoller(private val die: Die) {
    fun roll(bonus: Int): RollResult {
        val roll = die.roll()
        val outcome = when((roll + bonus).coerceAtLeast(1)) {
            in fail -> "Провал"
            in partial -> "Частичный успех"
            else -> "Успех"
        }
        return RollResult(roll, bonus, outcome)
    }

    companion object {
        private val fail = 1..6
        private val partial = 7..9
    }
}

data class RollResult(
    val roll: Int,
    val bonus: Int,
    val outcome: String,
)
