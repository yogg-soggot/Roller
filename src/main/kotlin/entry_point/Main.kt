package entry_point

import dev.kord.core.Kord
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.interaction.string
import dice.DiceRoller
import dice.Die
import roller.PersonalRoller
import roller.Rollers

suspend fun main(args: Array<String>) {
    val kord = Kord(TokenLoader())

    kord.createGlobalChatInputCommand("ino", "talk with Ino") {
        string("command", "performs a command") {
            choice("create roller", "create roller")
        }
    }
    val rollers = Rollers()
    kord.on<GuildChatInputCommandInteractionCreateEvent> {
        if (interaction.command.rootName != "ino") return@on
        val roller = PersonalRoller(DiceRoller(Die(10)))
        roller.init(interaction)
        rollers.add(roller)
    }

    kord.on<ButtonInteractionCreateEvent> {
        rollers.routeButtonClick(interaction)
    }

    kord.login()
}
