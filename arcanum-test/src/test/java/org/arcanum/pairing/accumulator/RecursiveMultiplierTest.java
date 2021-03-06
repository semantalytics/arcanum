package org.arcanum.pairing.accumulator;


import org.arcanum.AbstractArcanumTest;
import org.arcanum.Element;
import org.arcanum.common.concurrent.RecursiveMultiplier;
import org.junit.Test;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ForkJoinPool;

import static org.junit.Assert.assertEquals;

/**
 * @author Angelo De Caro (arcanumlib@gmail.com)
 * @since 1.0.0
 */
public class RecursiveMultiplierTest extends AbstractArcanumTest {

    @Parameterized.Parameters
    public static Collection parameters() {
        Object[][] data = {
                {false, "org/arcanum/pairing/a/a_181_603.properties"},
        };

        return Arrays.asList(data);
    }


    public RecursiveMultiplierTest(boolean usePBC, String curvePath) {
        super(usePBC, curvePath);
    }


    @Test
    public void testRecursiveMultiplier() {
        int n = 10000;
        Element elements[] = new Element[n];

        for (int i = 0; i < n; i++) {
            elements[i] = pairing.getGT().newRandomElement();
        }

        Element product = pairing.getGT().newOneElement();
        for (int i = 0; i < n; i++) {
            product.mul(elements[i]);
        }

        // Test default
        ForkJoinPool pool = new ForkJoinPool();
        Element result = pool.invoke(new RecursiveMultiplier(elements, 0, n - 1));

        assertEquals(true, product.isEqual(result));
    }

}
