package entry_point

import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.interaction.user
import dice.DiceRoller
import dice.Die
import roller.PersonalRoller
import roller.Rollers

suspend fun main(args: Array<String>) {

    val kord = Kord(TokenLoader())

    kord.createGlobalChatInputCommand("roller", "Создаёт персональный роллер для каждого пользователя") {
        user("for", "Создать роллер для кого-то другого") {
            required = false
        }
    }

    val rollers = Rollers()
    kord.on<GuildChatInputCommandInteractionCreateEvent> {
        if (interaction.command.rootName != "roller") return@on
        val user = runCatching { interaction.command.users.values.first().id }.getOrNull()
        val roller = PersonalRoller(DiceRoller(Die(10)), user)
        roller.init(interaction)
        rollers.add(roller)
    }

    kord.on<ButtonInteractionCreateEvent> {
        rollers.routeButtonClick(interaction)
    }

    kord.on<SelectMenuInteractionCreateEvent> {
        interaction.respondPublic {  }
    }

    kord.login()
}