package task2;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

class Job2Test {

    private Job2 instance;

    @BeforeEach
    void init() {
        instance = new Job2();
    }

    @AfterEach
    public void finish() {
        instance = null;
    }

    @ParameterizedTest
    @MethodSource("dataForWhetherArrayContainsOnly4and1IsTrue")
    public void testWhetherArrayContainsOnly4and1IsTrue(int[] input) {
        Assertions.assertTrue(instance.whetherArrayContainsOnly4and1(input), Arrays.toString(input));
    }

    public static Stream<Arguments> dataForWhetherArrayContainsOnly4and1IsTrue() {
        List<Arguments> out = new ArrayList<>();
        out.add(Arguments.arguments((Object) new int[]{1, 1, 1, 4, 4, 1, 4, 1}));
        out.add(Arguments.arguments((Object) new int[]{4, 4, 1, 4, 1, 1, 4, 1, 1}));
        out.add(Arguments.arguments((Object) new int[]{1, 1, 4, 4, 4, 1, 4, 4, 1, 1, 1, 4}));
        return out.stream();
    }

    @ParameterizedTest
    @MethodSource("dataWhetherArrayContainsOnly4and1IsFalse")
    public void testWhetherArrayContainsOnly4and1IsFalse(int[] input) {
        Assertions.assertFalse(instance.whetherArrayContainsOnly4and1(input), Arrays.toString(input));
    }

    public static Stream<Arguments> dataWhetherArrayContainsOnly4and1IsFalse() {
        List<Arguments> out = new ArrayList<>();
        out.add(Arguments.arguments((Object) new int[]{2, 3, 6, 7, 0, 7, 9, 5}));
        out.add(Arguments.arguments((Object) new int[]{4, 4, 1, 4, 1, 1, 4, 0, 1}));
        out.add(Arguments.arguments((Object) new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4}));
        out.add(Arguments.arguments((Object) new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}));
        return out.stream();
    }

}
