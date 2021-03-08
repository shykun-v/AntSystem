import kotlin.math.pow
import kotlin.math.sqrt

const val PROBLEM_NAME = "eil76.tsp"
const val ITERATIONS_COUNT = 2000

const val ALPHA = 1
const val BETTA = 2
const val EVAPORATION = 0.5
const val Q = 10

private lateinit var d: Array<Array<Double>>
private lateinit var tau: Array<Array<Double>>
private var N = 0 // amount of nodes
private var K = 1 // amount of ants

private var bestResult = Int.MAX_VALUE.toDouble()
private var bestPath = mutableListOf<Int>()
private lateinit var visited: Array<Boolean>

fun main() {
    setup()
    solve()
}

fun setup() {
    val reader = Reader(PROBLEM_NAME)
    N = reader.N
    K = reader.N
    d = reader.d

    fillPheromones()
}

fun fillPheromones() {
    var dist = 0.0
    for (i in 0 until N - 1) {
        dist += d[i][i + 1]
    }
    dist += d[N - 1][0]
    bestResult = dist

    val tauMax = getTauMax()
    tau = Array(N) { Array(N) { tauMax } }
}

fun calculateDistance(from: Node, to: Node): Double {
    val xDiff = to.x - from.x
    val yDiff = to.y - from.y
    return sqrt(xDiff * xDiff + yDiff * yDiff)
}

fun solve() {
    val startTime = System.currentTimeMillis()
    for (i in 1..ITERATIONS_COUNT) {
        var bestLocalResult = Int.MAX_VALUE.toDouble()
        var bestAnt = 0
        var prev = Array(K) { mutableListOf<Int>() }

        for (k in 0 until K) {
            visited = Array(N) { false }
            var curResult = 0.0
            val initialNode = (0 until N).shuffled().first()
            var curNode = initialNode
            prev[k].add(curNode)
            visited[curNode] = true

            while (true) {
                val nextNode = getNextNode(curNode, visited) ?: break
                curResult += d[curNode][nextNode]
                prev[k].add(nextNode)
                visited[nextNode] = true
                curNode = nextNode
            }
            curResult += d[curNode][initialNode]
            prev[k].add(initialNode)

            if (curResult < bestLocalResult) {
                bestLocalResult = curResult
                bestAnt = k
            }
            println("ITERATION $i.$k: bestResult = $bestResult; result = $bestLocalResult; path = ${prev[k]}")
        }

        updatePheromones(prev[bestAnt])
        if (bestLocalResult < bestResult) {
            bestResult = bestLocalResult
            bestPath = prev[bestAnt]
        }
    }

    println("Best result: $bestResult")
    println("Best path: $bestPath")

    val duration = (System.currentTimeMillis() - startTime).toDouble() / 1000
    println("Duration: $duration")
}

fun getNextNode(currentNode: Int, visited: Array<Boolean>): Int? {
    if (visited.all { it }) {
        return null
    }
    var p_list = Array(N) { 0.0 }
    for (i in 0 until N) {
        if (i != currentNode && !visited[i]) {
            p_list[i] = tau[currentNode][i].pow(ALPHA) * (1.0 / d[currentNode][i]).pow(BETTA)
        }
    }

    var sumP = 0.0
    for (i in 0 until N) {
        sumP += p_list[i]
    }

    for (i in 0 until N) {
        p_list[i] = p_list[i] / sumP
    }

    var rand = Math.random()
    for (i in 0 until N) {
        rand -= p_list[i]
        if (rand <= 0) {
            return i
        }
    }
    return null
}

fun updatePheromones(prev: List<Int>) {
    val tauMax = getTauMax()
    val tauMin = getTauMin(tauMax)

    for (i in 0 until N) {
        for (j in 0 until N) {
            tau[i][j] = (1 - EVAPORATION) * tau[i][j]
            if (tau[i][j] > tauMax) {
                tau[i][j] = tauMax
            }
            if (tau[i][j] < tauMin) {
                tau[i][j] = tauMin
            }
        }
    }

    for (i in 0 until N - 1) {
        val from = prev[i]
        val to = prev[i + 1]
        val deltaTau = Q / d[from][to]

        tau[from][to] += deltaTau
        if (tau[from][to] > tauMax) {
            tau[from][to] = tauMax
        }
        if (tau[from][to] < tauMin) {
            tau[from][to] = tauMin
        }
    }
}


fun getTauMax(): Double {
    return 1.0 / ((1 - EVAPORATION) * bestResult)
}

fun getTauMin(tauMax: Double): Double {
    return tauMax * (1 - 0.05.pow(1.0 / N)) / ((N / 2.0 - 1) * 0.05.pow(1.0 / N))
}
