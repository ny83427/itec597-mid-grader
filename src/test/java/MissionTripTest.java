import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class MissionTripTest {

    private int[][] from(String file) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(getClass().getResource("/" + file).toURI()));
            int[][] cells = new int[lines.size()][];
            for (int i = 0; i < cells.length; i++) {
                cells[i] = Stream.of(lines.get(i).split("\\s+")).mapToInt(Integer::valueOf).toArray();
            }
            return cells;
        } catch (IOException | URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    void resolve() {
        MissionTrip mt = new MissionTrip();
        assertTimeoutPreemptively(Duration.ofSeconds(2), () ->
            assertEquals(12, mt.resolve(from("q4-case1"))));
        assertTimeoutPreemptively(Duration.ofSeconds(2), () ->
            assertEquals(0, mt.resolve(from("q4-case2"))));
        assertTimeoutPreemptively(Duration.ofSeconds(2), () ->
            assertEquals(0, mt.resolve(from("q4-case3"))));
        assertTimeoutPreemptively(Duration.ofSeconds(2), () ->
            assertEquals(4, mt.resolve(from("q4-case4"))));
        assertTimeoutPreemptively(Duration.ofSeconds(2), () ->
            assertEquals(8512, mt.resolve(from("q4-case5"))));
    }
}
