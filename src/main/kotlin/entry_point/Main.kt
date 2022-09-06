package entry_point

import dev.kord.common.entity.ButtonStyle
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.interaction.PublicInteractionResponseBehavior
import dev.kord.core.behavior.interaction.edit
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.message.create.InteractionResponseCreateBuilder
import dev.kord.rest.builder.message.create.actionRow
import dice.Die
import dice.Roller
import entry_point.TokenLoader

suspend fun main(args: Array<String>) {
    val kord = Kord(TokenLoader()) {
    }
    var roll = 0
    var r: PublicInteractionResponseBehavior? = null
    var previous: String? = null
    val roller = Roller(Die(d = 10))
    kord.createGlobalChatInputCommand("ino", "talk with Ino") {
        string("command", "performs a command") {
            choice("start", "start")
        }
    }

    kord.on<GuildChatInputCommandInteractionCreateEvent> {
        if (interaction.command.rootName != "ino") return@on
        r = interaction.respondPublic {
            content = "Click to roll"
            addButtons()
        }
        interaction.channel.createMessage {
            actionRow {
                selectMenu("id") {
                    placeholder = "History"
                    (1..25).forEach {
                        option("$it", "$it")
                    }
                }
            }
        }
    }

    val die = Die(10)

    kord.on<ButtonInteractionCreateEvent> {
        val bonus = interaction.componentId.toIntOrNull() ?: return@on
        roll++
        val prev = previous?.let { ", previous: $it" } ?: ""
        val result = roller.roll(bonus)
        r?.edit { content = "Roll #$roll: $result$prev" }
        previous = result
        interaction.respondPublic { }
    }

    kord.login()
}

private fun InteractionResponseCreateBuilder.addButtons() {
    actionRow {
        listOf(1, 2, 3, 4, 5).forEach {
            interactionButton(ButtonStyle.Primary, "$it") { label = "+$it" }
        }
    }
}
