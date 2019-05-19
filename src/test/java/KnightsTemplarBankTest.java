import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class KnightsTemplarBankTest {

    @Test
    void resolve() {
        KnightsTemplarBank kb = new KnightsTemplarBank();
        assertTimeoutPreemptively(Duration.ofSeconds(2), () ->
            assertArrayEquals(new int[]{134235101, 400}, kb.resolve(10000, new int[]{1, 5, 10, 25})));
        assertTimeoutPreemptively(Duration.ofSeconds(2), () ->
            assertArrayEquals(new int[]{-1, -1}, kb.resolve(3, new int[]{2, 5, 10, 25})));
        assertTimeoutPreemptively(Duration.ofSeconds(2), () ->
            assertArrayEquals(new int[]{19, 20}, kb.resolve(6249, new int[]{186, 419, 83, 408})));
    }

}
