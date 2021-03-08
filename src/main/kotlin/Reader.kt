import java.io.File
import java.nio.file.Files
import kotlin.math.sqrt

class Reader(private val fileName: String) {

    var N = -1
    lateinit var d: Array<Array<Double>>

    init {
        val content = getContent()
        if (fileName.endsWith(".tsp")) {
            parseTspContent(content)
        } else if (fileName.endsWith(".atsp")) {
            parseAtspContent(content)
        }
    }

    private fun parseTspContent(content: String) {
        val lines = content.split("\n").filter { it.isNotEmpty() && it.first().isDigit() }
        val nodes = mutableListOf<Node>()
        lines.forEach {
            val lineContent = it.split(" ")
            val num = lineContent[0].toInt() - 1
            val x = lineContent[1].toDouble()
            val y = lineContent[2].toDouble()
            nodes.add(Node(num, x, y))
        }

        N = nodes.size
        d = Array(N) { Array(N) { 0.0 } }
        for (i in 0 until nodes.size) {
            for (j in 0 until nodes.size) {
                val distance = calculateDistance(nodes[i], nodes[j])
                d[i][j] = distance
            }
        }
    }

    private fun parseAtspContent(content: String) {
        N = content
            .split("\n")
            .find { it.startsWith("DIMENSION") }
            ?.filter { it.isDigit() }
            ?.toInt() ?: return

        val lines = content
            .split("\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() && it.first().isDigit() }

        val numbers = mutableListOf<Int>()
        lines.forEach {
            val lineNumbers = it.split(" ").filter { it.isNotBlank() }.map { it.toInt() }
            numbers.addAll(lineNumbers)
        }

        val tmp = numbers.chunked(N)

        d = Array(N) { Array(N) { 0.0 } }
        for (i in 0 until N) {
            for (j in 0 until N) {
                d[i][j] = tmp[i][j].toDouble()
            }
        }
        println(d)
    }

    private fun getContent(): String {
        val file = getFileFromResources(fileName)
        return String(Files.readAllBytes(file.toPath()))
    }

    private fun getFileFromResources(fileName: String): File {
        val url = javaClass.classLoader.getResource(fileName).file
            ?: throw IllegalArgumentException("file not found! $fileName")
        return File(url)
    }


    fun calculateDistance(from: Node, to: Node): Double {
        val xDiff = to.x - from.x
        val yDiff = to.y - from.y
        return sqrt(xDiff * xDiff + yDiff * yDiff)
    }
}