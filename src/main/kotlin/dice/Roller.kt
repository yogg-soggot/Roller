package dice

class Roller(private val die: Die) {
    fun roll(bonus: Int): String {
        return when(die.roll(bonus)) {
            in fail -> "Fail"
            in partial -> "Partial"
            else -> "Success"
        }
    }

    companion object {
        private val fail = 1..6
        private val partial = 7..9
    }
}
