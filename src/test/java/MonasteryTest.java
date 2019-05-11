import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class MonasteryTest {

    private int[][] from(String file) {
        Scanner sc = new Scanner(getClass().getResourceAsStream("/" + file));
        int cols = sc.nextInt(), rows = sc.nextInt();
        sc.nextLine();

        int[][] cells = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = sc.nextInt();
            }
            sc.nextLine();
        }
        return cells;
    }

    private int[] ia(String s) {
        return Stream.of(s.split(", ")).mapToInt(Integer::valueOf).toArray();
    }

    private final String[] outputs = {
        "5, 9, 16, 4, 1, 4",
        "2, 1, 2, 1, 1, 4",
        "3, 9, 17, 4, 1, 2",
        "27, 55, 85, 11, 11, 4",
        "9, 36, 41, 6, 10, 2",
        "2, 512, 1024, 32, 16, 4",
        "2500, 1, 2, 50, 1, 2",
        "306, 905, 1233, 37, 18, 2"
    };

    @Test
    void resolve() {
        Monastery m = new Monastery();
        for (int i = 0; i < outputs.length; i++) {
            final int index = i;
            final String tc = "q2-case" + (index + 1);
            assertTimeoutPreemptively(Duration.ofSeconds(2), () ->
                assertArrayEquals(ia(outputs[index]),
                    m.resolve(from(tc)), tc + " FAILED!"), tc + " TLE!");
        }
    }

    @Disabled
    void generate() {
        Monastery monastery = new Monastery();
        for (int i = 1; i <= 8; i++) {
            String s = Arrays.toString(monastery.resolve(from("q2-case" + i)));
            System.out.println(s.substring(1, s.length() - 1));
        }
    }

}
