package guild

import dev.kord.common.entity.Snowflake
import java.util.concurrent.ConcurrentHashMap

class Guilds {
    val settings = ConcurrentHashMap<Snowflake, GuildSettings>()

    fun modifySettings(guild: Snowflake, modify: GuildSettings.() -> GuildSettings) {
        settings[guild] = settings[guild]?.modify() ?: GuildSettings().modify()
    }
}