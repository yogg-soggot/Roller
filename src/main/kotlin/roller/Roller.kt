package roller

import dev.kord.core.entity.interaction.ButtonInteraction
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction

interface Roller {
    suspend fun init(interaction: GuildChatInputCommandInteraction)
    suspend fun onButtonClick(interaction: ButtonInteraction)
}
