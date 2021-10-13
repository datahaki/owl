// code by jph
package ch.alpine.owl.rrts.core;

import ch.alpine.owl.bot.rn.RnTransitionSpace;
import ch.alpine.owl.bot.se2.rrts.ClothoidTransitionSpace;
import ch.alpine.owl.bot.se2.rrts.DubinsTransitionSpace;
import ch.alpine.tensor.Tensor;

/** TransitionSpace is a factory for {@link Transition}s
 * 
 * An instance of TransitionSpace is immutable.
 * 
 * Examples:
 * @see RnTransitionSpace
 * @see DubinsTransitionSpace
 * @see ClothoidTransitionSpace */
@FunctionalInterface
public interface TransitionSpace {
  /** @param head state
   * @param tail state
   * @return transition that represents the (unique) connection between the start and end state */
  Transition connect(Tensor head, Tensor tail);
}
