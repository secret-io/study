package item14;

import java.util.Comparator;

public class Stage implements Comparable<Stage> {

    private final int index;
    private final int finishedUsers;
    private final double failureRate;
    private PhoneNumber pn;

    public Stage(int index, int finishedUsers, double failureRate) {
        this.index = index;
        this.finishedUsers = finishedUsers;
        this.failureRate = failureRate;
    }

    public int getIndex() {
        return index;
    }

    public int getFinishedUsers() {
        return finishedUsers;
    }

    public double getFailureRate() {
        return failureRate;
    }

    private static final Comparator<Stage> COMPARATOR =  Comparator
            .comparingDouble((Stage s) -> s.failureRate)
//            .thenComparing(s -> s.pn, Comparator.reverseOrder())
//            .thenComparing(s -> s.pn)
//            .thenComparing(Comparator.reverseOrder())
            .thenComparingInt(s -> -s.finishedUsers)
            .thenComparingInt(s -> s.index);

    private static final Comparator<Stage> COMPARATOR2 =  Comparator.
            comparingDouble(Stage::getFailureRate)
            .thenComparing(Stage::getFinishedUsers, Comparator.reverseOrder())
            .thenComparingInt(Stage::getIndex);

    @Override
    public int compareTo(Stage o) {
        return COMPARATOR.compare(this, o);
    }

}


