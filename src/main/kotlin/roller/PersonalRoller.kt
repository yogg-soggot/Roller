package roller

import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.behavior.interaction.response.PublicMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.edit
import dev.kord.core.entity.interaction.ButtonInteraction
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.rest.builder.component.option
import dev.kord.rest.builder.message.MessageBuilder
import dev.kord.rest.builder.message.actionRow
import dice.DiceRoller
import guild.Guilds
import utils.FixedSizeQueue
import utils.mention
import java.security.InvalidKeyException

class PersonalRoller(
    private val guilds: Guilds,
    private val diceRoller: DiceRoller,
    private val providedUser: Snowflake? = null,
    private val isExtended: Boolean = false,
) : Roller {
    lateinit var userId: Snowflake

    private var rollCount = 0
    private val history = FixedSizeQueue<RollRecord>(15)

    private lateinit var interactionResponse: PublicMessageInteractionResponseBehavior
    lateinit var interactionId: Snowflake

    override suspend fun init(interaction: GuildChatInputCommandInteraction) {
        bindUser(interaction)
        interactionId = interaction.id
        interactionResponse = interaction.respondPublic {
            headerMessage()
            historyPlaceholder()
        }
    }

    override suspend fun onButtonClick(interaction: ButtonInteraction) {
        rollCount++
        val bonus = interaction.component.label?.extractBonus() ?: throw InvalidKeyException("Cannot extract bonus")
        val rollResult = diceRoller.roll(bonus - difficulty(interaction))
        history.add(RollRecord(rollCount, rollResult))
        interactionResponse.edit {
            headerMessage()
            historyRecord()
        }
        interaction.deferEphemeralMessageUpdate()
    }

    private fun MessageBuilder.headerMessage() {
        content = "${userId.mention()}, кликай на бонус чтобы бросить"
        if (isExtended) {
            addButtons(-2..2)
            addButtons(3..7)
        } else addButtons(1..5)
    }

    private fun MessageBuilder.historyPlaceholder() {
        actionRow {
            stringSelect("$userId history") {
                placeholder = "История бросков"
                option("Пока нет бросков", "0")
            }
        }
    }

    private fun MessageBuilder.historyRecord() {
        actionRow {
            stringSelect("$userId history") {
                placeholder = displayResult(history[0])
                history.forEachIndexed { i, record ->
                    option(label = displayResult(record), value = "$i")
                }
            }
        }
    }

    private fun bindUser(interaction: GuildChatInputCommandInteraction) {
        userId = providedUser ?: interaction.user.id
    }

    private fun MessageBuilder.addButtons(range: IntRange) {
        fun label(bonus: Int) = if (bonus <= 0) "$bonus" else "+$bonus"
        actionRow {
            for (bonus in range) {
                interactionButton(ButtonStyle.Primary, buttonId(userId, interactionId, bonus)) { label = label(bonus) }
            }
        }
    }

    private fun displayResult(rollRecord: RollRecord): String {
        val sign = if (rollRecord.bonus < 0) "" else "+"
        return "Ролл #${rollRecord.number}: ${rollRecord.outcome} [${rollRecord.roll}$sign${rollRecord.bonus}]"
    }

    private fun String.extractBonus() = toIntOrNull()

    private fun difficulty(interaction: ButtonInteraction): Int {
        val guild = interaction.data.guildId.value ?: throw IllegalStateException("Cannot extract guild id")
        return guilds.settings[guild]?.difficulty_level ?: 0
    }
}