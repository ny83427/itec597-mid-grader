import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class BibleScholarTest {

    @Test
    void resolve() {
        assertTimeoutPreemptively(Duration.ofSeconds(3), () ->
            assertArrayEquals(new String[]{
                "lord:7830",
                "god:4442",
                "said:3999",
                "man:2613",
                "israel:2565",
                "son:2370",
                "king:2258",
                "people:2139",
                "came:2093",
                "house:2024",
                "come:1971",
                "one:1967",
                "abi:1",
                "abhorring:1",
                "abez:1",
                "abelshittim:1",
                "abelmizraim:1",
                "abelmaim:1",
                "abdiel:1",
                "abdeel:1",
                "abasing:1",
                "abana:1",
                "abagtha:1",
                "abaddon:1"
            }, new BibleScholar().resolve()));
    }

}
