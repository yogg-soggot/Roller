package entry_point

object TokenLoader {
    operator fun invoke(): String {
        return javaClass.classLoader.getResource("ino_token.txt")!!.readText()
    }
}
