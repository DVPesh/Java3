package task1;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class Job1Test {

    private Job1 instance;

    @BeforeEach
    public void init() {
        instance = new Job1();
    }

    @AfterEach
    public void finish() {
        instance = null;
    }

    @Test
    public void testExtractAfterTheLastFourIfFourDoesNotExist() {
        Assertions.assertThrowsExactly(RuntimeException.class, () -> instance.extractAfterTheLastFour(new int[]{-7, 1}));
    }

    @ParameterizedTest
    @MethodSource("dataForExtractAfterTheLastFourIfFourExists")
    public void testExtractAfterTheLastFourIfFourExists(int[] input, int[] output) {
        Assertions.assertArrayEquals(output, instance.extractAfterTheLastFour(input));
    }

    public static Stream<Arguments> dataForExtractAfterTheLastFourIfFourExists() {
        List<Arguments> out = new ArrayList<>();
        out.add(Arguments.arguments(new int[]{2, -4, 6, 8, 4, 1, 1, 0, 4}, new int[]{}));
        out.add(Arguments.arguments(new int[]{2, -4, 6, 8, 4, 4, 1, 0, 3}, new int[]{1, 0, 3}));
        out.add(Arguments.arguments(new int[]{4, -4, 6, 8, 4, 1, 1, 0, 7}, new int[]{1, 1, 0, 7}));
        return out.stream();
    }

}
