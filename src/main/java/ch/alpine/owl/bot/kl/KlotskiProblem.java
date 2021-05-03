// code by jph
package ch.alpine.owl.bot.kl;

import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.tensor.Tensor;

/* package */ interface KlotskiProblem {
  /** @return list of stones with type and initial position */
  Tensor startState();

  /** @return vector of length 2 */
  Tensor size();

  /** @return */
  StateTimeRaster stateTimeRaster();

  /** @return vector of length 3 */
  Tensor getGoal();

  Tensor frame();

  Tensor getBorder();

  String name();
}
