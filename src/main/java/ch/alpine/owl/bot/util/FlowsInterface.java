// code by jph
package ch.alpine.owl.bot.util;

import java.util.Collection;

import ch.alpine.tensor.Tensor;

@FunctionalInterface
public interface FlowsInterface {
  /** Example:
   * The interface is implemented by classes in order to provide
   * the available/allowed controls of an entity to a motion planner.
   * 
   * @param resolution
   * @return collection with size roughly proportional to given resolution */
  Collection<Tensor> getFlows(int resolution);
}
