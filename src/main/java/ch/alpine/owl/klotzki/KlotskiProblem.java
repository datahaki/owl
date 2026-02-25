// code by jph
package ch.alpine.owl.klotzki;

import java.io.Serializable;

import ch.alpine.owlets.glc.core.StateTimeRaster;
import ch.alpine.tensor.Tensor;

record KlotskiProblem( //
    Tensor startState, String name, StateTimeRaster stateTimeRaster, Tensor size, Tensor goal, Tensor frame, Tensor border) //
    implements Serializable {
}
