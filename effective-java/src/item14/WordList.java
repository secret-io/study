package item14;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class WordList {

    public static void main(String[] args) {
        Set<String> s = new TreeSet<>();
        Collections.addAll(s, args);
        System.out.println(s);

        Stage s1 = new Stage(1, 3, 0.3);
        Stage s2 = new Stage(2, 3, 0.5);
        System.out.println(s1.compareTo(s2));

        s1 = new Stage(1, 5, 0.5);
        s2 = new Stage(2, 3, 0.5);
        System.out.println(s1.compareTo(s2));

        s1 = new Stage(1, 3, 0.5);
        s2 = new Stage(2, 3, 0.5);
        System.out.println(s1.compareTo(s2));

    }

}
