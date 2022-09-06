package roller

import dev.kord.common.entity.Snowflake

fun buttonId(userId: Snowflake, interactionId: Snowflake, bonus: Int) = "$userId:$interactionId:$bonus"
fun extractUserId(buttonId: String) = buttonId.split(':').first().let(::Snowflake)
fun extractInteractionId(buttonId: String) = buttonId.split(':')[1].let(::Snowflake)
