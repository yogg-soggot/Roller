package roller

import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.ButtonInteraction
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.rest.builder.message.create.InteractionResponseCreateBuilder
import dev.kord.rest.builder.message.create.actionRow
import dev.kord.rest.builder.message.modify.actionRow
import dice.DiceRoller
import guild.Guilds
import utils.mention
import utils.FixedSizeQueue
import java.security.InvalidKeyException

class PersonalRoller(
    private val guilds: Guilds,
    private val diceRoller: DiceRoller,
    private val providedUser: Snowflake? = null
) : Roller {
    lateinit var userId: Snowflake

    private var rollCount = 0
    private val history = FixedSizeQueue<RollRecord>(15)

    lateinit var historyMessage: Message
    lateinit var interactionId: Snowflake

    override suspend fun init(interaction: GuildChatInputCommandInteraction) {
        bindUser(interaction)
        interactionId = interaction.id
        interaction.respondPublic {
            content = "${userId.mention()}, кликай на бонус чтобы бросить"
            addButtons()
        }
        historyMessage = interaction.channel.createMessage {
            actionRow {
                selectMenu("$userId history") {
                    placeholder = "История бросков"
                    option("Пока нет бросков", "0")
                }
            }
        }
    }

    override suspend fun onButtonClick(interaction: ButtonInteraction) {
        rollCount++
        val bonus = interaction.component?.label?.extractBonus() ?: throw InvalidKeyException("Cannot extract bonus")
        val rollResult = diceRoller.roll(bonus - difficulty(interaction))
        history.add(RollRecord(rollCount, rollResult))
        historyMessage.edit {
            actionRow {
                selectMenu("$userId history") {
                    placeholder = displayResult(history[0])
                    history.forEachIndexed { i, record ->
                        option(label = displayResult(record), value = "$i")
                    }
                }
            }
        }
        interaction.respondPublic {  }
    }

    private fun bindUser(interaction: GuildChatInputCommandInteraction) {
        userId = providedUser ?: interaction.user.id
    }

    private fun InteractionResponseCreateBuilder.addButtons() {
        actionRow {
            for (bonus in 1..5) {
                interactionButton(ButtonStyle.Primary, buttonId(userId, interactionId, bonus)) { label = "+$bonus" }
            }
        }
    }

    private fun displayResult(rollRecord: RollRecord): String {
        return "Ролл #${rollRecord.number}: ${rollRecord.outcome} [${rollRecord.roll}+${rollRecord.bonus}]"
    }

    private fun String.extractBonus() = drop(1).toIntOrNull()

    private fun difficulty(interaction: ButtonInteraction): Int {
        val guild = interaction.data.guildId.value ?: throw IllegalStateException("Cannot extract guild id")
        return guilds.settings[guild]?.difficulty_level ?: 0
    }
}