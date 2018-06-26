package beast.evolution.substitutionmodel;

import beast.core.Citation;
import beast.core.Description;
import beast.core.Input;
import beast.core.Input.Validate;
import beast.core.parameter.RealParameter;
import beast.evolution.tree.Node;

import java.util.Arrays;

/**
 * @author Alexandra Gavryushkina
 * @author Chi Zhang
 */
@Description("Extension to LewisMK substitution model")
public class LewisMkG extends LewisMK {
    public final Input<Boolean> orderedInput = new Input<>("ordered", "Whether the states are ordered (default: unordered).", false);
    public final Input<RealParameter> bInput = new Input<>("b", "nonadjacent state change rate parameter", Validate.XOR, orderedInput);

    // substitution rate matrix (Q)
    private double[][] rateMatrix;
    private boolean updateMatrix;

    private void setRateMatrix() {
        frequencies = frequenciesInput.get().getFreqs();
        if (frequencies.length != nrOfStates)
            throw new RuntimeException("number of stationary frequencies does not match number of states.");

        double b = 1.0;
        if (bInput.get() != null) {
            b = bInput.get().getValue();
            if (b < 0.0)
                throw new RuntimeException("relative rate b should be positive.");
        }
        else if (orderedInput.get())
            b = 0.0;

        for (int i = 0; i < nrOfStates; i++) {
            for (int j = i + 1; j < nrOfStates; j++) {
                rateMatrix[i][j] = frequencies[j];
                rateMatrix[j][i] = frequencies[i];
                if (j - i > 1) {
                    rateMatrix[i][j] *= b;
                    rateMatrix[j][i] *= b;
                }
            }
        }
        // set up diagonal
        for (int i = 0; i < nrOfStates; i++) {
            double sum = 0.0;
            for (int j = 0; j < nrOfStates; j++) {
                if (i != j)
                    sum += rateMatrix[i][j];
            }
            rateMatrix[i][i] = -sum;
        }
        // normalise rate matrix to one expected substitution per unit time
        double subst = 0.0;
        for (int i = 0; i < nrOfStates; i++)
            subst += -rateMatrix[i][i] * frequencies[i];
        for (int i = 0; i < nrOfStates; i++) {
            for (int j = 0; j < nrOfStates; j++) {
                rateMatrix[i][j] = rateMatrix[i][j] / subst;
            }
        }
    }

    @Override
    public void initAndValidate() {
        if (nrOfStatesInput.get() != null) {
            nrOfStates = nrOfStatesInput.get();
        } else {
            nrOfStates = dataTypeInput.get().getStateCount();
        }

        frequencies = new double[nrOfStates];
        rateMatrix = new double[nrOfStates][nrOfStates];

        setRateMatrix();
    }


    @Override
    public void getTransitionProbabilities(Node node, double fStartTime, double fEndTime, double fRate, double[] matrix) {
        if (updateMatrix) {
            setRateMatrix();
            updateMatrix = false;
        }




    }


    protected boolean requiresRecalculation() {
        // we only get here if something is dirty
        updateMatrix = true;
        return true;
    }
}
