package beast.evolution.substitutionmodel;

import beast.core.Citation;
import beast.core.Description;
import beast.core.Input;
import beast.core.parameter.RealParameter;

/**
 * @author Chi Zhang
 */

@Description("")
public class MinorOrdinal extends NStatesNoRatesSubstitutionModel {

    public final Input<RealParameter> bInput = new Input<>("b", "nonadjacent state change relative rate. b=0:ordered; b>0:unordered");

    @Override
    protected void setupRelativeRates() {

        /* A relative rate matrix for a model of this class looks as follows (for, e.g. 5 states)
         * [   1 b b b ]
         * [ 1   1 b b ]
         * [ b 1   1 b ]
         * [ b b 1   1 ]
         * [ b b b 1   ]
         */

        double b = 1.0; // equal relative rates if no input
        if (bInput.get() != null) {
            b = bInput.get().getValue();
            if (b < 0.0)
                throw new RuntimeException("relative rate b should be positive.");
        }

        for (int i = 0; i < nrOfStates - 1; i++) {
            for (int j = 0; j < nrOfStates; j++) {
                if (j == 0 || j == nrOfStates - 1)
                    relativeRates[nrOfStates * i + j] = 1.0; // adjacent states
                else
                    relativeRates[nrOfStates * i + j] = b; // nonadjacent states
            }
        }
    }
}
