package entry_point

import java.util.Properties

class TokenLoader private constructor() {
    private fun load(): String {
        val prop = javaClass.classLoader.getResourceAsStream("token.properties").use {
            Properties().apply { load(it) }
        }
        return prop.getProperty("prod")
    }

    companion object {
        operator fun invoke() = TokenLoader().load()
    }
}
