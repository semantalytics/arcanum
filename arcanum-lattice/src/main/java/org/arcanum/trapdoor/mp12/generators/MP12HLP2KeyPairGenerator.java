package org.arcanum.trapdoor.mp12.generators;

import org.arcanum.Element;
import org.arcanum.Field;
import org.arcanum.Matrix;
import org.arcanum.Sampler;
import org.arcanum.common.cipher.params.ElementKeyPairParameters;
import org.arcanum.field.vector.MatrixField;
import org.arcanum.field.vector.TwoByColumnMatrixElement;
import org.arcanum.field.vector.VectorField;
import org.arcanum.sampler.SamplerFactory;
import org.arcanum.trapdoor.mp12.params.MP12HLP2KeyPairGenerationParameters;
import org.arcanum.trapdoor.mp12.params.MP12HLP2PrivateKeyParameters;
import org.arcanum.trapdoor.mp12.params.MP12HLP2PublicKeyParameters;
import org.arcanum.trapdoor.mp12.params.MP12PLP2PublicKeyParameters;
import org.arcanum.trapdoor.mp12.utils.MP12P2Utils;

import java.math.BigInteger;

/**
 * Computational instantiation.
 *
 * @author Angelo De Caro (arcanumlib@gmail.com)
 */
public class MP12HLP2KeyPairGenerator extends MP12PLP2KeyPairGenerator<MP12HLP2KeyPairGenerationParameters> {
    private MP12HLP2KeyPairGenerationParameters params;

    private int barM, w, m, mInBytes;
    private Field inputField, outputField;
    private Sampler<BigInteger> hlZSampler;

    public MP12HLP2KeyPairGenerator init(MP12HLP2KeyPairGenerationParameters keyGenerationParameters) {
        this.params = keyGenerationParameters;

        super.init(params);

        this.barM = 2 * n;
        this.w = n * k;
        this.m = barM + w;
        this.mInBytes = (m + 7) / 8;

        this.inputField = new VectorField<Field>(params.getRandom(), Zq, n);
        this.outputField = new VectorField<Field>(params.getRandom(), Zq, m);

        this.hlZSampler = SamplerFactory.getInstance().getDiscreteGaussianSampler(
                random,
                MP12P2Utils.getLWENoiseParameter(n, params.getParameters().getRandomizedRoundingParameter())
        );

        return this;
    }

    public ElementKeyPairParameters generateKeyPair() {
        super.generateKeyPair();

        // Construct Parity-check matrix

        // 1. Choose barA random in Z_q[n x barM]
        MatrixField<Field> hatAField = new MatrixField<Field>(random, Zq, n);
        Matrix hatA = hatAField.newRandomElement();
        Matrix barA = new TwoByColumnMatrixElement(hatAField.newElementIdentity(), hatA);

        // 2. Sample R from Z[barM x w] using distribution D
        MatrixField<Field> rField = new MatrixField<Field>(random, Zq, barM, w);
        Matrix R = rField.newElementFromSampler(hlZSampler);

        // 3. Compute G - barA R
        Matrix A1 = ((Matrix) barA.mul(R)).transform(new Matrix.Transformer() {
            public void transform(int row, int col, Element e) {
                e.negate();
                if (!G.isZeroAt(row, col))
                    e.add(G.getAt(row, col));
            }
        });

        Matrix A = new TwoByColumnMatrixElement(barA, A1);

        return new ElementKeyPairParameters(
                new MP12HLP2PublicKeyParameters(
                        params.getParameters(),
                        (MP12PLP2PublicKeyParameters) keyPair.getPublic(),
                        A, m
                ),
                new MP12HLP2PrivateKeyParameters(params.getParameters(), R)
        );
    }

    public int getMInBytes() {
        return mInBytes;
    }

    public Field getInputField() {
        return inputField;
    }

    public Field getOutputField() {
        return outputField;
    }

}
