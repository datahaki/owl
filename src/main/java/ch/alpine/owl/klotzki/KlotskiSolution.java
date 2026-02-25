// code by jph
package ch.alpine.owl.klotzki;

import java.io.Serializable;
import java.util.List;

import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.tensor.Tensor;

record KlotskiSolution(KlotskiProblem klotskiProblem, List<StateTime> list, Tensor domain) //
    implements Serializable {
}
