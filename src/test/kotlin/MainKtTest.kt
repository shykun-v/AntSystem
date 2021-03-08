import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class MainKtTest {

    @Test
    fun simpleTest() {
        val distance = calculateDistance(Node(1, 7.0, 4.0), Node(2, 17.0, 6.0))
        assertEquals(10.198039027185569, distance)
    }

}