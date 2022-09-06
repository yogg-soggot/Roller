import dev.kord.common.entity.Snowflake

fun Snowflake.mention(): String {
    return "<@$value>"
}