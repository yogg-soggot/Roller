package roller

import dice.RollResult

data class RollRecord(
    val number: Int,
    val roll: Int,
    val bonus: Int,
    val outcome: String,
) {
    constructor(number: Int, result: RollResult): this(number, result.roll, result.bonus, result.outcome)
}