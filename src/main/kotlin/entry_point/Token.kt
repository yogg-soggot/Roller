package entry_point

import java.util.Properties

class TokenLoader private constructor() {
    private fun load(args: Array<String>): String {
        val prop = javaClass.classLoader.getResourceAsStream("token.properties").use {
            Properties().apply { load(it) }
        }
        val isTest = args.getOrNull(0) == "test"
        val env = if (isTest) "test" else "prod"
        return prop.getProperty(env)
    }

    companion object {
        operator fun invoke(args: Array<String>) = TokenLoader().load(args)
    }
}
