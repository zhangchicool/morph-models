package morphmodels.evolution.substitutionmodel;

import beast.core.Citation;
import beast.core.Description;

/**
 * @author Chi Zhang
 */

@Description("")
public class MinorOrdinal extends NStatesNoRatesSubstitutionModel {

    @Override
    protected void setupRelativeRates() {

        /* A relative rate matrix for a model of this class looks as follows (for, e.g. 5 states)
         * [   1 b b b ]
         * [ 1   1 b b ]
         * [ b 1   1 b ]
         * [ b b 1   1 ]
         * [ b b b 1   ]
         *
         * nonadjacent relative rate b = 0 : ordered; b > 0 : unordered */

        double rate = 1.0; // equal relative rates by default
        if (ratesInput.get() != null) {
            rate = ratesInput.get().getArrayValue();
            if (rate < 0.0)
                throw new RuntimeException("relative rate should be positive.");
        }

        for (int i = 0; i < nrOfStates - 1; i++) {
            for (int j = 0; j < nrOfStates; j++) {
                if (j == 0 || j == nrOfStates - 1)
                    relativeRates[nrOfStates * i + j] = 1.0; // adjacent states
                else
                    relativeRates[nrOfStates * i + j] = rate; // nonadjacent states
            }
        }
    }
}
