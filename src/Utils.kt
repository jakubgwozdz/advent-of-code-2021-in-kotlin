import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.time.Duration
import java.time.Instant
import java.time.LocalTime

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt").readLines()


fun <T> parse(line: String, re: Regex, op: (MatchResult.Destructured) -> T): T {
    return re.matchEntire(line)
        ?.destructured
        ?.let(op)
        ?: error("`$line` does not match `${re.pattern}`")
}

fun <T> parse(line: String, pattern: String, op: (MatchResult.Destructured) -> T) =
    parse(line, Regex(pattern), op)


/**
 * Converts string to md5 hash.
 */
fun String.md5(): String =
    BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)

fun <T> expect(expected: T, block: () -> T) {
    val actual = block();
    check(expected == actual) { "expected $expected, but got $actual" }
}

val startTime = Instant.now()

fun <T> logWithTime(msg: T, op: T.() -> String = { "$this" }) {
    val t = LocalTime.of(0, 0) + Duration.between(startTime, Instant.now())
    println("${t.toString().take(12).padEnd(12)}: ${msg.op()}")
}

operator fun<T1,T2> Iterable<T1>.times(other:Iterable<T2>) = flatMap { a -> other.map { b -> a to b } }
operator fun<T1,T2> Iterable<T1>.times(other:Sequence<T2>) = flatMap { a -> other.map { b -> a to b } }

operator fun<T1,T2> Sequence<T1>.times(other:Iterable<T2>) = flatMap { a -> other.asSequence().map { b -> a to b } }
operator fun<T1,T2> Sequence<T1>.times(other:Sequence<T2>) = flatMap { a -> other.map { b -> a to b } }


open class Queue<E : Any> {

    protected var queue = mutableListOf<E>()

    val size get() = queue.size

    fun isNotEmpty(): Boolean = size > 0

    fun poll(): E {
        check(size > 0)
        return queue.removeAt(0)
    }

    open fun offer(e: E) {
        queue.add(e)
    }

}


class PriorityQueue<E : Any>(private val comparator: Comparator<E>) : Queue<E>() {

    override fun offer(e: E) {
        val index = queue.binarySearch(e, comparator).let {
            if (it < 0) -it - 1 else it
        }
        queue.add(index, e)
    }

}


interface Pathfinder<T : Any, R : Any> {
    fun findShortest(startState: R, endOp: (R) -> Boolean): R?
}

open class BFSPathfinder<T : Any, R : Any, I : Comparable<I>>(
    val loggingOp: (() -> Any) -> Unit = {},
    val adderOp: (R, T) -> R,
    val distanceOp: ((R) -> I),
    val meaningfulOp: (R, I) -> Boolean = { _, _ -> true },
    val priority: Comparator<Pair<R, I>> = compareBy { it.second },
    val waysOutOp: (R) -> Iterable<T>,
) : Pathfinder<T, R> {

    override fun findShortest(startState: R, endOp: (R) -> Boolean): R? {
        add(startState)
        while (toVisit.isNotEmpty()) {
            val state = pick()
            waysOutOp(state)
                .also { loggingOp { "WaysOut for $state: $it" } }
                .map { next -> adderOp(state, next) }
                .forEach { r ->
                    if (endOp(r)) {
                        done(r)
                    } else {
                        add(r)
                    }
                }
        }

        return currentBest?.first
    }

    private fun add(nextState: R) {
        val distance = distanceOp(nextState)
        if (!meaningfulOp(nextState, distance)) {
            loggingOp { "skipping $nextState with distance $distance, it's not meaningful" }
            return
        }
        val c = currentBest
        if (c == null || c.second > distance) {
            val new = nextState to distance
            toVisit.offer(new)
            loggingOp { "adding $nextState with distance $distance" }
        } else loggingOp { "skipping $nextState with distance $distance, we got better result already" }
    }

    private fun done(nextState: R) {
        val distance = distanceOp(nextState)
        val c = currentBest
        if (c == null || c.second > distance) {
            currentBest = nextState to distance
            loggingOp { "FOUND $nextState with distance $distance" }
        } else loggingOp { "skipping found $nextState with distance $distance, we got better result already" }
    }

    private fun pick(): R {
        val (r, i) = toVisit.poll()
        return r
    }

    private var currentBest: Pair<R, I>? = null

    private val toVisit = PriorityQueue(priority)

}

class BasicPathfinder<T : Any, I : Comparable<I>>(
    loggingOp: (() -> Any) -> Unit = {},
    adderOp: (Set<T>, T) -> Set<T> = { l, t -> l + t },
    distanceOp: ((Set<T>) -> I),
    waysOutOp: (Set<T>) -> Iterable<T>,
    private val cache: Cache<T, I> = Cache(),
) : BFSPathfinder<T, Set<T>, I>(
    loggingOp = loggingOp,
    adderOp = adderOp,
    distanceOp = distanceOp,
    waysOutOp = { l -> waysOutOp(l).filter { it !in l } },
    meaningfulOp = { l, d -> cache.isBetterThanPrevious(l.last(), d) }
) {
//    fun findShortest(start: T, end: T): List<T>? = findShortest(listOf(start)) { t -> t.last() == end }
}

class Cache<R : Any, I : Comparable<I>> {
    private val cache = mutableMapOf<R, I>()
    fun isBetterThanPrevious(state: R, distance: I): Boolean {
        val previous = cache[state]
        return when {
            previous != null && previous <= distance -> {
                false
            }
            else -> {
                cache[state] = distance
                true
            }
        }
    }
}
