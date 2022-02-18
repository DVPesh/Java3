package task1;

import java.util.Arrays;

public class Job1 {

    public int[] extractAfterTheLastFour(int[] array) {
        for (int i = array.length - 1; i >= 0; --i) {
            if (array[i] == 4) {
                return Arrays.copyOfRange(array, i + 1, array.length);
            }
        }
        throw new RuntimeException("нет ни одной четвёрки");
    }

}
