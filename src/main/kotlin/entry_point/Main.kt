package entry_point

import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.interaction.number
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.interaction.user
import dice.DiceRoller
import dice.Die
import guild.Guilds
import kotlinx.coroutines.flow.collect
import roller.PersonalRoller
import roller.Rollers

suspend fun main(args: Array<String>) {

    val kord = Kord(TokenLoader(args))

    //kord.globalCommands.collect { it.delete() }

    kord.createGlobalChatInputCommand("roller", "Создаёт персональный роллер для каждого пользователя") {
        user("for", "Создать роллер для кого-то другого") {
            required = false
        }
    }

    kord.createGlobalChatInputCommand("difficulty", "Меняет сложность боя. +1 к сложности = -1 к броскам") {
        number("roll_disadvantage", "Максимум 3, не больше") {
            required = false
        }
        string("command", "опциональная команда") {
            required = false
            choice("reset", "reset")
        }
    }

    val guilds = Guilds()
    val rollers = Rollers()
    kord.on<GuildChatInputCommandInteractionCreateEvent> {
        when(interaction.command.rootName) {
            "roller" -> {
                val user = runCatching { interaction.command.users.values.first().id }.getOrNull()
                val roller = PersonalRoller(guilds, DiceRoller(Die(10)), user)
                roller.init(interaction)
                rollers.add(roller)
            }
            "difficulty" -> {
                val guild = interaction.guildId
                val isReset = interaction.command.strings.values.firstOrNull() == "reset"
                if(isReset) {
                    guilds.modifySettings(guild) { copy(difficulty_level = 0) }
                    interaction.respondPublic { content = "Сложность боя вернулась в норму. Штрафы отменены" }
                }
                val difficulty = interaction.command.numbers.values.firstOrNull()?.toInt()
                    ?: run { interaction.respondEphemeral { content = "Ошибка" }; return@on }
                guilds.modifySettings(guild) { copy(difficulty_level = difficulty) }
                interaction.respondPublic { content = "Сложность боя изменена на $difficulty. Все игроки получают ${-difficulty} к броскам" }
            }
            else -> return@on
        }

    }

    kord.on<ButtonInteractionCreateEvent> {
        rollers.routeButtonClick(interaction)
    }

    kord.on<SelectMenuInteractionCreateEvent> {
        interaction.respondPublic {  }
    }

    kord.login()
}