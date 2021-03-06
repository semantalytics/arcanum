package org.arcanum.fe.abe.bns14.generators;

import org.arcanum.Element;
import org.arcanum.common.cipher.engine.ElementCipher;
import org.arcanum.common.fe.generator.SecretKeyGenerator;
import org.arcanum.fe.abe.bns14.params.BNS14MasterSecretKeyParameters;
import org.arcanum.fe.abe.bns14.params.BNS14PublicKeyParameters;
import org.arcanum.fe.abe.bns14.params.BNS14SecretKeyParameters;
import org.arcanum.program.circuit.ArithmeticCircuit;
import org.arcanum.program.circuit.ArithmeticGate;
import org.bouncycastle.crypto.CipherParameters;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Angelo De Caro (arcanumlib@gmail.com)
 */
public class BNS14SecretKeyGenerator extends SecretKeyGenerator<BNS14PublicKeyParameters, BNS14MasterSecretKeyParameters, ArithmeticCircuit> {

    public CipherParameters generateKey(ArithmeticCircuit circuit) {
        // encode the circuit
        Map<Integer, Element> keys = new HashMap<Integer, Element>();

        ElementCipher solver = publicKey.getParameters().getFactory().newPrimitiveMatrixSolver();

        for (ArithmeticGate gate : circuit) {
            int index = gate.getIndex();

            switch (gate.getType()) {
                case INPUT:
                    keys.put(index, publicKey.getBAt(index));
                    break;

                case OR:
                    // addition
                    Element B = publicKey.getBAt(0).getField().newZeroElement();
                    for (int j = 0, k = gate.getNumInputs(); j < k; j++) {
                        // \alpha_i G
                        Element R = solver.processElements(
                                (gate.getAlphaAt(j).isOne()) ?
                                        publicKey.getPrimitiveLatticePk().getG() :
                                        publicKey.getPrimitiveLatticePk().getG().duplicate().mulZn(gate.getAlphaAt(j))
                        );
                        B.add(keys.get(gate.getInputIndexAt(j)).mul(R));
                    }

                    keys.put(index, B);
                    break;

                case AND:
                    // multiplication

                    // Compute R_0 = SolveR(G, T_G, \alpha G)
                    Element R = solver.processElements(
                            (gate.getAlphaAt(0).isOne()) ?
                                    publicKey.getPrimitiveLatticePk().getG() :
                                    publicKey.getPrimitiveLatticePk().getG().duplicate().mulZn(gate.getAlphaAt(0))
                    );
                    for (int j = 1, k = gate.getNumInputs(); j < k; j++) {
                        // R_j = SolveR(G, T_G, - B_{j-1} R_{j-1})
                        R = solver.processElements(keys.get(gate.getInputIndexAt(j - 1)).mul(R).negate());
                    }

                    // Compute B_g = B_{k-1} R_{k-1}
                    keys.put(index, keys.get(gate.getInputIndexAt(gate.getNumInputs() - 1)).mul(R));
                    break;
            }
        }

        circuit.getOutputGate().putAt(-1, keys.get(circuit.getOutputGate().getIndex()));

        // SampleLeft
        Element skC = publicKey.getParameters().getFactory().createMatrixLeftSampler(secretKey.getLatticeSk()).processElements(
                keys.get(circuit.getOutputGate().getIndex()),
                publicKey.getD()
        );

//        Element F = MatrixField.unionByCol(publicKey.getLatticePk().getA(), keys.get(circuit.getOutputGate().getIndex()));
//        Element DPrime = F.mul(skC);
//        assertTrue(DPrime.equals(publicKey.getD()));

        return new BNS14SecretKeyParameters(publicKey, circuit, skC);
    }

}