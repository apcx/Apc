package apc.jar

object Speed {

    fun calc(default: Int) {
        var last = 0
        repeat(2000) {
            val raw = default * 1000 / (1000 + it)
            var frames = raw / 66
            if (raw % 66 != 0) {
                ++frames
            }
            val cd = 66 * frames
            if (last != cd) {
                last = cd
                println("${String.format("%.1f", it / 10f)} -> $raw $frames frames - $cd ms")
            }
        }
    }
}