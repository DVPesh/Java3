package task2;

public class Job2 {

    public boolean whetherArrayContainsOnly4and1(int[] array) {
        boolean contains4 = false;
        boolean contains1 = false;

        for (int i : array) {
            switch (i) {
                case 4:
                    contains4 = true;
                    break;
                case 1:
                    contains1 = true;
                    break;
                default:
                    return false;
            }
        }
        return contains1 && contains4;
    }

}
