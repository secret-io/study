package item6;

import org.junit.Test;

public class AutoBoxing {

    //sum이 Long 타입이면 9s 460ms / long 타입이면 835ms

    @Test
    public void test() {
        Long sum = 0L;

        for (long i = 0; i < Integer.MAX_VALUE; i++) {
            sum += i;
        }

        long end = System.currentTimeMillis();
    }

}
