package org.arcanum.field;

import org.arcanum.AbstractArcanumTest;
import org.arcanum.Element;
import org.arcanum.ElementPowPreProcessing;
import org.arcanum.Field;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

/**
 * @author Angelo De Caro (arcanumlib@gmail.com)
 */
public class ElementPowPreProcessingTest extends AbstractArcanumTest {

    protected int fieldIdentifier;
    protected Field field;


    @Parameterized.Parameters
    public static Collection parameters() {
        Object[][] data = {
                {false, "org/arcanum/pairing/a/a_181_603.properties", 1},
                {false, "org/arcanum/pairing/a/a_181_603.properties", 2},
                {false, "org/arcanum/pairing/a/a_181_603.properties", 3},
                {false, "org/arcanum/pairing/a/a_181_603.properties", 0},

                {false, "org/arcanum/pairing/a1/a1_3primes.properties", 1},
                {false, "org/arcanum/pairing/a1/a1_3primes.properties", 2},
                {false, "org/arcanum/pairing/a1/a1_3primes.properties", 3},
                {false, "org/arcanum/pairing/a1/a1_3primes.properties", 0},

                {false, "org/arcanum/pairing/d/d_9563.properties", 1},
                {false, "org/arcanum/pairing/d/d_9563.properties", 2},
                {false, "org/arcanum/pairing/d/d_9563.properties", 3},
                {false, "org/arcanum/pairing/d/d_9563.properties", 0},

                {false, "org/arcanum/pairing/e/e.properties", 1},
                {false, "org/arcanum/pairing/e/e.properties", 2},
                {false, "org/arcanum/pairing/e/e.properties", 3},
                {false, "org/arcanum/pairing/e/e.properties", 0},

                {false, "org/arcanum/pairing/f/f.properties", 1},
                {false, "org/arcanum/pairing/f/f.properties", 2},
                {false, "org/arcanum/pairing/f/f.properties", 3},
                {false, "org/arcanum/pairing/f/f.properties", 0},

                {false, "org/arcanum/pairing/g/g149.properties", 1},
                {false, "org/arcanum/pairing/g/g149.properties", 2},
                {false, "org/arcanum/pairing/g/g149.properties", 3},
                {false, "org/arcanum/pairing/g/g149.properties", 0},

                {true, "org/arcanum/pairing/a/a_181_603.properties", 1},
                {true, "org/arcanum/pairing/a/a_181_603.properties", 2},
                {true, "org/arcanum/pairing/a/a_181_603.properties", 3},
                {true, "org/arcanum/pairing/a/a_181_603.properties", 0},

                {true, "org/arcanum/pairing/a1/a1_3primes.properties", 1},
                {true, "org/arcanum/pairing/a1/a1_3primes.properties", 2},
                {true, "org/arcanum/pairing/a1/a1_3primes.properties", 3},
                {true, "org/arcanum/pairing/a1/a1_3primes.properties", 0},

                {true, "org/arcanum/pairing/d/d_9563.properties", 1},
                {true, "org/arcanum/pairing/d/d_9563.properties", 2},
                {true, "org/arcanum/pairing/d/d_9563.properties", 3},
                {true, "org/arcanum/pairing/d/d_9563.properties", 0},

                {true, "org/arcanum/pairing/e/e.properties", 1},
                {true, "org/arcanum/pairing/e/e.properties", 2},
                {true, "org/arcanum/pairing/e/e.properties", 3},
                {true, "org/arcanum/pairing/e/e.properties", 0},

                {true, "org/arcanum/pairing/f/f.properties", 1},
                {true, "org/arcanum/pairing/f/f.properties", 2},
                {true, "org/arcanum/pairing/f/f.properties", 3},
                {true, "org/arcanum/pairing/f/f.properties", 0},

                {true, "org/arcanum/pairing/g/g149.properties", 1},
                {true, "org/arcanum/pairing/g/g149.properties", 2},
                {true, "org/arcanum/pairing/g/g149.properties", 3},
                {true, "org/arcanum/pairing/g/g149.properties", 0}
        };

        return Arrays.asList(data);
    }


    public ElementPowPreProcessingTest(boolean usePBC, String curvePath, int fieldIdentifier) {
        super(usePBC, curvePath);

        this.fieldIdentifier = fieldIdentifier;
    }

    @Before
    public void before() throws Exception {
        super.before();

        this.field = pairing.getFieldAt(fieldIdentifier);
    }

    @Test
    public void testPowPreProcessing() {
        Element base = field.newElement().setToRandom().getImmutable();

        BigInteger n = pairing.getZr().newElement().setToRandom().toBigInteger();

        Element r1 = base.getElementPowPreProcessing().pow(n);
        Element r2 = base.pow(n);

        assertTrue(r1.isEqual(r2));
    }

    @Test
    public void testPowPreProcessingZn() {
        Element base = field.newElement().setToRandom().getImmutable();

        Element n = pairing.getZr().newElement().setToRandom();

        Element r1 = base.getElementPowPreProcessing().powZn(n);
        Element r2 = base.powZn(n);

        assertTrue(r1.isEqual(r2));
    }

    @Test
    public void testPowPreProcessingBytes() {
        Element base = field.newElement().setToRandom().getImmutable();

        ElementPowPreProcessing ppp1 = base.getElementPowPreProcessing();
        ElementPowPreProcessing ppp2 = base.getField().getElementPowPreProcessingFromBytes(ppp1.toBytes());

        Element n = pairing.getZr().newElement().setToRandom();

        Element r1 = ppp1.powZn(n);
        Element r2 = ppp2.powZn(n);

        assertTrue(r1.isEqual(r2));
    }

}
