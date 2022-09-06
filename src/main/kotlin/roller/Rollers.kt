package roller

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.entity.interaction.ButtonInteraction

class Rollers {
    private val rollers = HashMap<Snowflake, PersonalRoller>()
    fun add(roller: PersonalRoller) {
        rollers[roller.userId] = roller
    }

    suspend fun routeButtonClick(interaction: ButtonInteraction) {
        val user = interaction.componentId.let(::extractUserId)
        val originalInteractionId = interaction.componentId.let(::extractInteractionId)
        val roller = rollers[user] ?: return
        if (originalInteractionId != roller.interactionId) {
            interaction.respondEphemeral { content = "Ты создал новый роллер, вот его и используй" }
            return
        }
        roller.onButtonClick(interaction)
    }
}
